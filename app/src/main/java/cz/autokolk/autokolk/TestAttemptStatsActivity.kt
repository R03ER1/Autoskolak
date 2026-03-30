package cz.autokolk

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView

class TestAttemptStatsActivity : AutokolkActivity() {
    private lateinit var lessonProgress: LessonProgress
    private lateinit var streakButton: com.google.android.material.button.MaterialButton
    private lateinit var xpButton: com.google.android.material.button.MaterialButton
    private lateinit var heartsButton: com.google.android.material.button.MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blank_page)

        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        lessonProgress = LessonProgress(this)
        streakButton = findViewById(R.id.streakButton)
        xpButton = findViewById(R.id.xpButton)
        heartsButton = findViewById(R.id.heartsButton)
        updateStreakHeader()
        updatePointsHeader()
        updateHeartsHeader()

        streakButton.setOnClickListener { showStreakBottomSheet() }
        xpButton.setOnClickListener { showPointsBottomSheet() }
        heartsButton.setOnClickListener { showHeartsBottomSheet() }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.setBackgroundColor(Color.TRANSPARENT)
        bottomNav.background = null
        ViewCompat.setElevation(bottomNav, 0f)
        bottomNav.backgroundTintList = android.content.res.ColorStateList.valueOf(Color.TRANSPARENT)
        bottomNav.itemRippleColor = null
        try { bottomNav.setItemBackground(null) } catch (_: Throwable) { }
        try { bottomNav.isItemActiveIndicatorEnabled = false } catch (_: Throwable) { }

        bottomNav.selectedItemId = R.id.nav_test
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = android.content.Intent(this, HomeActivity::class.java)
                        .addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP or android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_lion -> {
                    val intent = android.content.Intent(this, AlexActivity::class.java)
                        .addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP or android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_exercise -> {
                    val intent = android.content.Intent(this, PracticeActivity::class.java)
                        .addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP or android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_test -> true
                R.id.nav_settings -> {
                    val intent = android.content.Intent(this, SettingsActivity::class.java)
                        .addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP or android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                else -> false
            }
        }

        val titleView = findViewById<TextView>(R.id.pageTitle)
        titleView.text = "Statistiky"
        val contentContainer = titleView.parent as? android.view.ViewGroup
        contentContainer?.removeAllViews()
        layoutInflater.inflate(R.layout.activity_test_stats, contentContainer, true)

        updateAverage()

        // Set chart data
        val chart = findViewById<ScoresChartView>(R.id.scoresChart)
        val scores = lessonProgress.getAllTestScores(50)
        chart?.setScores(scores, 50)
    }

    override fun onResume() {
        super.onResume()
        lessonProgress.normalizeStreakForToday()
        updateStreakHeader()
        updatePointsHeader()
        updateHeartsHeader()
    }

    private fun updateStreakHeader() {
        val streak = lessonProgress.getCurrentStreak()
        streakButton.text = streak.toString()
    }

    private fun updatePointsHeader() {
        val points = lessonProgress.getTotalPoints()
        xpButton.text = points.toString()
    }

    private fun updateHeartsHeader() {
        val hearts = lessonProgress.getCurrentHearts()
        heartsButton.text = hearts.toString()
    }

    private fun updateAverage() {
        val avg = lessonProgress.getAverageTestScore(50)
        val avgText = findViewById<TextView>(R.id.avgScoreValue)
        val display = "Průměrné skóre: ${avg.toInt()}/50"
        avgText?.text = display
    }

    private fun showStreakBottomSheet() {
        val dialog = com.google.android.material.bottomsheet.BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.streak_bottom_sheet, null)
        dialog.setContentView(view)
        val streak = lessonProgress.getCurrentStreak()
        view.findViewById<android.widget.ImageView>(R.id.bottomSheetFlame).setImageResource(R.drawable.ic_streak)
        view.findViewById<TextView>(R.id.bottomSheetStreakNumber).text = streak.toString()
        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.bottomSheetOk).setOnClickListener { dialog.dismiss() }
        // Hide + button for streak sheet
        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.bottomSheetPlus)?.visibility = View.GONE
        dialog.show()
    }

    private fun showPointsBottomSheet() {
        val dialog = com.google.android.material.bottomsheet.BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.streak_bottom_sheet, null)
        dialog.setContentView(view)
        val points = lessonProgress.getTotalPoints()
        view.findViewById<android.widget.ImageView>(R.id.bottomSheetFlame).setImageResource(R.drawable.ic_coin)
        view.findViewById<TextView>(R.id.bottomSheetStreakNumber).text = points.toString()
        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.bottomSheetOk).setOnClickListener { dialog.dismiss() }
        // Hide actions that are only for hearts
        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.bottomSheetPlus)?.visibility = View.GONE
        view.findViewById<View>(R.id.bottomSheetRewardContainer)?.visibility = View.GONE
        dialog.show()
    }

    private fun showHeartsBottomSheet() {
        val dialog = com.google.android.material.bottomsheet.BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.streak_bottom_sheet, null)
        dialog.setContentView(view)
        val emoji = view.findViewById<android.widget.ImageView>(R.id.bottomSheetFlame)
        val number = view.findViewById<TextView>(R.id.bottomSheetStreakNumber)
        val subtitle = view.findViewById<TextView>(R.id.bottomSheetSubtitle)
        emoji.setImageResource(R.drawable.ic_live)
        var hearts = lessonProgress.getCurrentHearts()
        number.text = hearts.toString()
        fun format(ms: Long): String {
            val totalSec = (ms / 1000).toInt()
            val m = (totalSec / 60) % 60
            val s = totalSec % 60
            return String.format(java.util.Locale.getDefault(), "%02d:%02d", m, s)
        }
        var handler: android.os.Handler? = android.os.Handler(mainLooper)
        val runnable = object : Runnable {
            override fun run() {
                hearts = lessonProgress.getCurrentHearts()
                number.text = hearts.toString()
                val until = lessonProgress.millisUntilNextHeart()
                if (hearts >= 15) {
                    subtitle.text = "Plné životy"
                } else if (until > 0) {
                    subtitle.text = "Další srdce za ${format(until)}"
                } else {
                    subtitle.text = "Nové srdce dostupné"
                }
                handler?.postDelayed(this, 1000L)
            }
        }
        runnable.run()
        dialog.setOnDismissListener {
            handler?.removeCallbacksAndMessages(null)
            handler = null
        }
        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.bottomSheetOk).setOnClickListener { dialog.dismiss() }
        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.bottomSheetPlus)?.setOnClickListener {
            val intent = android.content.Intent(this, MainActivity::class.java)
            intent.putExtra(MainActivity.EXTRA_RANDOM_COUNT, 10)
            startActivity(intent)
            dialog.dismiss()
        }
        dialog.show()
    }
}


