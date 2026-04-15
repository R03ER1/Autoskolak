package cz.autokolk.data.test

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "test_attempts")
data class TestAttemptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val finishedAtMillis: Long,
    val score: Int,
    val maxScore: Int = 50,
    val passed: Boolean,
    /** Null for legacy migrated rows without per-question data. */
    val correctCount: Int? = null,
    val hasAnswerDetails: Boolean = true,
)
