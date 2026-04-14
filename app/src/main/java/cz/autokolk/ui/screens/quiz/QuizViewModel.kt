package cz.autokolk.ui.screens.quiz

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cz.autokolk.AchievementsManager
import cz.autokolk.LessonProgress
import cz.autokolk.Question
import cz.autokolk.HeartRefillJobService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class QuizUiState(
    val questions: List<Question> = emptyList(),
    val index: Int = 0,
    val awaitingAdvance: Boolean = false,
    val lastWasCorrect: Boolean? = null,
    val comboStreak: Int = 0,
    val shakeToken: Int = 0,
    val testRemainingMs: Long? = null,
    val showQuitDialog: Boolean = false,
)

sealed class QuizFinish {
    data class Lesson(
        val lessonId: Int,
        val score: Int,
        val total: Int,
        val isReview: Boolean,
    ) : QuizFinish()

    data class Test(
        val score: Int,
        val total: Int,
    ) : QuizFinish()
}

class QuizViewModel(
    application: Application,
    private val lessonId: Int,
    private val isTest: Boolean,
    @Suppress("UNUSED_PARAMETER") private val categoryId: Int,
    private val isReview: Boolean,
) : AndroidViewModel(application) {

    private val lessonProgress = LessonProgress(application)

    private val _state = MutableStateFlow(QuizUiState())
    val state: StateFlow<QuizUiState> = _state.asStateFlow()

    private val _finish = MutableStateFlow<QuizFinish?>(null)
    val finish: StateFlow<QuizFinish?> = _finish.asStateFlow()

    private var timerJob: Job? = null
    private var hasCompleted: Boolean = false

    init {
        loadQuestions()
        if (isTest) {
            startTestTimer(45L * 60L * 1000L)
        }
    }

    fun clearFinish() {
        _finish.value = null
    }

    override fun onCleared() {
        timerJob?.cancel()
        super.onCleared()
    }

    private fun loadQuestions() {
        val qs = when {
            isTest -> {
                val pool = lessonProgress.getRandomQuestions(50)
                if (pool.size >= 10) pool else lessonProgress.getRandomQuestions(25)
            }
            lessonId > 0 -> lessonProgress.getQuestionsForLesson(lessonId)
            else -> emptyList()
        }
        _state.value = QuizUiState(questions = qs)
    }

    private fun startTestTimer(totalMs: Long) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var t = totalMs
            while (t > 0) {
                _state.update { it.copy(testRemainingMs = t) }
                delay(1000)
                t -= 1000
            }
            _state.update { it.copy(testRemainingMs = 0L) }
            completeTestMode()
        }
    }

    fun selectAnswer(key: String) {
        val s = _state.value
        if (!isTest && s.awaitingAdvance) return
        val q = s.questions.getOrNull(s.index) ?: return
        if (q.userAnswer != null && !isTest) return

        val norm = normalizeAnswerKey(key)
        q.userAnswer = norm
        val correct = norm == resolveCorrectKey(q)
        if (!isTest) {
            lessonProgress.recordMistakeStreak(q.id, correct)
            try {
                AchievementsManager(getApplication()).onAnswer(correct)
            } catch (_: Throwable) {
            }
        }
        if (!isTest && !isReview && lessonId > 0) {
            if (!correct) {
                val consumed = lessonProgress.consumeHeart()
                if (consumed) {
                    try {
                        HeartRefillJobService.scheduleNext(getApplication(), lessonProgress)
                    } catch (_: Throwable) {
                    }
                }
            }
        }
        val newCombo = if (correct) s.comboStreak + 1 else 0
        if (isTest) {
            _state.update {
                it.copy(
                    lastWasCorrect = correct,
                    comboStreak = newCombo,
                    shakeToken = if (!correct) it.shakeToken + 1 else it.shakeToken,
                )
            }
            return
        }
        _state.update {
            it.copy(
                awaitingAdvance = true,
                lastWasCorrect = correct,
                comboStreak = newCombo,
                shakeToken = if (!correct) it.shakeToken + 1 else it.shakeToken,
            )
        }
    }

    fun advance() {
        val s = _state.value
        if (isTest) {
            val last = s.questions.lastIndex
            if (s.index >= last) {
                completeTestMode()
            } else {
                _state.update {
                    it.copy(
                        index = it.index + 1,
                        lastWasCorrect = null,
                    )
                }
            }
            return
        }
        if (!s.awaitingAdvance) return
        val last = s.questions.lastIndex
        if (s.index >= last) {
            completeLessonMode()
            return
        }
        _state.update {
            it.copy(
                index = it.index + 1,
                awaitingAdvance = false,
                lastWasCorrect = null,
            )
        }
    }

    fun requestQuit() {
        _state.update { it.copy(showQuitDialog = true) }
    }

    fun dismissQuitDialog() {
        _state.update { it.copy(showQuitDialog = false) }
    }

    fun confirmQuit() {
        _state.update { it.copy(showQuitDialog = false) }
        if (isTest) completeTestMode() else completeLessonMode()
    }

    private fun completeTestMode() {
        if (hasCompleted) return
        val qs = _state.value.questions
        if (qs.isEmpty()) {
            hasCompleted = true
            _finish.value = QuizFinish.Test(score = 0, total = 50)
            return
        }
        var correct = 0
        for (q in qs) {
            val ok = normalizeAnswerKey(q.userAnswer) == resolveCorrectKey(q)
            lessonProgress.recordMistakeStreak(q.id, ok)
            if (ok) correct++
        }
        try {
            AchievementsManager(getApplication()).onTestCorrectAdded(correct)
        } catch (_: Throwable) {
        }
        try {
            lessonProgress.updateStreakOnLessonCompleted()
        } catch (_: Throwable) {
        }
        val weighted = (correct * 50 / qs.size.coerceAtLeast(1)).coerceIn(0, 50)
        try {
            if (weighted > 0) lessonProgress.addPoints(weighted)
        } catch (_: Throwable) {
        }
        try {
            lessonProgress.addTestScore(weighted, 50)
        } catch (_: Throwable) {
        }
        hasCompleted = true
        _finish.value = QuizFinish.Test(score = weighted, total = 50)
    }

    private fun completeLessonMode() {
        if (hasCompleted) return
        if (lessonId <= 0) {
            hasCompleted = true
            _finish.value = QuizFinish.Lesson(lessonId = -1, score = 0, total = 0, isReview = isReview)
            return
        }
        val qs = _state.value.questions
        val correctAnswers = qs.count { normalizeAnswerKey(it.userAnswer) == resolveCorrectKey(it) }
        val totalQuestions = qs.size
        if (!isReview) {
            val incorrectIds = qs.mapNotNull { q ->
                if (normalizeAnswerKey(q.userAnswer) != resolveCorrectKey(q)) q.id else null
            }.toSet()
            lessonProgress.saveLessonProgress(lessonId, incorrectIds)
        } else {
            val currentState = lessonProgress.getLessonState(lessonId)
            val remaining = currentState.incorrectQuestionIds.toMutableSet()
            qs.forEach { q ->
                if (normalizeAnswerKey(q.userAnswer) == resolveCorrectKey(q)) {
                    remaining.remove(q.id)
                }
            }
            lessonProgress.saveLessonProgress(lessonId, remaining)
        }
        try {
            lessonProgress.updateStreakOnLessonCompleted()
        } catch (_: Throwable) {
        }
        hasCompleted = true
        _finish.value = QuizFinish.Lesson(
            lessonId = lessonId,
            score = correctAnswers,
            total = totalQuestions,
            isReview = isReview,
        )
    }
}
