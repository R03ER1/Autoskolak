package cz.autokolk

import android.os.Bundle
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton

class StreakActivity : AutokolkActivity() {
    companion object {
        const val EXTRA_FROM_TEST = "extra_from_test"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_streak)

        // Set the status bar color to black
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        val lessonProgress = LessonProgress(this)
        val streak = lessonProgress.getCurrentStreak()
        val fromTest = intent.getBooleanExtra(EXTRA_FROM_TEST, false)

        findViewById<TextView>(R.id.streakFlame).text = "🔥"
        findViewById<TextView>(R.id.streakNumber).text = streak.toString()
        findViewById<MaterialButton>(R.id.backHomeButton).setOnClickListener {
            if (fromTest) {
                // If launched from test, go to TestAttemptActivity
                val intent = android.content.Intent(this, TestAttemptActivity::class.java)
                    .addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP or android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(intent)
            }
            finish()
        }
    }
}


