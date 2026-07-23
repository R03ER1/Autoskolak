package cz.autokolk.ui.screens.quiz

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cz.autokolk.LessonProgress
import cz.autokolk.Question
import cz.autokolk.audio.SoundManager
import cz.autokolk.data.test.TestAttemptRepository
import cz.autokolk.ui.util.HapticFeedback
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

const val TEST_QUESTION_COUNT = 25
private val TEST_DURATION_MS = 30L * 60L * 1000L

enum class TestLoadState { Loading, Ready, InsufficientPool }

enum class TestRunPhase { Countdown, Running }

data class TestUiState(
    val loadState: TestLoadState = TestLoadState.Loading,
    val insufficientMessage: String? = null,
    val runPhase: TestRunPhase = TestRunPhase.Countdown,
    /** null = countdown hotov; 3..1 = číslice; 0 = „Start!“ */
    val countdownShow: Int? = 3,
    val questions: List<Question> = emptyList(),
    /** Body za otázku v pořadí [questions] (součet = 50). */
    val pointsPerQuestion: List<Int> = emptyList(),
    val examBlocks: List<OfficialExamBlock> = emptyList(),
    val index: Int = 0,
    val testRemainingMs: Long? = null,
    val showQuitDialog: Boolean = false,
    /** Odpovědi podle id otázky (Compose nemusí spoléhat na mutaci [Question.userAnswer]). */
    val answersByQuestionId: Map<String, String> = emptyMap(),
    /** Inkrementace při změně odpovědi. */
    val answerToken: Int = 0,
)

sealed class TestNavEvent {
    data class ToResults(val attemptId: Long) : TestNavEvent()
}

class TestViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private val lessonProgress = LessonProgress(application)
    private val testRepo = TestAttemptRepository.getInstance(application)

    private val _state = MutableStateFlow(TestUiState())
    val state: StateFlow<TestUiState> = _state.asStateFlow()

    private val _navEvent = MutableStateFlow<TestNavEvent?>(null)
    val navEvent: StateFlow<TestNavEvent?> = _navEvent.asStateFlow()

    private var timerJob: Job? = null
    private var hasCompleted = false

    init {
        viewModelScope.launch {
            testRepo.migrateLegacyScoresIfNeeded()
            val built = buildOfficialExamQuestionSet(lessonProgress)
            if (built == null) {
                _state.update {
                    it.copy(
                        loadState = TestLoadState.InsufficientPool,
                        insufficientMessage = "V databázi není dostatek otázek pro oficiální složení zkoušky (${TEST_QUESTION_COUNT} otázek, 50 bodů).",
                    )
                }
                return@launch
            }
            _state.update {
                it.copy(
                    loadState = TestLoadState.Ready,
                    questions = built.questions,
                    pointsPerQuestion = built.pointsPerQuestion,
                    examBlocks = built.blocks,
                    answersByQuestionId = emptyMap(),
                    index = 0,
                    runPhase = TestRunPhase.Countdown,
                    countdownShow = 3,
                    testRemainingMs = null,
                )
            }
            runCountdownThenStartTimer()
        }
    }

    fun clearNavEvent() {
        _navEvent.value = null
    }

    override fun onCleared() {
        timerJob?.cancel()
        super.onCleared()
    }

    private suspend fun runCountdownThenStartTimer() {
        for (v in 3 downTo 1) {
            _state.update { it.copy(countdownShow = v) }
            countdownTick()
            delay(1000L)
        }
        _state.update { it.copy(countdownShow = 0) }
        countdownTick()
        delay(800L)
        _state.update {
            it.copy(
                countdownShow = null,
                runPhase = TestRunPhase.Running,
                testRemainingMs = TEST_DURATION_MS,
            )
        }
        startTestTimer()
    }

    private fun startTestTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var t = TEST_DURATION_MS
            // Poslední úplná sekunda, u níž jsme zahráli tikot (zabraňuje 4× tik/sekundu).
            var lastTickedSecond: Int = -1
            while (t > 0) {
                _state.update { it.copy(testRemainingMs = t) }
                // V posledních 5 sekundách přehraj krátký countdown tik jednou za vteřinu.
                if (t <= 5_000L) {
                    val secondsLeft = ((t + 999L) / 1000L).toInt()
                    if (secondsLeft in 1..5 && secondsLeft != lastTickedSecond) {
                        lastTickedSecond = secondsLeft
                        countdownTick()
                    }
                }
                delay(250L)
                t -= 250L
            }
            _state.update { it.copy(testRemainingMs = 0L) }
            completeTestFinal()
        }
    }

    fun selectAnswer(key: String) {
        val s = _state.value
        if (s.loadState != TestLoadState.Ready || s.runPhase != TestRunPhase.Running) return
        val q = s.questions.getOrNull(s.index) ?: return
        val norm = normalizeAnswerKey(key)
        _state.update {
            it.copy(
                answersByQuestionId = it.answersByQuestionId + (q.id to norm),
                answerToken = it.answerToken + 1,
            )
        }
    }

    fun goToQuestion(pageIndex: Int) {
        val s = _state.value
        if (pageIndex !in s.questions.indices || pageIndex == s.index) return
        _state.update { it.copy(index = pageIndex) }
    }

    fun prevQuestion() {
        val s = _state.value
        if (s.index <= 0) return
        _state.update { it.copy(index = s.index - 1) }
    }

    fun nextQuestion() {
        val s = _state.value
        if (s.index >= s.questions.lastIndex) return
        _state.update { it.copy(index = s.index + 1) }
    }

    fun requestQuit() {
        _state.update { it.copy(showQuitDialog = true) }
    }

    fun dismissQuitDialog() {
        _state.update { it.copy(showQuitDialog = false) }
    }

    fun confirmQuit() {
        _state.update { it.copy(showQuitDialog = false) }
        completeTestFinal()
    }

    fun finishTest() {
        completeTestFinal()
    }

    private fun completeTestFinal() {
        if (hasCompleted) return
        val st = _state.value
        val qs = st.questions
        val pts = st.pointsPerQuestion
        val answered = st.answersByQuestionId
        timerJob?.cancel()
        timerJob = null
        hasCompleted = true
        viewModelScope.launch {
            if (qs.isEmpty()) return@launch
            val qsForSave = qs.map { q -> q.copy(userAnswer = answered[q.id]) }
            val app = getApplication<Application>()
            TestCompletionHelper.applyPostTestRewards(
                application = app,
                lessonProgress = lessonProgress,
                questions = qsForSave,
                pointsPerQuestion = pts,
            )
            val (attemptId, _) = testRepo.insertCompletedAttempt(
                questions = qsForSave,
                pointsPerQuestion = pts,
                resolveCorrect = ::resolveCorrectKey,
                normalizeKey = ::normalizeAnswerKey,
                answerLabel = { q, k -> answerKeyToDisplayLabel(q, k) },
            )
            _navEvent.value = TestNavEvent.ToResults(attemptId)
        }
    }

    private fun countdownTick() {
        HapticFeedback.onCountdown(getApplication())
        SoundManager.play(SoundManager.Sound.COUNTDOWN)
    }
}
