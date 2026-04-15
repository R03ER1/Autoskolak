package cz.autokolk.data.test

import android.content.Context
import androidx.room.withTransaction
import cz.autokolk.LessonProgress
import cz.autokolk.Question
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

private const val PREFS = "lesson_progress"
private const val KEY_ROOM_MIGRATED = "test_scores_room_migrated"

class TestAttemptRepository(
    private val appContext: Context,
    private val db: AutokolkDatabase = AutokolkDatabase.getInstance(appContext),
) {
    private val dao = db.testAttemptDao()

    suspend fun migrateLegacyScoresIfNeeded() = withContext(Dispatchers.IO) {
        val prefs = appContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (prefs.getBoolean(KEY_ROOM_MIGRATED, false)) return@withContext
        val scores = LessonProgress(appContext).getAllTestScores(50)
        if (scores.isEmpty()) {
            prefs.edit().putBoolean(KEY_ROOM_MIGRATED, true).apply()
            return@withContext
        }
        val base = System.currentTimeMillis() - scores.size * 86_400_000L
        scores.forEachIndexed { i, score ->
            dao.insertAttemptBlocking(
                TestAttemptEntity(
                    finishedAtMillis = base + i * 86_400_000L,
                    score = score,
                    maxScore = 50,
                    passed = score >= 43,
                    correctCount = null,
                    hasAnswerDetails = false,
                ),
            )
        }
        prefs.edit().putBoolean(KEY_ROOM_MIGRATED, true).apply()
    }

    /** @return Pair(attemptId, weightedScore 0–50) */
    suspend fun insertCompletedAttempt(
        questions: List<Question>,
        pointsPerQuestion: List<Int> = emptyList(),
        finishedAtMillis: Long = System.currentTimeMillis(),
        resolveCorrect: (Question) -> String,
        normalizeKey: (String?) -> String,
        answerLabel: (Question, String) -> String,
    ): Pair<Long, Int> = withContext(Dispatchers.IO) {
        var correct = 0
        var weighted = 0
        val answers = questions.mapIndexed { index, q ->
            val userKey = normalizeKey(q.userAnswer)
            val ok = userKey.isNotEmpty() && userKey == resolveCorrect(q)
            if (ok) {
                correct++
                weighted += pointsPerQuestion.getOrElse(index) { 0 }
            }
            val userLabel = if (userKey.isEmpty()) "—" else answerLabel(q, userKey)
            TestAnswerEntity(
                attemptId = 0L,
                questionId = q.id,
                questionText = q.questionText,
                userAnswerLabel = userLabel,
                correctAnswerLabel = answerLabel(q, resolveCorrect(q)),
                correct = ok,
                orderIndex = index,
            )
        }
        if (pointsPerQuestion.size != questions.size || pointsPerQuestion.sum() <= 0) {
            weighted = (correct * 50 / questions.size.coerceAtLeast(1)).coerceIn(0, 50)
        } else {
            weighted = weighted.coerceIn(0, 50)
        }
        val attempt = TestAttemptEntity(
            finishedAtMillis = finishedAtMillis,
            score = weighted,
            maxScore = 50,
            passed = weighted >= 43,
            correctCount = correct,
            hasAnswerDetails = true,
        )
        val id = db.withTransaction {
            val attemptId = dao.insertAttemptBlocking(attempt)
            dao.insertAnswersBlocking(answers.map { it.copy(attemptId = attemptId) })
            attemptId
        }
        Pair(id, weighted)
    }

    suspend fun getAttempt(id: Long): TestAttemptEntity? = withContext(Dispatchers.IO) {
        dao.getAttemptByIdBlocking(id)
    }

    suspend fun getAnswers(attemptId: Long): List<TestAnswerEntity> = withContext(Dispatchers.IO) {
        dao.getAnswersForAttemptBlocking(attemptId)
    }

    suspend fun getChartScoresDescending(limit: Int = 60): List<Int> = withContext(Dispatchers.IO) {
        dao.getRecentScoresDescendingBlocking(limit)
    }

    suspend fun getStats(): TestStatsSnapshot = withContext(Dispatchers.IO) {
        val count = dao.countAttemptsBlocking()
        val avg = dao.averageScoreBlocking(maxScore = 50) ?: 0.0
        val passAll = dao.passRatePercentAllBlocking() ?: 0.0
        TestStatsSnapshot(
            attemptCount = count,
            averageScore = avg,
            passRatePercent = passAll,
        )
    }

    fun chartScoresFlow(limit: Int = 60): Flow<List<Int>> = flow {
        emit(getChartScoresDescending(limit))
    }.flowOn(Dispatchers.IO)

    companion object {
        @Volatile
        private var inst: TestAttemptRepository? = null

        fun getInstance(context: Context): TestAttemptRepository {
            return inst ?: synchronized(this) {
                inst ?: TestAttemptRepository(context.applicationContext).also { inst = it }
            }
        }
    }
}

data class TestStatsSnapshot(
    val attemptCount: Int,
    val averageScore: Double,
    val passRatePercent: Double,
)
