package cz.autokolk.ui.screens.quiz

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cz.autokolk.LessonProgress
import cz.autokolk.Question
import cz.autokolk.data.test.TestAttemptRepository
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
    val index: Int = 0,
    val testRemainingMs: Long? = null,
    val showQuitDialog: Boolean = false,
    /** Inkrementace při změně odpovědi (mutovaný [Question.userAnswer]). */
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
            val pool = lessonProgress.getRandomQuestions(TEST_QUESTION_COUNT)
            if (pool.size < TEST_QUESTION_COUNT) {
                _state.update {
                    it.copy(
                        loadState = TestLoadState.InsufficientPool,
                        insufficientMessage = "V databázi není dostatek otázek pro zkoušku (potřeba ${TEST_QUESTION_COUNT}).",
                    )
                }
                return@launch
            }
            _state.update {
                it.copy(
                    loadState = TestLoadState.Ready,
                    questions = pool,
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
            buzzTick()
            delay(1000L)
        }
        _state.update { it.copy(countdownShow = 0) }
        buzzTick()
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
            while (t > 0) {
                _state.update { it.copy(testRemainingMs = t) }
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
        q.userAnswer = norm
        _state.update { it.copy(answerToken = it.answerToken + 1) }
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

    fun allQuestionsAnswered(): Boolean {
        val qs = _state.value.questions
        if (qs.isEmpty()) return false
        return qs.all { normalizeAnswerKey(it.userAnswer).isNotEmpty() }
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

    fun finishTestIfComplete() {
        if (allQuestionsAnswered()) completeTestFinal()
    }

    private fun completeTestFinal() {
        if (hasCompleted) return
        val qs = _state.value.questions
        timerJob?.cancel()
        timerJob = null
        hasCompleted = true
        viewModelScope.launch {
            if (qs.isEmpty()) return@launch
            val app = getApplication<Application>()
            TestCompletionHelper.applyPostTestRewards(app, lessonProgress, qs)
            val (attemptId, _) = testRepo.insertCompletedAttempt(
                questions = qs,
                resolveCorrect = ::resolveCorrectKey,
                normalizeKey = ::normalizeAnswerKey,
                answerLabel = { q, k -> answerKeyToDisplayLabel(q, k) },
            )
            _navEvent.value = TestNavEvent.ToResults(attemptId)
        }
    }

    private fun buzzTick() {
        vibrate(12L, 28)
    }

    private fun vibrate(durationMs: Long, amplitude: Int) {
        val app = getApplication<Application>()
        if (ContextCompat.checkSelfPermission(app, Manifest.permission.VIBRATE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val v = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = app.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vm?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            app.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        } ?: return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(
                    VibrationEffect.createOneShot(
                        durationMs,
                        amplitude.coerceIn(1, 255),
                    ),
                )
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(durationMs)
            }
        } catch (_: SecurityException) {
        }
    }
}
