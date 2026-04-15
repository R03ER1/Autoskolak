package cz.autokolk.data.test

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
abstract class TestAttemptDao {

    @Insert
    abstract fun insertAttemptBlocking(attempt: TestAttemptEntity): Long

    @Insert
    abstract fun insertAnswersBlocking(answers: List<TestAnswerEntity>)

    @Transaction
    open fun insertAttemptWithAnswersBlocking(
        attempt: TestAttemptEntity,
        answers: List<TestAnswerEntity>,
    ): Long {
        val id = insertAttemptBlocking(attempt)
        insertAnswersBlocking(answers.map { it.copy(attemptId = id) })
        return id
    }

    @Query("SELECT * FROM test_attempts WHERE id = :id LIMIT 1")
    abstract suspend fun getAttemptById(id: Long): TestAttemptEntity?

    @Query("SELECT * FROM test_answer_rows WHERE attemptId = :attemptId ORDER BY orderIndex ASC")
    abstract suspend fun getAnswersForAttempt(attemptId: Long): List<TestAnswerEntity>

    @Query("SELECT score FROM test_attempts ORDER BY finishedAtMillis DESC LIMIT :limit")
    abstract suspend fun getRecentScoresDescending(limit: Int): List<Int>

    @Query("SELECT COUNT(*) FROM test_attempts")
    abstract suspend fun countAttempts(): Int

    @Query("SELECT AVG(score * 1.0) FROM test_attempts WHERE maxScore = :maxScore")
    abstract suspend fun averageScore(maxScore: Int): Double?

    @Query(
        """
        SELECT (SUM(CASE WHEN passed THEN 1 ELSE 0 END) * 100.0 / NULLIF(COUNT(*), 0))
        FROM test_attempts
        """,
    )
    abstract suspend fun passRatePercentAll(): Double?
}
