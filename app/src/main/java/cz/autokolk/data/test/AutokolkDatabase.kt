package cz.autokolk.data.test

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [TestAttemptEntity::class, TestAnswerEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AutokolkDatabase : RoomDatabase() {
    abstract fun testAttemptDao(): TestAttemptDao

    companion object {
        @Volatile
        private var instance: AutokolkDatabase? = null

        fun getInstance(context: Context): AutokolkDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AutokolkDatabase::class.java,
                    "autokolk_test_attempts.db",
                ).build().also { instance = it }
            }
        }
    }
}
