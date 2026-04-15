package cz.autokolk.data.test

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "test_answer_rows",
    foreignKeys = [
        ForeignKey(
            entity = TestAttemptEntity::class,
            parentColumns = ["id"],
            childColumns = ["attemptId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("attemptId")],
)
data class TestAnswerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val attemptId: Long,
    val questionId: String,
    val questionText: String,
    val userAnswerLabel: String,
    val correctAnswerLabel: String,
    val correct: Boolean,
    val orderIndex: Int,
)
