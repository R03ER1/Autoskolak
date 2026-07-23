package cz.autokolk.ui.screens.quiz

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cz.autokolk.AchievementsManager
import cz.autokolk.HeartRefillJobService
import cz.autokolk.InterstitialAdController
import cz.autokolk.LessonPoints
import cz.autokolk.LessonProgress
import cz.autokolk.Question
import cz.autokolk.XpRewardTable
import cz.autokolk.audio.SoundManager
import cz.autokolk.ui.util.HapticFeedback
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
    val showQuitDialog: Boolean = false,
    /** Před odkrytím správně/špatně (lesson mód). */
    val pendingAnswerKey: String? = null,
    val hearts: Int? = null,
    val showNoLivesOverlay: Boolean = false,
    val showCoinPopup: Boolean = false,
    /** Zobrazené „+N“ u odměny. */
    val coinPopupAmount: Int = 1,
    val eliminatedOptionKeys: Set<String> = emptySet(),
    val hintVisible: Boolean = false,
    val powerUpUsedThisQuestion: Boolean = false,
    val skippedQuestionIds: Set<String> = emptySet(),
)

sealed class QuizSession {
    data class Lesson(val lessonId: Int, val isReview: Boolean) : QuizSession()
    data class Practice(
        val categoryKey: String,
        val practiceMode: Int,
        val subcategoryKey: String,
        val focusQuestionId: String,
    ) : QuizSession()
}

sealed class QuizFinish {
    data class Lesson(
        val lessonId: Int,
        val score: Int,
        val total: Int,
        val isReview: Boolean,
        val firstOfDay: Boolean,
        val pointsAwarded: Int,
    ) : QuizFinish()

    data class Practice(
        val score: Int,
        val total: Int,
        val pointsAwarded: Int,
        val firstOfDay: Boolean,
        val replayCategory: String,
        val replayPracticeMode: Int,
        val replaySubKey: String,
        val replayFocusQuestionId: String,
    ) : QuizFinish()
}

class QuizViewModel(
    application: Application,
    private val session: QuizSession,
) : AndroidViewModel(application) {

    private val lessonProgress = LessonProgress(application)

    private val _state = MutableStateFlow(QuizUiState())
    val state: StateFlow<QuizUiState> = _state.asStateFlow()

    private val _finish = MutableStateFlow<QuizFinish?>(null)
    val finish: StateFlow<QuizFinish?> = _finish.asStateFlow()

    private var revealJob: Job? = null
    private var hasCompleted: Boolean = false
    private var maxComboThisLesson: Int = 0
    private val skippedQuestionIdSet = mutableSetOf<String>()

    private val practiceSeenFlags = mutableListOf<Boolean>()
    private var practiceAwardedBuckets: Int = 0
    private var practiceBucketPointsTotal: Int = 0

    init {
        when (session) {
            is QuizSession.Lesson -> loadLessonQuestions(session)
            is QuizSession.Practice -> loadPracticeQuestions(session)
        }
        refreshHeartsForSession()
        if (session is QuizSession.Practice) {
            onPracticeQuestionDisplayed(0)
        }
    }

    fun clearFinish() {
        _finish.value = null
    }

    fun dismissCoinPopup() {
        _state.update { it.copy(showCoinPopup = false) }
    }

    fun dismissNoLivesOverlay() {
        _state.update { it.copy(showNoLivesOverlay = false) }
    }

    fun goHomeFromNoLives() {
        _state.update { it.copy(showNoLivesOverlay = false) }
        confirmQuit()
    }

    fun onHeartRefilledFromAd() {
        _state.update {
            it.copy(
                showNoLivesOverlay = false,
                hearts = lessonProgress.getCurrentHearts(),
            )
        }
    }

    private fun refreshHeartsForSession() {
        when (session) {
            is QuizSession.Lesson -> _state.update { it.copy(hearts = lessonProgress.getCurrentHearts()) }
            is QuizSession.Practice -> _state.update { it.copy(hearts = null) }
        }
    }

    override fun onCleared() {
        revealJob?.cancel()
        super.onCleared()
    }

    private fun loadLessonQuestions(s: QuizSession.Lesson) {
        val qs = when {
            s.lessonId > 0 -> lessonProgress.getQuestionsForLesson(s.lessonId)
            else -> emptyList()
        }
        _state.value = QuizUiState(questions = qs, hearts = lessonProgress.getCurrentHearts())
    }

    private fun loadPracticeQuestions(s: QuizSession.Practice) {
        val qs = PracticeQuestionList.build(
            lessonProgress = lessonProgress,
            categoryKey = s.categoryKey,
            practiceMode = s.practiceMode,
            subcategoryFilter = s.subcategoryKey,
            focusQuestionId = s.focusQuestionId,
        )
        practiceSeenFlags.clear()
        practiceSeenFlags.addAll(List(qs.size) { false })
        practiceAwardedBuckets = 0
        practiceBucketPointsTotal = 0
        _state.value = QuizUiState(questions = qs, hearts = null)
    }

    private fun onPracticeQuestionDisplayed(index: Int) {
        if (session !is QuizSession.Practice) return
        if (index !in practiceSeenFlags.indices) return
        if (practiceSeenFlags[index]) return
        practiceSeenFlags[index] = true
        val seenCount = practiceSeenFlags.count { it }
        val buckets = seenCount / 5
        if (buckets > practiceAwardedBuckets) {
            val delta = buckets - practiceAwardedBuckets
            practiceAwardedBuckets = buckets
            practiceBucketPointsTotal += delta
            try {
                lessonProgress.addPoints(delta)
            } catch (_: Throwable) {
            }
            _state.update {
                it.copy(
                    showCoinPopup = true,
                    coinPopupAmount = delta.coerceAtLeast(1),
                )
            }
        }
    }

    fun selectAnswer(key: String) {
        val s = _state.value
        if (s.showNoLivesOverlay) return
        if (s.awaitingAdvance) return
        if (s.pendingAnswerKey != null) return
        val q = s.questions.getOrNull(s.index) ?: return
        if (q.userAnswer != null) return
        if (normalizeAnswerKey(key) in s.eliminatedOptionKeys) return

        val norm = normalizeAnswerKey(key)

        revealJob?.cancel()
        _state.update { it.copy(pendingAnswerKey = norm) }
        revealJob = viewModelScope.launch {
            delay(200)
            HapticFeedback.onTap(getApplication())
            delay(100)
            applyAnswer(q, norm)
            _state.update { it.copy(pendingAnswerKey = null) }
        }
    }

    private fun applyAnswer(q: Question, norm: String) {
        val s = _state.value
        q.userAnswer = norm
        val correct = norm == resolveCorrectKey(q)
        lessonProgress.recordMistakeStreak(q.id, correct)
        try {
            AchievementsManager(getApplication()).onAnswer(correct)
        } catch (_: Throwable) {
        }

        when (session) {
            is QuizSession.Practice -> {
                lessonProgress.savePracticeAnswer(session.categoryKey, q.id, correct)
            }
            is QuizSession.Lesson -> {
                if (!session.isReview && session.lessonId > 0 && !lessonProgress.hasInfiniteLives()) {
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
            }
        }

        val newCombo = if (correct) s.comboStreak + 1 else 0
        if (correct) {
            maxComboThisLesson = maxOf(maxComboThisLesson, newCombo)
            try {
                lessonProgress.incrementDailyCorrectAnswers()
            } catch (_: Throwable) {
            }
            if (session is QuizSession.Lesson) {
                try {
                    AchievementsManager(getApplication()).onAnswerTimeHints()
                } catch (_: Throwable) {
                }
                if (newCombo >= 10) {
                    try {
                        AchievementsManager(getApplication()).onLessonCombo10()
                    } catch (_: Throwable) {
                    }
                }
            }
        }
        val heartsNow = lessonProgress.getCurrentHearts()
        val isLesson = session is QuizSession.Lesson
        val lessonSession = session as? QuizSession.Lesson
        val outOfLives =
            isLesson && lessonSession != null &&
                !lessonSession.isReview && lessonSession.lessonId > 0 &&
                !lessonProgress.hasInfiniteLives() &&
                !correct &&
                heartsNow <= 0

        if (correct) {
            HapticFeedback.onCorrect(getApplication())
            SoundManager.play(SoundManager.Sound.CORRECT)
            // Combo streak zvuk / haptika: první milník od 3 správných v řadě.
            if (newCombo >= 3 && newCombo % 3 == 0) {
                HapticFeedback.onCombo(getApplication())
                SoundManager.play(SoundManager.Sound.COMBO)
            }
        } else {
            HapticFeedback.onWrong(getApplication())
            SoundManager.play(SoundManager.Sound.WRONG)
        }

        _state.update {
            it.copy(
                awaitingAdvance = true,
                lastWasCorrect = correct,
                comboStreak = newCombo,
                shakeToken = if (!correct) it.shakeToken + 1 else it.shakeToken,
                hearts = if (isLesson) heartsNow else null,
                showCoinPopup = correct && isLesson,
                coinPopupAmount = if (correct && isLesson) 1 else it.coinPopupAmount,
                showNoLivesOverlay = outOfLives,
            )
        }
    }

    fun advance() {
        val s = _state.value
        if (s.showNoLivesOverlay) return
        if (!s.awaitingAdvance) return
        val last = s.questions.lastIndex
        if (s.index >= last) {
            completeSession()
            return
        }
        val next = s.index + 1
        _state.update {
            it.copy(
                index = next,
                awaitingAdvance = false,
                lastWasCorrect = null,
                showCoinPopup = false,
                eliminatedOptionKeys = emptySet(),
                hintVisible = false,
                powerUpUsedThisQuestion = false,
            )
        }
        if (session is QuizSession.Practice) {
            onPracticeQuestionDisplayed(next)
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
        completeSession()
    }

    private fun completeSession() {
        if (hasCompleted) return
        when (session) {
            is QuizSession.Lesson -> completeLesson(session)
            is QuizSession.Practice -> completePractice(session)
        }
    }

    private fun completeLesson(session: QuizSession.Lesson) {
        if (session.lessonId <= 0) {
            hasCompleted = true
            _finish.value = QuizFinish.Lesson(
                lessonId = -1,
                score = 0,
                total = 0,
                isReview = session.isReview,
                firstOfDay = false,
                pointsAwarded = 0,
            )
            return
        }
        val qs = _state.value.questions
        val skipped = skippedQuestionIdSet.toSet()
        val qsActive = qs.filter { it.id !in skipped }
        val correctAnswers = qsActive.count { normalizeAnswerKey(it.userAnswer) == resolveCorrectKey(it) }
        val totalQuestions = qsActive.size.coerceAtLeast(1)
        val priorState = lessonProgress.getLessonState(session.lessonId)
        val wasFirstEverComplete = !priorState.completed
        if (!session.isReview) {
            val incorrectIds = qs.mapNotNull { q ->
                if (q.id in skipped) return@mapNotNull q.id
                if (normalizeAnswerKey(q.userAnswer) != resolveCorrectKey(q)) q.id else null
            }.toSet()
            lessonProgress.saveLessonProgress(session.lessonId, incorrectIds)
        } else {
            val currentState = lessonProgress.getLessonState(session.lessonId)
            val remaining = currentState.incorrectQuestionIds.toMutableSet()
            qs.forEach { q ->
                if (q.id in skipped) return@forEach
                if (normalizeAnswerKey(q.userAnswer) == resolveCorrectKey(q)) {
                    remaining.remove(q.id)
                }
            }
            skipped.forEach { id -> remaining.add(id) }
            lessonProgress.saveLessonProgress(session.lessonId, remaining)
        }
        val firstOfDay = try {
            lessonProgress.updateStreakOnLessonCompleted()
        } catch (_: Throwable) {
            false
        }
        try {
            lessonProgress.onDailyChallengeLessonsProgress()
        } catch (_: Throwable) {
        }
        val pointsAwarded = LessonPoints.computeLessonPointsAwarded(
            isPractice = false,
            isRandom = false,
            isReviewMode = session.isReview,
            correctAnswers = correctAnswers,
            totalQuestions = totalQuestions,
        )
        if (pointsAwarded > 0) {
            try {
                lessonProgress.addPoints(pointsAwarded)
            } catch (_: Throwable) {
            }
        }
        val comboMult = when {
            maxComboThisLesson >= 10 -> 1.1f
            maxComboThisLesson >= 5 -> 1.05f
            else -> 1f
        }
        try {
            val lessonXp = XpRewardTable.lessonXp(
                correctAnswers = correctAnswers,
                totalQuestions = totalQuestions,
                isReview = session.isReview,
            )
            lessonProgress.addXp(lessonXp, applyDoubleXpFromAds = true, sessionComboMultiplier = comboMult)
            if (firstOfDay) {
                lessonProgress.addXp(XpRewardTable.streakFirstLessonOfDay(), applyDoubleXpFromAds = true, sessionComboMultiplier = 1f)
            }
        } catch (_: Throwable) {
        }
        try {
            val perfect = totalQuestions > 0 && correctAnswers == totalQuestions && skipped.isEmpty()
            AchievementsManager(getApplication()).onLessonGamification(
                firstEverLessonComplete = wasFirstEverComplete && !session.isReview,
                perfectLesson = perfect && !session.isReview,
            )
        } catch (_: Throwable) {
        }
        // Interstitial reklamy: každá skutečná lekce (i review) zvedá počítadlo. Rozhodnutí
        // o zobrazení pak přebírá InterstitialAdController v ResultsComposeScreen.
        try {
            InterstitialAdController.onLessonCompleted(getApplication())
        } catch (_: Throwable) {
        }
        hasCompleted = true
        _finish.value = QuizFinish.Lesson(
            lessonId = session.lessonId,
            score = correctAnswers,
            total = totalQuestions,
            isReview = session.isReview,
            firstOfDay = firstOfDay,
            pointsAwarded = pointsAwarded,
        )
    }

    private fun completePractice(session: QuizSession.Practice) {
        val qs = _state.value.questions
        val correctAnswers = qs.count { normalizeAnswerKey(it.userAnswer) == resolveCorrectKey(it) }
        val totalQuestions = qs.size
        val firstOfDay = try {
            lessonProgress.updateStreakOnLessonCompleted()
        } catch (_: Throwable) {
            false
        }
        try {
            lessonProgress.addXp(8, applyDoubleXpFromAds = true, sessionComboMultiplier = 1f)
            lessonProgress.onDailyChallengeLessonsProgress()
        } catch (_: Throwable) {
        }
        hasCompleted = true
        _finish.value = QuizFinish.Practice(
            score = correctAnswers,
            total = totalQuestions,
            pointsAwarded = practiceBucketPointsTotal,
            firstOfDay = firstOfDay,
            replayCategory = session.categoryKey,
            replayPracticeMode = session.practiceMode,
            replaySubKey = session.subcategoryKey,
            replayFocusQuestionId = session.focusQuestionId,
        )
    }

    fun usePowerUpEliminate(): Boolean {
        val s = _state.value
        if (session !is QuizSession.Lesson || session.isReview) return false
        if (s.showNoLivesOverlay || s.awaitingAdvance || s.pendingAnswerKey != null) return false
        if (s.powerUpUsedThisQuestion) return false
        val q = s.questions.getOrNull(s.index) ?: return false
        if (q.userAnswer != null) return false
        val correct = resolveCorrectKey(q)
        val wrong = listOf("a", "b", "c").filter {
            it != correct && it !in s.eliminatedOptionKeys
        }
        if (wrong.isEmpty()) return false
        if (!lessonProgress.spendPoints(5)) return false
        val pick = wrong.random()
        _state.update {
            it.copy(
                eliminatedOptionKeys = it.eliminatedOptionKeys + pick,
                powerUpUsedThisQuestion = true,
            )
        }
        return true
    }

    fun usePowerUpSkip(): Boolean {
        val s = _state.value
        if (session !is QuizSession.Lesson || session.isReview) return false
        if (s.showNoLivesOverlay || s.awaitingAdvance || s.pendingAnswerKey != null) return false
        if (s.powerUpUsedThisQuestion) return false
        val q = s.questions.getOrNull(s.index) ?: return false
        if (q.id in skippedQuestionIdSet) return false
        if (!lessonProgress.spendPoints(10)) return false
        skippedQuestionIdSet.add(q.id)
        _state.update {
            it.copy(
                powerUpUsedThisQuestion = true,
                skippedQuestionIds = skippedQuestionIdSet.toSet(),
            )
        }
        goToNextQuestionOrFinish()
        return true
    }

    fun usePowerUpHint(): Boolean {
        val s = _state.value
        if (session !is QuizSession.Lesson || session.isReview) return false
        if (s.showNoLivesOverlay || s.awaitingAdvance || s.pendingAnswerKey != null) return false
        if (s.powerUpUsedThisQuestion) return false
        val q = s.questions.getOrNull(s.index) ?: return false
        if (q.userAnswer != null) return false
        if (!lessonProgress.spendPoints(3)) return false
        _state.update {
            it.copy(
                hintVisible = true,
                powerUpUsedThisQuestion = true,
            )
        }
        return true
    }

    private fun goToNextQuestionOrFinish() {
        val s = _state.value
        if (s.showNoLivesOverlay) return
        val last = s.questions.lastIndex
        if (s.index >= last) {
            completeSession()
            return
        }
        val next = s.index + 1
        _state.update {
            it.copy(
                index = next,
                awaitingAdvance = false,
                lastWasCorrect = null,
                showCoinPopup = false,
                eliminatedOptionKeys = emptySet(),
                hintVisible = false,
                powerUpUsedThisQuestion = false,
            )
        }
        if (session is QuizSession.Practice) {
            onPracticeQuestionDisplayed(next)
        }
    }

}
