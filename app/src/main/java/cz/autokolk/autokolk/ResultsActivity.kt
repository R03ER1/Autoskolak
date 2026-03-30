package cz.autokolk

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.button.MaterialButton

class ResultsActivity : AutokolkActivity() {
    private var lessonInterstitialAd: InterstitialAd? = null
    private var adShownForThisResult = false

    companion object {
        private const val EXTRA_CORRECT_ANSWERS = "extra_correct_answers"
        private const val EXTRA_TOTAL_QUESTIONS = "extra_total_questions"
        private const val EXTRA_LESSON_NUMBER = "extra_lesson_number"
        private const val EXTRA_IS_REVIEW = "extra_is_review"
        const val EXTRA_FIRST_OF_DAY = "extra_first_of_day"
        const val EXTRA_IS_PRACTICE = "extra_is_practice"
        const val EXTRA_PRACTICE_CATEGORY = "extra_practice_category"
        private const val LESSON_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-7904041740523292/1806063612"

        fun createIntent(
            context: Context, 
            correctAnswers: Int, 
            totalQuestions: Int,
            lessonNumber: Int,
            isReviewMode: Boolean
        ): Intent {
            return Intent(context, ResultsActivity::class.java).apply {
                putExtra(EXTRA_CORRECT_ANSWERS, correctAnswers)
                putExtra(EXTRA_TOTAL_QUESTIONS, totalQuestions)
                putExtra(EXTRA_LESSON_NUMBER, lessonNumber)
                putExtra(EXTRA_IS_REVIEW, isReviewMode)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        // Set the status bar color to black
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        // Resolve overlay/content views
        val adLoadingOverlay = findViewById<View>(R.id.adLoadingOverlay)
        val resultsContent = findViewById<View>(R.id.resultsContent)

        val correctAnswers = intent.getIntExtra(EXTRA_CORRECT_ANSWERS, 0)
        val totalQuestions = intent.getIntExtra(EXTRA_TOTAL_QUESTIONS, 0)
        val lessonNumber = intent.getIntExtra(EXTRA_LESSON_NUMBER, 1)
        val isReviewMode = intent.getBooleanExtra(EXTRA_IS_REVIEW, false)
        val percentage = (correctAnswers * 100.0 / totalQuestions).toInt()
        val isPractice = intent.getBooleanExtra(EXTRA_IS_PRACTICE, false)
        val isRandom = intent.getBooleanExtra("extra_is_random", false)
        val practiceCategory = intent.getStringExtra(EXTRA_PRACTICE_CATEGORY) ?: ""

        var pointsAwarded = 0
        if (!isPractice && !isRandom) {
            // Calculate awarded points (body) based on rules
            pointsAwarded = run {
                if (!isReviewMode) {
                    when {
                        percentage == 100 -> 8
                        percentage >= 65 -> 6
                        percentage < 35 -> 1
                        else -> 4
                    }
                } else {
                    when {
                        totalQuestions in 6..10 -> {
                            when {
                                percentage == 100 -> 6
                                percentage >= 65 -> 4
                                else -> 2
                            }
                        }
                        totalQuestions in 1..5 -> {
                            if (percentage >= 65) 2 else 1
                        }
                        else -> 0
                    }
                }
            }
            if (pointsAwarded > 0) {
                LessonProgress(this).addPoints(pointsAwarded)
            }
        }

        // Update congratulation text based on performance
        val congratsText = findViewById<TextView>(R.id.congratsText)
        congratsText.text = if (isPractice) {
            "Hotovo!"
        } else if (isRandom) {
            if (percentage >= 50) "Získáváte extra život!" else "Bohužel tentokrát bez extra života."
        } else {
            when {
                percentage == 100 -> "Výborně!"
                percentage >= 80 -> "Skvělá práce!"
                percentage >= 60 -> "Dobrá práce!"
                else -> "Zkuste to znovu!"
            }
        }

        // Show detailed results
        val resultsText = findViewById<TextView>(R.id.resultsText)
        if (isPractice) {
            resultsText.text = ""
        } else if (isRandom) {
            val earned = intent.getBooleanExtra("extra_random_earned", false)
            val base = "Náhodný kvíz\n\nSprávně: $correctAnswers z $totalQuestions."
            val tail = if (earned) "\n\nZískáváte extra život!" else "\n\nBohužel tentokrát bez extra života."
            resultsText.text = base + tail
        } else {
            val modeText = if (isReviewMode) "Opakování lekce" else "Lekce"
            val baseResults = "$modeText $lessonNumber\n\n" +
                "Správně jste odpověděli na $correctAnswers z $totalQuestions otázek ($percentage%).\n\n" +
                when {
                    percentage < 60 -> "Pro úspěšné zvládnutí testu potřebujete alespoň 60%."
                    isReviewMode && percentage == 100 -> "Všechny otázky jste zvládli správně!"
                    isReviewMode -> "Pokračujte v procvičování zbývajících otázek."
                    else -> "Můžete pokračovat na další lekci!"
                }
            val pointsLine = if (pointsAwarded > 0) "\n\nZískali jste $pointsAwarded body." else ""
            resultsText.text = baseResults + pointsLine
        }

        val firstOfDay = intent.getBooleanExtra(EXTRA_FIRST_OF_DAY, false)
        val homeButton = findViewById<MaterialButton>(R.id.homeButton)
        val practiceClose = findViewById<MaterialButton>(R.id.practiceClose)

        if (isPractice) {
            // Hide lesson home button, show practice close action
            homeButton.visibility = android.view.View.GONE
            practiceClose.visibility = android.view.View.VISIBLE
            practiceClose.setOnClickListener {
                if (firstOfDay) {
                    val streakIntent = Intent(this, StreakActivity::class.java)
                    startActivity(streakIntent)
                }
                finish()
            }
        } else {
            practiceClose.visibility = android.view.View.GONE
            if (firstOfDay) {
                homeButton.text = "Pokračovat"
                homeButton.setOnClickListener {
                    val streakIntent = Intent(this, StreakActivity::class.java)
                    startActivity(streakIntent)
                    finish()
                }
            } else {
                homeButton.setOnClickListener { finish() }
            }
        }

        maybeShowLessonInterstitial(isPractice, isRandom, adLoadingOverlay, resultsContent)
    }

    private fun maybeShowLessonInterstitial(
        isPractice: Boolean,
        isRandom: Boolean,
        adLoadingOverlay: View,
        resultsContent: View
    ) {
        // Show interstitial only for completed lessons/reviews, not for practice or random quiz.
        if (isPractice || isRandom || adShownForThisResult) {
            // Ensure content is visible and overlay hidden when we skip ad
            adLoadingOverlay.visibility = View.GONE
            resultsContent.visibility = View.VISIBLE
            return
        }

        // While loading/showing ad, show fullscreen overlay and hide content
        adLoadingOverlay.visibility = View.VISIBLE
        resultsContent.visibility = View.INVISIBLE

        InterstitialAd.load(
            this,
            LESSON_INTERSTITIAL_AD_UNIT_ID,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    lessonInterstitialAd = interstitialAd
                    lessonInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            lessonInterstitialAd = null
                            adLoadingOverlay.visibility = View.GONE
                            resultsContent.visibility = View.VISIBLE
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError) {
                            Log.w("ResultsActivity", "Interstitial failed to show: ${adError.message}")
                            lessonInterstitialAd = null
                            adLoadingOverlay.visibility = View.GONE
                            resultsContent.visibility = View.VISIBLE
                        }
                    }
                    adShownForThisResult = true
                    lessonInterstitialAd?.show(this@ResultsActivity)
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.w("ResultsActivity", "Interstitial failed to load: ${loadAdError.message}")
                    lessonInterstitialAd = null
                    adLoadingOverlay.visibility = View.GONE
                    resultsContent.visibility = View.VISIBLE
                }
            }
        )
    }
} 