package cz.autokolk

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import cz.autokolk.reading.ReadingLessonsCatalog

class ReadingLessonActivity : AutokolkActivity() {
    companion object {
        const val EXTRA_CATEGORY = "extra_category"
        const val EXTRA_LESSON_NUMBER = "extra_lesson_number"
        const val EXTRA_IS_REVIEW = "extra_is_review"
        const val EXTRA_DISPLAY_LESSON_NUMBER = "extra_display_lesson_number"
        const val EXTRA_RETURN_TO_CALLER = "extra_return_to_caller"
    }

    private lateinit var lessonText: TextView
    private lateinit var lessonImage: ImageView
    private lateinit var nextButton: MaterialButton
    private lateinit var okButton: MaterialButton
    private lateinit var closeButton: ImageButton
    private lateinit var lessonProgressBar: ProgressBar

    private var currentSlide = 0
    private var category = ""
    private var lessonNumber = 1
    private var isReviewMode = false
    private var returnToCaller = false
    private lateinit var readingLessons: List<ReadingLesson>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reading_lesson)

        // Set the status bar color to black
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        category = intent.getStringExtra(EXTRA_CATEGORY) ?: ""
        lessonNumber = intent.getIntExtra(EXTRA_LESSON_NUMBER, 1)
        isReviewMode = intent.getBooleanExtra(EXTRA_IS_REVIEW, false)
        returnToCaller = intent.getBooleanExtra(EXTRA_RETURN_TO_CALLER, false)

        // Preload next-screen interstitial while user reads the intro (before quiz in MainActivity).
        LessonInterstitialAds.preload(this)

        initializeViews()
        loadReadingLessons()
        showCurrentSlide()
    }

    private fun initializeViews() {
        lessonText = findViewById(R.id.lessonText)
        lessonImage = findViewById(R.id.lessonImage)
        nextButton = findViewById(R.id.nextButton)
        okButton = findViewById(R.id.okButton)
        closeButton = findViewById(R.id.closeButton)
        lessonProgressBar = findViewById(R.id.lessonProgressBar)

        nextButton.setOnClickListener {
            currentSlide++
            showCurrentSlide()
        }

        okButton.setOnClickListener {
            finishReadingLesson()
        }

        closeButton.setOnClickListener {
            finishReadingLesson()
        }
    }

    private fun loadReadingLessons() {
        readingLessons = ReadingLessonsCatalog.readingLessonsForSubcategory(category)
    }

    private fun showCurrentSlide() {
        if (currentSlide >= readingLessons.size) {
            finishReadingLesson()
            return
        }

        val lesson = readingLessons[currentSlide]
        
        // Animate text and image
        val fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        lessonText.startAnimation(fadeIn)
        lessonImage.startAnimation(fadeIn)

        lessonText.text = lesson.text

        // Handle image display
        lesson.imagePath?.let { imagePath ->
            try {
                val inputStream = assets.open(imagePath)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                lessonImage.setImageBitmap(bitmap)
                // Limit max height to ~40% of screen to avoid overly tall images
                val displayMetrics = resources.displayMetrics
                val maxHeightPx = (displayMetrics.heightPixels * 0.4f).toInt()
                if (lessonImage.maxHeight != maxHeightPx) {
                    lessonImage.maxHeight = maxHeightPx
                }
                lessonImage.visibility = View.VISIBLE
                inputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
                lessonImage.visibility = View.GONE
            }
        } ?: run {
            lessonImage.visibility = View.GONE
        }

        // Update navigation buttons
        nextButton.visibility = if (lesson.isLastSlide) View.GONE else View.VISIBLE
        okButton.visibility = if (lesson.isLastSlide) View.VISIBLE else View.GONE

        // Update progress bar; progress is percentage of slides completed
        val totalSlides = if (readingLessons.isNotEmpty()) readingLessons.size else 1
        val progressPercent = ((currentSlide + 1) * 100) / totalSlides
        lessonProgressBar.progress = progressPercent
    }

    private fun finishReadingLesson() {
        // If opened as inline info from an ongoing lesson, just return to caller
        if (returnToCaller) {
            finish()
            return
        }
        // Otherwise transition to MainActivity with the lesson number
        val back = Intent(this, MainActivity::class.java).apply {
            putExtra(MainActivity.EXTRA_LESSON_NUMBER, lessonNumber)
            putExtra(MainActivity.EXTRA_IS_REVIEW, isReviewMode)
            // Do NOT pass category for normal lessons; this would incorrectly trigger practice mode
            putExtra(MainActivity.EXTRA_CATEGORY, "")
            // Forward display lesson number if provided so numbering stays consistent after intro
            val displayNum = intent?.getIntExtra(EXTRA_DISPLAY_LESSON_NUMBER, -1) ?: -1
            if (displayNum > 0) {
                putExtra(MainActivity.EXTRA_DISPLAY_LESSON_NUMBER, displayNum)
            }
        }
        startActivity(back)
        finish()
    }
} 