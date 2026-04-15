package cz.autokolk.data.test

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

/**
 * Pouze blokující metody — Room + novější javac/Kotlin generuje u `suspend` v DAO
 * nekompatibilní signatury Continuation oproti Kotlin rozhraní.
 */
@Dao
interface TestAttemptDao {

    @Insert
    fun insertAttemptBlocking(attempt: TestAttemptEntity): Long

    @Insert
    fun insertAnswersBlocking(answers: List<TestAnswerEntity>)

    @Query("SELECT * FROM test_attempts WHERE id = :id LIMIT 1")
    fun getAttemptByIdBlocking(id: Long): TestAttemptEntity?

    @Query("SELECT * FROM test_answer_rows WHERE attemptId = :attemptId ORDER BY orderIndex ASC")
    fun getAnswersForAttemptBlocking(attemptId: Long): List<TestAnswerEntity>

    @Query("SELECT score FROM test_attempts ORDER BY finishedAtMillis DESC LIMIT :limit")
    fun getRecentScoresDescendingBlocking(limit: Int): List<Int>

    @Query("SELECT COUNT(*) FROM test_attempts")
    fun countAttemptsBlocking(): Int

    @Query("SELECT AVG(score * 1.0) FROM test_attempts WHERE maxScore = :maxScore")
    fun averageScoreBlocking(maxScore: Int): Double?

    @Query(
        """
        SELECT (SUM(CASE WHEN passed THEN 1 ELSE 0 END) * 100.0 / NULLIF(COUNT(*), 0))
        FROM test_attempts
        """,
    )
    fun passRatePercentAllBlocking(): Double?
}
