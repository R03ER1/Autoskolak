package cz.autokolk.ui.screens.test

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cz.autokolk.data.test.TestAnswerEntity
import cz.autokolk.data.test.TestAttemptEntity
import cz.autokolk.data.test.TestAttemptRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TestResultRowUi(
    val questionText: String,
    val correct: Boolean,
    val pointsLabel: String,
    val userAnswerLabel: String,
    val correctAnswerLabel: String,
)

data class TestResultsUiState(
    val loading: Boolean = true,
    val missing: Boolean = false,
    val score: Int = 0,
    val maxScore: Int = 50,
    val passed: Boolean = false,
    val hasDetails: Boolean = true,
    val rows: List<TestResultRowUi> = emptyList(),
)

class TestResultsViewModel(
    application: Application,
    private val attemptId: Long,
) : AndroidViewModel(application) {

    private val repo = TestAttemptRepository.getInstance(application)

    private val _state = MutableStateFlow(TestResultsUiState())
    val state: StateFlow<TestResultsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val attempt: TestAttemptEntity? = repo.getAttempt(attemptId)
            if (attempt == null) {
                _state.value = TestResultsUiState(loading = false, missing = true)
                return@launch
            }
            val answers: List<TestAnswerEntity> = repo.getAnswers(attemptId)
            val rows = if (attempt.hasAnswerDetails && answers.isNotEmpty()) {
                val perQ = 50 / answers.size.coerceAtLeast(1)
                answers.map { a ->
                    TestResultRowUi(
                        questionText = a.questionText,
                        correct = a.correct,
                        pointsLabel = "${if (a.correct) perQ else 0} b",
                        userAnswerLabel = a.userAnswerLabel,
                        correctAnswerLabel = a.correctAnswerLabel,
                    )
                }
            } else {
                emptyList()
            }
            _state.value = TestResultsUiState(
                loading = false,
                missing = false,
                score = attempt.score,
                maxScore = attempt.maxScore,
                passed = attempt.passed,
                hasDetails = attempt.hasAnswerDetails && answers.isNotEmpty(),
                rows = rows,
            )
        }
    }
}
