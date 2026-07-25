package cz.autokolk

import android.os.Bundle
import android.widget.TextView
import android.widget.ImageView
import android.widget.VideoView
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import java.io.BufferedReader
import java.io.InputStreamReader
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import android.animation.ObjectAnimator
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import android.widget.Toast
import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.view.View
import android.view.ViewGroup
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import android.media.MediaPlayer
import android.content.Intent
import android.widget.MediaController
import android.widget.ScrollView
import android.os.CountDownTimer
import android.util.Log
import android.widget.FrameLayout
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.navigation.compose.rememberNavController
import cz.autokolk.ui.screens.reading.ReadingLessonComposeScreen
import cz.autokolk.ui.screens.reading.ReadingLessonExternalExit
import cz.autokolk.ui.theme.AutokolkTheme
import android.widget.SeekBar
import android.widget.ImageButton
import android.os.Handler
import android.os.Looper
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest

/**
 * Lesson / practice / test UI. Video + DFM helpers live in [VideoModuleRegistry], [VideoSplitInstallListenerFactory], [VideoAssetFileCache] (audit A1).
 */
class MainActivity : AutokolkActivity() {
    companion object {
        const val EXTRA_LESSON_NUMBER = "extra_lesson_number"
        const val EXTRA_DISPLAY_LESSON_NUMBER = "extra_display_lesson_number"
        const val EXTRA_IS_REVIEW = "extra_is_review"
        const val EXTRA_CATEGORY = "extra_category"
        const val EXTRA_PRACTICE_WRONG_ONLY = "extra_practice_wrong_only"
        const val EXTRA_PRACTICE_MODE = "extra_practice_mode"
        const val PRACTICE_MODE_ALL = 0
        const val PRACTICE_MODE_WRONG = 1
        const val PRACTICE_MODE_CORRECT = 2
        const val PRACTICE_MODE_UNANSWERED = 3
        const val EXTRA_IS_TEST_MODE = "extra_is_test_mode"
        const val EXTRA_RANDOM_COUNT = "extra_random_count"
        const val EXTRA_IS_RANDOM = "extra_is_random"
    }

    private lateinit var questionText: TextView
    private lateinit var questionNumber: TextView
    private lateinit var questionImage: ImageView
    private lateinit var questionVideo: VideoView
    private lateinit var videoContainer: FrameLayout
    private lateinit var playPauseButton: ImageButton
    private lateinit var optionA: MaterialButton
    private lateinit var optionB: MaterialButton
    private lateinit var optionC: MaterialButton
    private lateinit var progressBar: ProgressBar
    private lateinit var resultPanel: View
    private lateinit var resultText: TextView
    private lateinit var resultNextButton: MaterialButton
    private lateinit var resultPrevButton: MaterialButton
    private lateinit var scrollView: ScrollView
    private lateinit var closeButton: ImageButton
    private lateinit var infoButton: ImageButton
    private var readingComposeOverlay: FrameLayout? = null
    private lateinit var floatingNavContainer: View
    private lateinit var floatingPrevButton: MaterialButton
    private lateinit var floatingNextButton: MaterialButton
    private var testCountDownTimer: CountDownTimer? = null
    private lateinit var testTimerView: TextView
    private var testTimeRemainingMs: Long = 0L
    private lateinit var coinPopup: View

    private lateinit var lessonProgress: LessonProgress
    private var questions: List<Question> = listOf()
    private var currentQuestionIndex = 0
    private var lessonNumber = 1
    private var isReviewMode = false
    private var displayLessonNumber: Int? = null
    private var category = ""
    private var practiceWrongOnly = false
    private var practiceMode: Int = PRACTICE_MODE_ALL
    private var isTestMode = false
    private val testQuestionWeightById: MutableMap<String, Int> = mutableMapOf()
    // Practice session tracking to award points progressively (1 per 5 unique questions viewed)
    private var practiceSeenFlags: MutableList<Boolean> = mutableListOf()
    private var practiceAwardedBuckets: Int = 0
    // Mapping between displayed buttons and their original answer keys ("a", "b", "c")
    private var buttonToOriginalAnswer: Map<MaterialButton, String> = emptyMap()
    private var originalAnswerToButton: Map<String, MaterialButton> = emptyMap()
    // Keep track of currently visible buttons for fallback highlighting
    private var visibleAnswerButtons: List<MaterialButton> = emptyList()
    // Test mode category tracking
    private var testCategoryCounts: Map<String, Int> = emptyMap()
    private var testCategoryStarts: Map<String, Int> = emptyMap()

    private lateinit var mediaPlayer: MediaPlayer
    private var currentVideoFile: File? = null

    // Dynamic Feature Modules for video assets
    private lateinit var splitInstallManager: SplitInstallManager
    private val videoModules = VideoModuleRegistry.MODULE_NAMES
    private val videoToModuleMap = VideoModuleRegistry.filenameToModule()
    private var installedVideoModules = mutableSetOf<String>()
    private var pendingVideoPath: String? = null

    private val videoFileCache by lazy {
        VideoAssetFileCache(File(cacheDir, "video_asset_cache"))
    }

    private lateinit var videoModuleStatusText: TextView

    private val splitInstallStateListener by lazy {
        VideoSplitInstallListenerFactory.create(
            "MainActivity",
            videoToModuleMap,
            installedVideoModules,
            getPendingVideoPath = { pendingVideoPath },
            setPendingVideoPath = { pendingVideoPath = it },
            onReloadVideo = { path ->
                runOnUiThread {
                    if (::videoModuleStatusText.isInitialized) {
                        videoModuleStatusText.visibility = View.GONE
                    }
                    handleVideoDisplay(path)
                }
            },
            onInstallFailed = { _, _ ->
                runOnUiThread {
                    pendingVideoPath = null
                    if (::videoModuleStatusText.isInitialized) {
                        videoModuleStatusText.visibility = View.GONE
                    }
                    Toast.makeText(
                        this,
                        getString(R.string.video_module_install_failed),
                        Toast.LENGTH_LONG,
                    ).show()
                }
            },
            onDownloadProgress = { _, percent ->
                runOnUiThread {
                    if (::videoModuleStatusText.isInitialized) {
                        videoModuleStatusText.visibility = View.VISIBLE
                        videoModuleStatusText.text = getString(
                            R.string.video_module_downloading_percent,
                            percent,
                        )
                    }
                }
            },
        )
    }

    private var isPlaying = true

    /**
     * Ensure the on-demand feature module with images and lesson videos is installed.
     * After install, assets are visible through the app [AssetManager].
     */
    private fun requestVideoModulesIfNeeded() {
        try {
            val manager = if (::splitInstallManager.isInitialized) {
                splitInstallManager
            } else {
                SplitInstallManagerFactory.create(this).also { splitInstallManager = it }
            }

            // Register listener for state updates
            manager.registerListener(splitInstallStateListener)

            // Check which modules are already installed
            val installedModules = manager.installedModules
            installedVideoModules.clear()
            installedVideoModules.addAll(installedModules.filter { it in videoModules })
            Log.d("MainActivity", "Installed video modules: ${installedVideoModules.joinToString()}")

            // Request installation of missing modules
            val missingModules = videoModules.filter { it !in installedModules }
            if (missingModules.isNotEmpty()) {
                Log.d("MainActivity", "Requesting installation of video modules: ${missingModules.joinToString()}")
                val request = SplitInstallRequest.newBuilder()
                    .apply {
                        missingModules.forEach { addModule(it) }
                    }
                    .build()
                
                manager.startInstall(request)
                .addOnSuccessListener { sessionId ->
                        Log.d("MainActivity", "Video modules installation started (sessionId=$sessionId)")
                }
                .addOnFailureListener { exception ->
                        Log.e("MainActivity", "Failed to request installation for video modules", exception)
                    }
            } else {
                Log.d("MainActivity", "All video modules are already installed")
                }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error while requesting video modules installation", e)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set the status bar color to black
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        lessonNumber = intent.getIntExtra(EXTRA_LESSON_NUMBER, 1)
        isReviewMode = intent.getBooleanExtra(EXTRA_IS_REVIEW, false)
        displayLessonNumber = intent.getIntExtra(EXTRA_DISPLAY_LESSON_NUMBER, -1).let { if (it > 0) it else null }
        category = intent.getStringExtra(EXTRA_CATEGORY) ?: ""
        practiceWrongOnly = intent.getBooleanExtra(EXTRA_PRACTICE_WRONG_ONLY, false)
        practiceMode = intent.getIntExtra(EXTRA_PRACTICE_MODE, PRACTICE_MODE_ALL)
        isTestMode = intent.getBooleanExtra(EXTRA_IS_TEST_MODE, false)
        lessonProgress = LessonProgress(this)

        if (shouldPreloadLessonInterstitial()) {
            LessonInterstitialAds.preload(this)
        }

        initializeViews()

        // After views exist (status text for DFM), register SplitInstall for video assets
        splitInstallManager = SplitInstallManagerFactory.create(this)
        requestVideoModulesIfNeeded()
        if (loadQuestions()) {
            setupListeners()
            showQuestion()
            updateProgress()
        } else {
            Toast.makeText(this, "Nepodařilo se načíst otázky", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun initializeViews() {
        questionText = findViewById(R.id.questionText)
        questionNumber = findViewById(R.id.questionNumber)
        questionImage = findViewById(R.id.questionImage)
        questionVideo = findViewById(R.id.questionVideo)
        videoContainer = findViewById(R.id.videoContainer)
        videoModuleStatusText = findViewById(R.id.videoModuleStatusText)
        playPauseButton = findViewById(R.id.playPauseButton)
        optionA = findViewById(R.id.optionA)
        optionB = findViewById(R.id.optionB)
        optionC = findViewById(R.id.optionC)
        progressBar = findViewById(R.id.progressBar)
        resultPanel = findViewById(R.id.resultPanel)
        resultText = findViewById(R.id.resultText)
        resultNextButton = findViewById(R.id.resultNextButton)
        resultPrevButton = findViewById(R.id.resultPrevButton)
        scrollView = findViewById(R.id.scrollView)
        closeButton = findViewById(R.id.closeButton)
        infoButton = findViewById(R.id.infoButton)
        floatingNavContainer = findViewById(R.id.floatingNavContainer)
        floatingPrevButton = findViewById(R.id.floatingPrevButton)
        floatingNextButton = findViewById(R.id.floatingNextButton)
        testTimerView = findViewById(R.id.testTimer)
        coinPopup = findViewById(R.id.coinPopupContainer)

        // Hide video container initially
        videoContainer.visibility = View.GONE
        videoModuleStatusText.visibility = View.GONE

        // Set up VideoView
        questionVideo.setOnPreparedListener { mp ->
            Log.d("MainActivity", "Video prepared")
            mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT)
            mp.isLooping = true
            mp.start()
            isPlaying = true
            updatePlayPauseButton()
        }

        questionVideo.setOnErrorListener { _, what, extra ->
            Log.e("MainActivity", "Video error: what=$what, extra=$extra")
            false
        }

        questionVideo.setOnCompletionListener {
            Log.d("MainActivity", "Video completed")
            questionVideo.start()
            isPlaying = true
            updatePlayPauseButton()
        }

        // Set up play/pause button
        playPauseButton.setOnClickListener {
            if (isPlaying) {
                questionVideo.pause()
            } else {
                questionVideo.start()
            }
            isPlaying = !isPlaying
            updatePlayPauseButton()
        }

        // Close lesson button
        closeButton.setOnClickListener {
            // Increment streak even if user quits early
            var firstOfDay = false
            try {
                firstOfDay = lessonProgress.updateStreakOnLessonCompleted()
            } catch (_: Throwable) { }

            // For practice sessions or test mode, show streak window after closing if first of day.
            // Krok 153: legacy StreakActivity zrušena, přesměrováno na Compose modální obrazovku streaku.
            if ((category.isNotBlank() || isTestMode) && firstOfDay) {
                try {
                    val streakIntent = Intent(this, ComposeMainActivity::class.java)
                        .putExtra(cz.autokolk.ui.navigation.ComposeNavIntent.EXTRA_OPEN_TAB, cz.autokolk.ui.navigation.ComposeNavIntent.OPEN_TAB_STREAK)
                    startActivity(streakIntent)
                } catch (_: Throwable) { }
            }
            finish()
        }

        if (isTestMode) {
            // 30 minutes in milliseconds
            val total = 30L * 60L * 1000L
            startOrResumeTestTimer(savedRemaining = null, totalMs = total)
            testTimerView.visibility = View.VISIBLE
        } else {
            testTimerView.visibility = View.GONE
        }

        // Info button shows topic intro (reading) when available for this lesson
        // Show only in normal lesson mode (not practice, not test)
        val canShowInfo = !isTestMode && category.isBlank()
        if (canShowInfo) {
            val code = getCategoryForLesson(lessonNumber)
            if (code != null) {
                infoButton.visibility = View.VISIBLE
                infoButton.setOnClickListener {
                    openLessonInfo(code)
                }
            } else {
                infoButton.visibility = View.GONE
            }
        } else {
            infoButton.visibility = View.GONE
        }
    }

    private fun openLessonInfo(@Suppress("UNUSED_PARAMETER") categoryCode: String) {
        try {
            if (readingComposeOverlay != null) return
            LessonInterstitialAds.preload(this)
            val root = findViewById<ViewGroup>(android.R.id.content)
            val host = FrameLayout(this).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
                setBackgroundColor(0xD9000000.toInt())
                isClickable = true
            }
            val composeView = ComposeView(this).apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {
                    AutokolkTheme {
                        val nav = rememberNavController()
                        ReadingLessonComposeScreen(
                            navController = nav,
                            lessonId = lessonNumber,
                            isReview = false,
                            externalExit = ReadingLessonExternalExit(
                                onDismissEmbedded = {
                                    root.removeView(host)
                                    readingComposeOverlay = null
                                },
                            ),
                        )
                    }
                }
            }
            composeView.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT,
            )
            host.addView(composeView)
            root.addView(host)
            readingComposeOverlay = host
        } catch (_: Throwable) { }
    }

    private fun getCategoryForLesson(lessonNumber: Int): String? {
        val plan = lessonProgress.getGlobalLessonPlan()
        val entry = plan.find { it.lessonNumber == lessonNumber } ?: return null
        val categoryName = entry.category
        val sub = entry.subcategory
        return when (sub) {
            // Known subcategory codes used in reading lessons
            "pru","neb","kri","upr","inf","pri","zak","vys","vod","slo","pok","cho","uca","aut","pra","mhd","sta","sme","pol","neh" -> sub
            else -> when (categoryName) {
                "Úřady" -> "pru"
                "Nebezpečí na vozovce" -> "neb"
                "Křižovatky" -> "kri"
                "Značky upravující přednost" -> "upr"
                "Informativní dopravní značky" -> "inf"
                "Příkazové dopravní značky" -> "pri"
                "Zákazové dopravní značky" -> "zak"
                "Výstražné dopravní značky" -> "vys"
                "Vodorovné značky" -> "vod"
                "Sloupky" -> "slo"
                "Policisté na křižovatce" -> "pok"
                "Stání a zastavení" -> "cho"
                "Účastníci provozu" -> "uca"
                "Typy vozidel" -> "aut"
                "Pruhy a zóny" -> "pra"
                "MHD" -> "mhd"
                "Stání a parkování" -> "sta"
                "Změny směru" -> "sme"
                "Policie" -> "pol"
                "Nehody" -> "neh"
                else -> null
            }
        }
    }

    private fun updatePlayPauseButton() {
        playPauseButton.setImageResource(
            if (isPlaying) android.R.drawable.ic_media_pause
            else android.R.drawable.ic_media_play
        )
    }

    private fun loadQuestions(): Boolean {
        try {
            if (isTestMode) {
                questions = buildTestModeQuestions()
                return questions.isNotEmpty()
            }
            // If launched as a random session, load N random questions from CSV
            val randomCount = intent.getIntExtra(EXTRA_RANDOM_COUNT, 0)
            if (randomCount > 0) {
                questions = lessonProgress.getRandomQuestions(randomCount)
                return questions.isNotEmpty()
            }
            // If launched for practice category, load category questions
            val isPracticeCategory = category.isNotBlank()
            val isUserMistakes = category.equals(LessonProgress.CATEGORY_USER_MISTAKES, ignoreCase = true)
            val allLessonQuestions = if (isPracticeCategory) {
                if (isUserMistakes) {
                    val (correctIds, wrongIds) = lessonProgress.getPracticeStatus(LessonProgress.CATEGORY_USER_MISTAKES)
                    val wrongOnly = lessonProgress.getQuestionsForIds(wrongIds).sortedWith(
                        compareByDescending<Question> { lessonProgress.getMistakeConsecutiveCount(it.id) }
                            .thenBy { it.id.toIntOrNull() ?: 0 }
                    )
                    val correctOnly = lessonProgress.getQuestionsForIds(correctIds)
                    val unanswered = emptyList<Question>()
                    when {
                        practiceWrongOnly && wrongOnly.isNotEmpty() -> wrongOnly
                        practiceMode == PRACTICE_MODE_WRONG -> wrongOnly
                        practiceMode == PRACTICE_MODE_CORRECT -> correctOnly
                        practiceMode == PRACTICE_MODE_UNANSWERED -> unanswered
                        wrongOnly.isNotEmpty() -> wrongOnly
                        correctOnly.isNotEmpty() -> correctOnly
                        else -> emptyList()
                    }
                } else {
                    val all = lessonProgress.getQuestionsForCategory(category)
                    val (correctIds, wrongIds) = lessonProgress.getPracticeStatus(category)
                    val unanswered = all.filter { q -> q.id !in correctIds && q.id !in wrongIds }
                    val wrongOnly = all.filter { q -> q.id in wrongIds }
                    val correctOnly = all.filter { q -> q.id in correctIds }
                    when {
                        // Backward compatibility with old flag
                        practiceWrongOnly && wrongOnly.isNotEmpty() -> wrongOnly
                        // New explicit modes
                        practiceMode == PRACTICE_MODE_WRONG -> wrongOnly.ifEmpty { all }
                        practiceMode == PRACTICE_MODE_CORRECT -> correctOnly.ifEmpty { all }
                        practiceMode == PRACTICE_MODE_UNANSWERED -> unanswered.ifEmpty { all }
                        // Default behavior
                        unanswered.isNotEmpty() -> unanswered
                        wrongOnly.isNotEmpty() -> wrongOnly
                        else -> all
                    }
                }
            } else {
                lessonProgress.getQuestionsForLesson(lessonNumber)
            }

            // In review mode, show only previously incorrect questions for this lesson
            questions = if (isPracticeCategory) {
                // Practice: shuffle except "Tvoje chyby", where order is by consecutive-wrong streak
                if (isUserMistakes) allLessonQuestions else allLessonQuestions.shuffled()
            } else if (isReviewMode) {
                val state = lessonProgress.getLessonState(lessonNumber)
                val filtered = allLessonQuestions.mapNotNull { question ->
                    if (state.incorrectQuestionIds.contains(question.id)) question.copy(userAnswer = null) else null
                }
                if (filtered.isNotEmpty()) filtered else allLessonQuestions
            } else {
                allLessonQuestions
            }
            // Initialize practice tracking for this session
            if (isPracticeCategory) {
                practiceSeenFlags = MutableList(questions.size) { false }
                practiceAwardedBuckets = 0
            }
            return questions.isNotEmpty()
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    private fun setupListeners() {
        optionA.setOnClickListener {
            buttonToOriginalAnswer[optionA]?.let { original -> checkAnswer(original) }
        }
        optionB.setOnClickListener {
            buttonToOriginalAnswer[optionB]?.let { original -> checkAnswer(original) }
        }
        optionC.setOnClickListener {
            buttonToOriginalAnswer[optionC]?.let { original -> checkAnswer(original) }
        }

        resultNextButton.setOnClickListener {
            if (!isTestMode) hideResultPanel()
            if (currentQuestionIndex < questions.size - 1) {
                currentQuestionIndex++
                showQuestion()
            } else {
                finishLesson()
            }
        }

        resultPrevButton.setOnClickListener {
            if (!isTestMode) hideResultPanel()
            if (currentQuestionIndex > 0) {
                currentQuestionIndex--
                showQuestion()
            }
        }

        // Floating navigation buttons for test mode
        floatingPrevButton.setOnClickListener {
            if (currentQuestionIndex > 0) {
                currentQuestionIndex--
                showQuestion()
            }
        }

        floatingNextButton.setOnClickListener {
            if (currentQuestionIndex < questions.size - 1) {
                currentQuestionIndex++
                showQuestion()
            } else {
                finishLesson()
            }
        }
    }

    private fun showQuestion() {
        if (questions.isEmpty()) return

        val question = questions[currentQuestionIndex]
        
        // Animate question text
        val fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        questionText.startAnimation(fadeIn)
        questionNumber.startAnimation(fadeIn)

        	questionText.text = question.questionText
        	val shownLessonNumber = displayLessonNumber ?: lessonNumber
        	questionNumber.text = if (isTestMode) {
                val categoryName = question.category?.let { mapCategoryDisplayName(it) } ?: "Neznámá kategorie"
                val points = testQuestionWeightById[question.id] ?: 0
                
                // Calculate position within current category
                val currentCategory = question.category
                val categoryStart = currentCategory?.let { testCategoryStarts[it] } ?: 0
                val categoryCount = currentCategory?.let { testCategoryCounts[it] } ?: 1
                val positionInCategory = (currentQuestionIndex - categoryStart) + 1
                
                "Test - Otázka ${currentQuestionIndex + 1} z ${questions.size} | $categoryName $positionInCategory/$categoryCount | $points body"
            } else if (category.isNotBlank()) {
        		"${mapCategoryDisplayName(category)} - Otázka ${currentQuestionIndex + 1} z ${questions.size}"
        	} else if (isReviewMode) {
        		"Opakování - Otázka ${currentQuestionIndex + 1} z ${questions.size}"
        	} else {
        		"Lekce $shownLessonNumber - Otázka ${currentQuestionIndex + 1} z ${questions.size}"
        	}

        // Handle image display
        question.imagePath?.let { imagePath ->
            try {
                val inputStream = assets.open(imagePath)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                questionImage.setImageBitmap(bitmap)
                // Limit max height to ~40% of screen to avoid overly tall images
                val displayMetrics = resources.displayMetrics
                val maxHeightPx = (displayMetrics.heightPixels * 0.4f).toInt()
                if (questionImage.maxHeight != maxHeightPx) {
                    questionImage.maxHeight = maxHeightPx
                }
                questionImage.visibility = View.VISIBLE
                questionImage.startAnimation(fadeIn)
                inputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
                questionImage.visibility = View.GONE
            }
        } ?: run {
            questionImage.visibility = View.GONE
        }

        // Handle video display
        question.videoPath?.let { videoPath ->
            handleVideoDisplay(videoPath)
        } ?: run {
            handleVideoDisplay(null)
        }
        
        // Animate options with slide from right
        val slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_right)
        optionA.startAnimation(slideIn)
        optionB.startAnimation(slideIn)
        optionC.startAnimation(slideIn)
        
        // Build, filter-out blank options, and shuffle while keeping original keys
        val allOptions = listOf(
            "a" to question.optionA,
            "b" to question.optionB,
            "c" to question.optionC
        )
        val filteredOptions = allOptions.filter { it.second.trim().isNotEmpty() }
        
        // Only shuffle if the correct answer is not a, b, or c (preserve order for a,b,c answers)
        val shouldShuffle = question.correctAnswer !in listOf("a)", "b)", "c)")
        val finalOptions = if (shouldShuffle) filteredOptions.shuffled() else filteredOptions

        // Assign texts only to the number of available options, hide the rest
        val buttons = listOf(optionA, optionB, optionC)
        // Reset visibility before assignment
        buttons.forEach { it.visibility = View.GONE }

        val usedButtons = mutableListOf<Pair<MaterialButton, Pair<String, String>>>()
        for (i in 0 until minOf(finalOptions.size, buttons.size)) {
            val (key, text) = finalOptions[i]
            val btn = buttons[i]
            btn.text = text
            btn.visibility = View.VISIBLE
            usedButtons.add(btn to (key to text))
        }

        // Create mappings for this question display from only visible buttons
        buttonToOriginalAnswer = usedButtons.associate { (btn, pair) -> btn to normalizeAnswerKey(pair.first) }
        originalAnswerToButton = usedButtons.associate { (btn, pair) -> normalizeAnswerKey(pair.first) to btn }
        visibleAnswerButtons = usedButtons.map { it.first }

        // Prepare UI for answering
        resetButtonColors()

        // Configure state based on whether already answered
        val savedAnswer = question.userAnswer
        if (savedAnswer != null) {
            // In test mode, keep options enabled so users can change their answer
            setOptionsEnabled(isTestMode)
            if (!isTestMode) {
                showAnswerResult(savedAnswer)
                showResultPanel(normalizeAnswerKey(savedAnswer) == resolveCorrectKey(question))
            } else {
                // In test mode, show floating navigation and highlight selected answer
                resultPanel.visibility = View.GONE
                floatingNavContainer.visibility = View.VISIBLE
                highlightSelectedAnswer(savedAnswer)
            }
        } else {
            setOptionsEnabled(true)
            if (!isTestMode) {
                hideResultPanel()
            } else {
                // In test mode, show floating navigation but no result panel
                resultPanel.visibility = View.GONE
                floatingNavContainer.visibility = View.VISIBLE
                resetButtonColors()
            }
        }

        // Update progress bar
        updateProgress()

        // Update prev button visibility
        resultPrevButton.visibility = if (currentQuestionIndex == 0) View.GONE else View.VISIBLE

        // Update floating navigation button text and visibility
        if (isTestMode) {
            val current = currentQuestionIndex + 1
            val total = questions.size
            val isLastQuestion = currentQuestionIndex == questions.size - 1
            floatingPrevButton.text = "← ${if (currentQuestionIndex > 0) currentQuestionIndex else current}/$total"
            
            if (isLastQuestion) {
                floatingNextButton.text = "UKONČIT"
                floatingNextButton.background = ContextCompat.getDrawable(this, R.drawable.button_gradient_red_orange)
                floatingNextButton.setTextColor(ContextCompat.getColor(this, R.color.white))
            } else {
                floatingNextButton.text = "${current + 1}/$total →"
                floatingNextButton.background = ContextCompat.getDrawable(this, R.drawable.button_gradient_green_blue)
                floatingNextButton.setTextColor(ContextCompat.getColor(this, R.color.white))
            }
            
            floatingPrevButton.visibility = if (currentQuestionIndex == 0) View.GONE else View.VISIBLE
            floatingNextButton.visibility = View.VISIBLE
        } else {
            floatingNavContainer.visibility = View.GONE
        }

        // Practice progressive points awarding: count unique questions viewed this session
        if (!isTestMode && category.isNotBlank()) {
            if (!practiceSeenFlags.getOrNull(currentQuestionIndex).orFalse()) {
                if (currentQuestionIndex in practiceSeenFlags.indices) {
                    practiceSeenFlags[currentQuestionIndex] = true
                }
                val seenCount = practiceSeenFlags.count { it }
                val buckets = seenCount / 5
                if (buckets > practiceAwardedBuckets) {
                    val delta = buckets - practiceAwardedBuckets
                    try {
                        LessonProgress(this).addPoints(delta)
                        // Show coin popup for each point earned
                        repeat(delta) {
                            Handler(Looper.getMainLooper()).postDelayed({
                                showCoinPopup()
                            }, it * 200L) // Stagger multiple popups by 200ms
                        }
                    } catch (_: Throwable) { }
                    practiceAwardedBuckets = buckets
                }
            }
        }
    }

    // Local safe helper for nullable Boolean
    private fun Boolean?.orFalse(): Boolean = this ?: false

    private fun showCoinPopup() {
        coinPopup.visibility = View.VISIBLE
        coinPopup.alpha = 1.0f
        coinPopup.scaleX = 0.5f
        coinPopup.scaleY = 0.5f
        
        // Animate popup appearance
        coinPopup.animate()
            .scaleX(1.0f)
            .scaleY(1.0f)
            .setDuration(200)
            .withEndAction {
                // Start fade out after a short delay
                Handler(Looper.getMainLooper()).postDelayed({
                    coinPopup.animate()
                        .alpha(0.0f)
                        .scaleX(0.8f)
                        .scaleY(0.8f)
                        .setDuration(1000)
                        .withEndAction {
                            coinPopup.visibility = View.GONE
                        }
                }, 1000) // Show for 1 second before starting fade
            }
    }

    private fun checkAnswer(selectedAnswer: String) {
        val question = questions[currentQuestionIndex]
        // In test mode, allow changing answers; in other modes, prevent re-answering
        if (question.userAnswer != null && !isTestMode) return // Already answered

        question.userAnswer = normalizeAnswerKey(selectedAnswer)
        if (!isTestMode) {
            val isCorrect = normalizeAnswerKey(selectedAnswer) == resolveCorrectKey(question)
            lessonProgress.recordMistakeStreak(question.id, isCorrect)
            if (category.isNotBlank()) {
                lessonProgress.savePracticeAnswer(category, question.id, isCorrect)
                try { AchievementsManager(this).onAnswer(isCorrect) } catch (_: Throwable) { }
            } else {
                // In lessons (not practice), consume a heart on wrong answer
                val isRandom = intent.getIntExtra(EXTRA_RANDOM_COUNT, 0) > 0
                if (!isCorrect && !isRandom) {
                    try {
                        val consumed = lessonProgress.consumeHeart()
                        if (consumed) {
                            HeartRefillJobService.scheduleNext(this@MainActivity, lessonProgress)
                        }
                    } catch (_: Throwable) { }
                }
            }
        }
        if (!isTestMode) {
            showAnswerResult(selectedAnswer)
            showResultPanel(normalizeAnswerKey(selectedAnswer) == resolveCorrectKey(question))
            setOptionsEnabled(false)
        } else {
            // In test mode, highlight selected answer and show floating navigation
            highlightSelectedAnswer(selectedAnswer)
            floatingNavContainer.visibility = View.VISIBLE
            // Keep options enabled in test mode so users can change their answer
        }
    }

    private fun showAnswerResult(selectedAnswer: String) {
        val question = questions[currentQuestionIndex]
        val correctButtonColor = ContextCompat.getColor(this, R.color.correct_answer)
        val wrongButtonColor = ContextCompat.getColor(this, R.color.wrong_answer)

        // Disable ripple effects on all answer buttons
        optionA.rippleColor = null
        optionB.rippleColor = null
        optionC.rippleColor = null

        // Animate all changes simultaneously
        val scaleAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)

        val selectedKey = normalizeAnswerKey(selectedAnswer)
        val correctKey = resolveCorrectKey(question)

        // Highlight selected button based on mapping
        originalAnswerToButton[selectedKey]?.let { selectedButton ->
            selectedButton.startAnimation(scaleAnimation)
            val isCorrect = selectedKey == correctKey
            selectedButton.setBackgroundColor(if (isCorrect) correctButtonColor else wrongButtonColor)
        }

        // If wrong answer selected, also show the correct answer button
        if (selectedKey != correctKey) {
            val correctBtn = originalAnswerToButton[correctKey]
                ?: visibleAnswerButtons.firstOrNull { btn ->
                    normalizeAnswerText(btn.text.toString()) == normalizeAnswerText(questions[currentQuestionIndex].correctAnswer)
                }
            correctBtn?.let {
                it.startAnimation(scaleAnimation)
                it.setBackgroundColor(correctButtonColor)
            }
        }
    }

    private fun resetButtonColors() {
        val defaultColor = ContextCompat.getColor(this, android.R.color.transparent)
        
        // Reset button colors and restore ripple effects
        optionA.apply {
            setBackgroundColor(defaultColor)
            rippleColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.button_pressed))
            setTextColor(ContextCompat.getColor(context, R.color.text_primary))
            strokeColor = null
            strokeWidth = 0
        }
        optionB.apply {
            setBackgroundColor(defaultColor)
            rippleColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.button_pressed))
            setTextColor(ContextCompat.getColor(context, R.color.text_primary))
            strokeColor = null
            strokeWidth = 0
        }
        optionC.apply {
            setBackgroundColor(defaultColor)
            rippleColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.button_pressed))
            setTextColor(ContextCompat.getColor(context, R.color.text_primary))
            strokeColor = null
            strokeWidth = 0
        }
    }

    private fun setOptionsEnabled(enabled: Boolean) {
        optionA.isEnabled = enabled
        optionB.isEnabled = enabled
        optionC.isEnabled = enabled
    }

    private fun highlightSelectedAnswer(selectedAnswer: String) {
        // Reset all button colors first
        resetButtonColors()
        
        // Find the selected button and add blue border
        val selectedKey = normalizeAnswerKey(selectedAnswer)
        val selectedButton = originalAnswerToButton[selectedKey]
        
        selectedButton?.let { button ->
            // Add blue border by setting stroke color
            button.strokeColor = ContextCompat.getColorStateList(this, android.R.color.holo_blue_bright)
            button.strokeWidth = 4 // 4dp border
        }
    }

    private fun showResultPanel(isCorrect: Boolean) {
        resultText.text = if (isCorrect) "Správně!" else "Špatně!"
        resultPanel.setBackgroundResource(if (isCorrect) R.drawable.result_panel_correct_gradient else R.drawable.result_panel_wrong_gradient)
        if (resultPanel.visibility != View.VISIBLE) {
            resultPanel.visibility = View.VISIBLE
            resultPanel.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_up))
        }

        // Ensure content can scroll under the floating result panel
        resultPanel.post {
            val extraPadding = resultPanel.height + dpToPx(16)
            scrollView.setPadding(scrollView.paddingLeft, scrollView.paddingTop, scrollView.paddingRight, extraPadding)
        }
    }

    private fun hideResultPanel() {
        resultPanel.visibility = View.GONE
        // Remove extra bottom padding when panel hidden
        scrollView.setPadding(scrollView.paddingLeft, scrollView.paddingTop, scrollView.paddingRight, 0)
    }

    private fun updateProgress() {
        val progress = ((currentQuestionIndex + 1) * 100) / questions.size
        
        // First animate to a value slightly higher than the actual progress
        val overshootProgress = minOf(progress + 4, 100) // Overshoot by 4%, but don't exceed 100%
        
        // First animation - overshoot
        val overshootAnim = ObjectAnimator.ofInt(progressBar, "progress", overshootProgress).apply {
            duration = 400
            interpolator = android.view.animation.AccelerateInterpolator(1.5f)
        }
        
        // Second animation - bounce back
        val bounceBackAnim = ObjectAnimator.ofInt(progressBar, "progress", progress).apply {
            duration = 200
            interpolator = android.view.animation.DecelerateInterpolator(1.5f)
        }
        
        // Start the first animation
        overshootAnim.start()
        
        // Start the bounce back animation after the first one completes
        overshootAnim.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                bounceBackAnim.start()
            }
        })
    }

    private fun finishLesson() {
        if (isTestMode) {
            // Compute weighted score out of 50
            var totalPoints = 0
            var correctCount = 0
            for (q in questions) {
                val given = normalizeAnswerKey(q.userAnswer ?: "")
                val correct = resolveCorrectKey(q)
                val ok = given.isNotEmpty() && given == correct
                lessonProgress.recordMistakeStreak(q.id, ok)
                if (ok) {
                    val w = testQuestionWeightById[q.id] ?: 0
                    totalPoints += w
                    correctCount += 1
                }
            }
            try { AchievementsManager(this).onTestCorrectAdded(correctCount) } catch (_: Throwable) { }
            // Extend streak for finishing a test
            var firstOfDay = false
            try { 
                firstOfDay = lessonProgress.updateStreakOnLessonCompleted() 
            } catch (_: Throwable) { }
            
            // Krok 153: legacy TestResultsActivity zrušena. Tato větev (legacy ad-hoc test v
            // MainActivity, spouštěný dřív jen z odstraněné TestAttemptActivity) je dnes
            // nedosažitelná — nic už nespouští MainActivity s EXTRA_IS_TEST_MODE=true. Pro
            // jistotu (kdyby v budoucnu vznikl nový vstupní bod) přesměrováváme na Compose
            // záložku Test, kde se nová zkouška spouští přes [cz.autokolk.ui.screens.test.TestScreen].
            android.util.Log.w("MainActivity", "Legacy isTestMode completion path hit — mělo by být nedosažitelné po kroku 153.")
            val intent = Intent(this, ComposeMainActivity::class.java)
                .putExtra(cz.autokolk.ui.navigation.ComposeNavIntent.EXTRA_OPEN_TAB, cz.autokolk.ui.navigation.ComposeNavIntent.OPEN_TAB_TEST)
            startActivity(intent)
            finish()
            return
        }

        // Calculate score for normal lesson flow
        val correctAnswers = questions.count { normalizeAnswerKey(it.userAnswer ?: "") == resolveCorrectKey(it) }
        val totalQuestions = questions.size

        if (!isReviewMode && category.isBlank()) {
            // Save progress only for normal lessons (not practice categories)
            val incorrectIds = questions.mapNotNull { question ->
                if (normalizeAnswerKey(question.userAnswer ?: "") != resolveCorrectKey(question)) question.id else null
            }.toSet()
            lessonProgress.saveLessonProgress(lessonNumber, incorrectIds)
        } else if (isReviewMode && category.isBlank()) {
            // Update progress after review
            val currentState = lessonProgress.getLessonState(lessonNumber)
            val remainingIncorrect = currentState.incorrectQuestionIds.toMutableSet()

            // Remove questions that were answered correctly in this review
            questions.forEach { reviewQuestion ->
                if (normalizeAnswerKey(reviewQuestion.userAnswer ?: "") == resolveCorrectKey(reviewQuestion)) {
                    remainingIncorrect.remove(reviewQuestion.id)
                }
            }
            
            // Save updated progress
            lessonProgress.saveLessonProgress(lessonNumber, remainingIncorrect)
        }

        // Update streak on finishing any session (lesson, review, or practice)
        var firstOfDay = false
        try {
            firstOfDay = lessonProgress.updateStreakOnLessonCompleted()
        } catch (e: Exception) {
            Log.e("MainActivity", "Failed updating streak", e)
        }

        // If this was a random session (e.g., from the hearts "+"), award +1 heart for >=50% correct
        val randomCount = intent.getIntExtra(EXTRA_RANDOM_COUNT, 0)
        var randomEarned = false
        if (randomCount > 0 && totalQuestions > 0) {
            if (correctAnswers * 2 >= totalQuestions) {
                val current = lessonProgress.getCurrentHearts()
                lessonProgress.setHearts(current + 1)
                randomEarned = true
            }
        }

        // Show results page.
        // Krok 153: legacy ResultsActivity zrušena, přesměrováno na Compose [ui.screens.results.ResultsComposeScreen]
        // se stejnými reálnými parametry (skóre, body, firstOfDay), jaké dřív dostávala legacy Activity.
        val finalDisplayLessonNumber = displayLessonNumber ?: lessonNumber
        val isPractice = category.isNotBlank()
        val isRandom = randomCount > 0
        val pointsAwarded = LessonPoints.computeLessonPointsAwarded(
            isPractice = isPractice,
            isRandom = isRandom,
            isReviewMode = isReviewMode,
            correctAnswers = correctAnswers,
            totalQuestions = totalQuestions,
        )
        if (pointsAwarded > 0) {
            lessonProgress.addPoints(pointsAwarded)
        }
        // Pozn.: legacy ResultsActivity měla speciální texty pro náhodný kvíz ("Získáváte extra
        // život!"/"Bohužel tentokrát bez extra života."). Compose ResultsComposeScreen tuto
        // variantu nerozlišuje a zobrazí obecný text — samotné udělení srdce (výše, `lessonProgress.setHearts`)
        // proběhne beze změny, jde jen o kosmetický rozdíl textu na výsledkové obrazovce pro tuto
        // okrajovou cestu (náhodný kvíz z "+" u srdcí na Home).
        val intent = Intent(this, ComposeMainActivity::class.java)
            .putExtra(cz.autokolk.ui.navigation.ComposeNavIntent.EXTRA_OPEN_TAB, cz.autokolk.ui.navigation.ComposeNavIntent.OPEN_RESULTS)
            .putExtra(cz.autokolk.ui.navigation.ComposeNavIntent.EXTRA_RESULTS_LESSON_ID, finalDisplayLessonNumber)
            .putExtra(cz.autokolk.ui.navigation.ComposeNavIntent.EXTRA_RESULTS_SCORE, correctAnswers)
            .putExtra(cz.autokolk.ui.navigation.ComposeNavIntent.EXTRA_RESULTS_TOTAL, totalQuestions)
            .putExtra(cz.autokolk.ui.navigation.ComposeNavIntent.EXTRA_RESULTS_FIRST_OF_DAY, firstOfDay)
            .putExtra(cz.autokolk.ui.navigation.ComposeNavIntent.EXTRA_RESULTS_POINTS_AWARDED, pointsAwarded)
            .putExtra(cz.autokolk.ui.navigation.ComposeNavIntent.EXTRA_RESULTS_FROM_PRACTICE, isPractice)
        // Interstitial reklamy: skutečné lekce (i review) zvedají počítadlo. Procvičování,
        // náhodné kvízy a testy se nezapočítávají — odpovídá pravidlům pro Compose flow.
        val isRealLesson = category.isBlank() && randomCount <= 0 && !isTestMode
        if (isRealLesson) {
            try {
                InterstitialAdController.onLessonCompleted(this)
            } catch (_: Throwable) {
            }
        }
        startActivity(intent)
        finish() // Return to HomePage after showing results
    }

    private fun buildTestModeQuestions(): List<Question> {
        testQuestionWeightById.clear()
        val random = kotlin.random.Random.Default

        fun pickFrom(categories: List<String>, count: Int, weight: Int, used: MutableSet<String>, acc: MutableList<Question>) {
            val pool = categories.flatMap { cat -> lessonProgress.getQuestionsForCategory(cat) }
            val uniquePool = pool.distinctBy { it.id }.toMutableList()
            uniquePool.shuffle(random)

            // Take without duplicates across global set
            val takeList = mutableListOf<Question>()
            for (q in uniquePool) {
                if (takeList.size >= count) break
                if (q.id !in used) {
                    takeList.add(q)
                    used.add(q.id)
                }
            }
            // If not enough, allow repeats by cycling previously selected for this bucket
            var idx = 0
            while (takeList.size < count && takeList.isNotEmpty()) {
                takeList.add(takeList[idx % takeList.size])
                idx += 1
            }
            // If still empty (no data), do nothing
            for (q in takeList) {
                acc.add(q.copy(userAnswer = null))
                testQuestionWeightById[q.id] = weight
            }
        }

        val result = mutableListOf<Question>()
        val usedIds = mutableSetOf<String>()

        // Group questions by category instead of shuffling
        // a) 10 from Def+Prav, 2 points each
        pickFrom(listOf("def", "prav"), 10, 2, usedIds, result)
        // b) 3 from Znak, 1 point each
        pickFrom(listOf("znak"), 3, 1, usedIds, result)
        // c) 4 from Bez, 2 points each
        pickFrom(listOf("bez"), 4, 2, usedIds, result)
        // d) 3 from Res, 4 points each
        pickFrom(listOf("res"), 3, 4, usedIds, result)
        // e) 2 from Voz, 1 point each
        pickFrom(listOf("voz"), 2, 1, usedIds, result)
        // f) 2 from Souv, 2 points each
        pickFrom(listOf("souv"), 2, 2, usedIds, result)
        // g) 1 from Med, 1 point
        pickFrom(listOf("med"), 1, 1, usedIds, result)

        // Group questions by category instead of shuffling
        val groupedResult = mutableListOf<Question>()
        val categories = listOf("Def", "Prav", "Znak", "Bez", "Res", "Voz", "Souv", "Med")
        
        // Calculate category counts and starting positions
        val categoryCounts = mutableMapOf<String, Int>()
        val categoryStarts = mutableMapOf<String, Int>()
        var currentIndex = 0
        
        for (category in categories) {
            val categoryQuestions = result.filter { it.category == category }
            if (categoryQuestions.isNotEmpty()) {
                categoryStarts[category] = currentIndex
                categoryCounts[category] = categoryQuestions.size
                groupedResult.addAll(categoryQuestions)
                currentIndex += categoryQuestions.size
            }
        }
        
        // Store for use in display
        testCategoryCounts = categoryCounts
        testCategoryStarts = categoryStarts
        
        return groupedResult
    }

    private fun deleteLegacyTempVideoOnly() {
        currentVideoFile?.let { f ->
            val marker = "${File.separator}video_asset_cache${File.separator}"
            if (!f.absolutePath.contains(marker)) {
                f.delete()
            }
        }
        currentVideoFile = null
    }

    private fun handleVideoDisplay(videoPath: String?) {
        try {
            deleteLegacyTempVideoOnly()

            if (videoPath == null) {
                questionVideo.stopPlayback()
                videoContainer.visibility = View.GONE
                videoModuleStatusText.visibility = View.GONE
                pendingVideoPath = null
                return
            }

            Log.d("MainActivity", "Loading video: $videoPath")

            val videoFileName = videoPath.substringAfterLast("/")
            val requiredModule = videoToModuleMap[videoFileName]

            if (requiredModule == null) {
                Log.e("MainActivity", "Video file '$videoFileName' not found in module map")
                videoContainer.visibility = View.GONE
                videoModuleStatusText.visibility = View.GONE
                pendingVideoPath = null
                return
            }

            if (!installedVideoModules.contains(requiredModule)) {
                Log.d("MainActivity", "Video module '$requiredModule' not installed yet, requesting installation")
                val request = SplitInstallRequest.newBuilder()
                    .addModule(requiredModule)
                    .build()

                splitInstallManager.startInstall(request)
                    .addOnSuccessListener { sessionId ->
                        Log.d("MainActivity", "Installation of module '$requiredModule' started (sessionId=$sessionId)")
                    }
                    .addOnFailureListener { exception ->
                        Log.e("MainActivity", "Failed to request installation for module '$requiredModule'", exception)
                    }

                pendingVideoPath = videoPath
                videoContainer.visibility = View.GONE
                videoModuleStatusText.visibility = View.VISIBLE
                videoModuleStatusText.text = getString(R.string.video_module_wait_install)
                return
            }

            try {
                Log.d("MainActivity", "Trying to load video from assets: $videoPath (module: $requiredModule)")

                val file = videoFileCache.getOrCopyFromAssets(assets, videoPath)
                if (file != null && file.length() > 0L) {
                    currentVideoFile = file
                    Log.d("MainActivity", "Video file ready, size: ${file.length()} bytes")
                    questionVideo.stopPlayback()
                    videoContainer.visibility = View.VISIBLE
                    videoModuleStatusText.visibility = View.GONE
                    questionVideo.setVideoPath(file.absolutePath)
                    questionVideo.requestFocus()
                    isPlaying = true
                    updatePlayPauseButton()
                    pendingVideoPath = null
                } else {
                    Log.e("MainActivity", "Video file is empty or missing after cache copy")
                    videoContainer.visibility = View.GONE
                    videoModuleStatusText.visibility = View.GONE
                    pendingVideoPath = null
                }
            } catch (e: VideoAssetFileCache.NoSpaceOnDeviceException) {
                Log.e("MainActivity", "No space while caching video", e)
                Toast.makeText(this, R.string.error_storage_full, Toast.LENGTH_LONG).show()
                videoContainer.visibility = View.GONE
                videoModuleStatusText.visibility = View.GONE
                pendingVideoPath = null
            } catch (e: FileNotFoundException) {
                Log.e("MainActivity", "Video file not found in assets: $videoPath", e)
                Log.e("MainActivity", "Module '$requiredModule' is installed but video not accessible")
                videoContainer.visibility = View.GONE
                videoModuleStatusText.visibility = View.GONE
                pendingVideoPath = null
            } catch (e: IOException) {
                Log.e("MainActivity", "Error loading video from assets", e)
                videoContainer.visibility = View.GONE
                videoModuleStatusText.visibility = View.GONE
                pendingVideoPath = null
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error loading video", e)
            videoContainer.visibility = View.GONE
            videoModuleStatusText.visibility = View.GONE
            pendingVideoPath = null
        }
    }

    override fun onPause() {
        super.onPause()
        questionVideo.pause()
        isPlaying = false
        updatePlayPauseButton()
        // Stop timer to avoid leaks; remember remaining time
        if (isTestMode) {
            testCountDownTimer?.cancel()
            testCountDownTimer = null
        }
    }

    override fun onResume() {
        super.onResume()
        if (videoContainer.visibility == View.VISIBLE && isPlaying) {
            questionVideo.start()
        }
        if (isTestMode) {
            // If we have remaining time (set by onTick), resume
            if (testTimeRemainingMs > 0L) {
                startOrResumeTestTimer(savedRemaining = testTimeRemainingMs, totalMs = null)
            }
        }
    }

    override fun onDestroy() {
        // Unregister SplitInstall listener
        if (::splitInstallManager.isInitialized) {
            try {
                splitInstallManager.unregisterListener(splitInstallStateListener)
            } catch (e: Exception) {
                Log.e("MainActivity", "Error unregistering split install listener", e)
            }
        }
        super.onDestroy()
        questionVideo.stopPlayback()
        deleteLegacyTempVideoOnly()
        testCountDownTimer?.cancel()
        testCountDownTimer = null
    }

    /** Sessions that end with a results-screen interstitial — preload while user answers questions. */
    private fun shouldPreloadLessonInterstitial(): Boolean {
        if (isTestMode) return false
        if (category.isNotBlank()) return false
        if (intent.getIntExtra(EXTRA_RANDOM_COUNT, 0) > 0) return false
        return true
    }

    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }

    private fun formatMs(ms: Long): String {
        val totalSec = (ms / 1000).toInt()
        val m = (totalSec / 60)
        val s = totalSec % 60
        return String.format(java.util.Locale.getDefault(), "%02d:%02d", m, s)
    }

    private fun startOrResumeTestTimer(savedRemaining: Long?, totalMs: Long?) {
        val duration = savedRemaining ?: totalMs ?: return
        testCountDownTimer?.cancel()
        testCountDownTimer = object : CountDownTimer(duration, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                testTimeRemainingMs = millisUntilFinished
                testTimerView.text = formatMs(millisUntilFinished)
            }
            override fun onFinish() {
                testTimeRemainingMs = 0L
                testTimerView.text = "00:00"
                // Time is up: end test and show results
                finishLesson()
            }
        }
        testCountDownTimer?.start()
    }

    private fun normalizeAnswerKey(key: String?): String {
        return key?.trim()?.lowercase()?.take(1) ?: ""
    }

    private fun resolveCorrectKey(question: Question): String {
        val rawNorm = normalizeAnswerText(question.correctAnswer)

        val aNorm = normalizeAnswerText(question.optionA)
        val bNorm = normalizeAnswerText(question.optionB)
        val cNorm = normalizeAnswerText(question.optionC)

        // 1) Prefer matching by normalized text content (handles punctuation and casing)
        if (rawNorm.isNotEmpty()) {
            if (rawNorm == aNorm) return "a"
            if (rawNorm == bNorm) return "b"
            if (rawNorm == cNorm) return "c"
        }

        // 2) If correctAnswer is exactly a key, accept it
        val rawKey = normalizeAnswerKey(question.correctAnswer)
        if (rawKey == "a" || rawKey == "b" || rawKey == "c") return rawKey

        // 3) Handle common yes/no synonyms mapping to whichever option matches
        val yesSynonyms = setOf("yes", "ano", "y", "true")
        val noSynonyms = setOf("no", "ne", "n", "false")
        if (rawNorm in yesSynonyms) {
            if (aNorm in yesSynonyms) return "a"
            if (bNorm in yesSynonyms) return "b"
            if (cNorm in yesSynonyms) return "c"
        }
        if (rawNorm in noSynonyms) {
            if (aNorm in noSynonyms) return "a"
            if (bNorm in noSynonyms) return "b"
            if (cNorm in noSynonyms) return "c"
        }

        // Unknown; UI will skip extra highlight
        return ""
    }

    private fun normalizeAnswerText(text: String?): String {
        val lower = text?.lowercase()?.trim() ?: ""
        // Remove non-alphanumeric letters in a Unicode-safe way without regex
        val builder = StringBuilder(lower.length)
        for (ch in lower) {
            if (ch.isLetterOrDigit()) builder.append(ch)
        }
        return builder.toString()
    }

    private fun capitalizeFirst(text: String): String {
        if (text.isEmpty()) return text
        return text.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }

    private fun mapCategoryDisplayName(code: String): String {
        val name = when (code.lowercase()) {
            LessonProgress.CATEGORY_USER_MISTAKES.lowercase() -> "tvoje chyby"
            "prav" -> "pravidla provozu a dopravní předpisy"
            "bez" -> "bezpečnost jízdy"
            "def" -> "základní definice"
            "znak" -> "značky"
            "res" -> "řešení dopravních situací"
            "voz" -> "podmínky provozu vozidla"
            "souv" -> "související předpisy"
            "med" -> "zdravotnická příprava"
            "cdt" -> "Předpisy pro jiné skupiny"
            else -> code
        }
        return capitalizeFirst(name)
    }
}