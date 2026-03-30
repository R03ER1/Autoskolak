package cz.autokolk

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import com.google.android.material.button.MaterialButton
import android.widget.ImageButton
import android.view.animation.AnimationUtils
import android.view.Gravity
import android.view.View
import android.widget.PopupWindow
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import android.content.res.ColorStateList
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.graphics.Color
import androidx.core.view.ViewCompat
import android.view.ViewGroup
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.ColorUtils
import android.graphics.drawable.GradientDrawable
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat

class HomeActivity : AutokolkActivity() {
    private lateinit var lessonProgress: LessonProgress
    private lateinit var buttonContainer: LinearLayout
    private lateinit var homeScrollView: android.widget.ScrollView
    private lateinit var streakButton: MaterialButton
    private lateinit var xpButton: MaterialButton
    private lateinit var heartsButton: MaterialButton
    private val lessonCircleColorByNumber: MutableMap<Int, Int> = mutableMapOf()
    private val displayNumberByLesson: MutableMap<Int, Int> = mutableMapOf()
    private val lessonIconCache: MutableMap<String, android.graphics.drawable.Drawable> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Set the status bar color to black
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        lessonProgress = LessonProgress(this)
        buttonContainer = findViewById(R.id.lessonButtonsContainer)
        homeScrollView = findViewById(R.id.homeScrollView)
        streakButton = findViewById(R.id.streakButton)
        xpButton = findViewById(R.id.xpButton)
        heartsButton = findViewById(R.id.heartsButton)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        // Force full transparency at runtime to override any theme defaults
        bottomNav.setBackgroundColor(Color.TRANSPARENT)
        bottomNav.background = null
        ViewCompat.setElevation(bottomNav, 0f)
        bottomNav.backgroundTintList = android.content.res.ColorStateList.valueOf(Color.TRANSPARENT)
        bottomNav.itemRippleColor = null
        try {
            bottomNav.setItemBackground(null)
        } catch (_: Throwable) { }
        try {
            bottomNav.isItemActiveIndicatorEnabled = false
        } catch (_: Throwable) { }
        
        createLessonButtons()
        updateStreakHeader()
        updatePointsHeader()
        updateHeartsHeader()

        streakButton.setOnClickListener {
            showStreakBottomSheet()
        }
        xpButton.setOnClickListener { showPointsBottomSheet() }
        heartsButton.setOnClickListener { showHeartsBottomSheet() }

        // Select Home by default
        bottomNav.selectedItemId = R.id.nav_home
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true // already here
                R.id.nav_lion -> {
                    val intent = Intent(this, AlexActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_exercise -> {
                    val intent = Intent(this, PracticeActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_test -> {
                    val intent = Intent(this, TestAttemptActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_settings -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }

        // Ask for notifications on Android 13+ if not granted
        requestNotificationsIfNeeded()

        // When Home is tapped, center next upcoming lesson on screen (if possible)
        bottomNav.setOnItemReselectedListener { item ->
            if (item.itemId == R.id.nav_home) {
                centerNextLesson()
            }
        }

        // Tutorial overlays - show welcome first, then home guide
        TutorialManager.showSequenceIfNeeded(
            this,
            listOf(
                "tutorial_welcome" to "Ahoj, já jsem Alex, a teď tě provedu aplikací Autoškolák.",
                "tutorial_home" to "Tady je cesta, kde postupně projdeš všechny otázky autoškoly. V každém kolečku je 10 otázek z určité kategorie."
            )
        )

        // Random events after tutorials are completed at least once
        val manager = RandomEventManager(this)
        manager.maybeShowEvent(this) {
            updatePointsHeader()
            updateHeartsHeader()
        }
    }

    private fun requestNotificationsIfNeeded() {
        if (android.os.Build.VERSION.SDK_INT < 33) return
        val permission = android.Manifest.permission.POST_NOTIFICATIONS
        if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) return
        val launcher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ -> }
        launcher.launch(permission)
    }

    private fun capitalizeFirst(text: String): String {
        if (text.isEmpty()) return text
        return text.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }

    private fun mapSubcategoryDisplayName(code: String): String {
        val name = when (code.lowercase()) {
            // Prav - pravidla provozu a dopravní předpisy
            "neh" -> "nehody"
            "pol" -> "policie"
            "sme" -> "změny směru a přednosti"
            "sta" -> "stání a zastavení"
            "mhd" -> "kontakt s mhd"
            "pra" -> "pruhy, zóny a rychlosti"
            "riz" -> "řízení a bezpečnosti vozidla"

            // Bez - bezpečnost jízdy
            "ost" -> "vztah k ostatním účastníkům a vozidlům"
            "rid" -> "chování při řízení"
            "pre" -> "přechody a chodci"
            "sil" -> "vztah k veškerému okolnímu vybavení"

            // Def - základní definice
            "uca" -> "účastníci"
            "aut" -> "vozidla"
            "vec" -> "místa"
            "cho" -> "chování"
            "poj" -> "obecné pojmy"

            // Znak - značky
            "sem" -> "semafory"
            "pok" -> "pokyny policie"
            "slo" -> "dopravní sloupky"
            "vod" -> "vodorovné značky"
            "vys" -> "výstražné značky"
            "zak" -> "zákazové"
            "pri" -> "příkazové značky"
            "inf" -> "informativní značky"
            "upr" -> "značky upravující přednost"

            // Res - řešení dopravních situací
            "kri" -> "křižovatky"
            "neb" -> "nebezpečí na silnici"

            // Voz - podmínky provozu vozidla
            "sou" -> "systémy a bezpečnost"
            "sve" -> "součásti auta"
            "nak" -> "náklady auta"
            "spo" -> "cestující"
            "stk" -> "kontroly"

            // Souv - související předpisy
            "pru" -> "řidičáky, ohlašování změn, úřady"
            "l" -> "režim l17"
            "mir" -> "míry vozidla, hmotnosti a kontroly"
            "pro" -> "provozovatel a pojištění"
            else -> code
        }
        return capitalizeFirst(name)
    }

    private fun mapCategoryDisplayName(code: String): String {
        val name = when (code.lowercase()) {
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

    override fun onResume() {
        super.onResume()
        // Ensure points and header are up to date
        lessonProgress.normalizeStreakForToday()
        updateStreakHeader()
        updatePointsHeader()
        updateHeartsHeader()
        updateLessonButtons()
        // Ensure bottom nav reflects that we're on Home when returning via CLEAR_TOP/SINGLE_TOP
        try {
            val bottomNav = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNavigation)
            if (bottomNav.selectedItemId != R.id.nav_home) {
                bottomNav.selectedItemId = R.id.nav_home
            }
        } catch (_: Throwable) { }
    }

    

    private fun createLessonButtons() {
        buttonContainer.removeAllViews()
        displayNumberByLesson.clear()
        val plan = lessonProgress.getGlobalLessonPlan()
        if (plan.isEmpty()) {
            updateLessonButtons()
            return
        }

        val density = resources.displayMetrics.density
        var globalWaveIndex = 0

		// Reorder lessons so that ONLY the first 14 positions contain "def" items,
		// and remaining positions are non-DEF items only.
        val defSubcategoryCodes = setOf("uca", "aut", "vec", "cho", "poj")
		val defLessonsAll = plan.filter { it.subcategory.trim().lowercase() in defSubcategoryCodes }
		val defBlock = defLessonsAll.take(14)
		val nonDefLessons = plan.filter { it.subcategory.trim().lowercase() !in defSubcategoryCodes }
		val reorderedLessons = defBlock + nonDefLessons

		val totalLessons = reorderedLessons.size
		val totalSections = (totalLessons + LessonProgress.QUESTIONS_PER_LESSON - 1) / LessonProgress.QUESTIONS_PER_LESSON

		// Utility views for section headers
		fun addCenteredTitle(text: String) {
			val titleView = TextView(this).apply {
				this.text = text
				textSize = 20f
				setTextColor(ContextCompat.getColor(this@HomeActivity, R.color.text_primary))
				gravity = Gravity.CENTER
				setPadding(0, (12 * density).toInt(), 0, (6 * density).toInt())
			}
			buttonContainer.addView(titleView)
		}

		fun addBigCenteredTitle(text: String) {
			val titleView = TextView(this).apply {
				this.text = text
				textSize = 28f
				setTextColor(ContextCompat.getColor(this@HomeActivity, R.color.text_primary))
				gravity = Gravity.CENTER
				setPadding(0, (24 * density).toInt(), 0, (12 * density).toInt())
			}
			buttonContainer.addView(titleView)
		}

		fun addDivider() {
			val divider = View(this).apply {
				layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (1 * density).toInt()).apply {
					(topMargin) = (6 * density).toInt()
					(bottomMargin) = (12 * density).toInt()
				}
				setBackgroundColor(Color.parseColor("#33444444"))
			}
			buttonContainer.addView(divider)
		}

		val defCount = defBlock.size
		if (defCount > 0) {
			addCenteredTitle("Základní pojmy")
			addDivider()
		}

		// Define non-DEF grouped sections and their sizes
		val nonDefGroups: List<Pair<String, Int>> = listOf(
			"Začátečník" to 10,
			"Pokročilý" to 15,
			"Profesionál" to 20,
			"Znalec" to 20,
			"Génius" to 20,
			"Alfa samec" to 20
		)
		var currentGroupIndex = -1
		var nextBoundary = defCount // index in reorderedLessons where next group should start
		var startedSkoroHotovo = false

		fun maybeStartNextGroup(currentIdx: Int) {
			if (currentIdx < defCount) return
			if (currentGroupIndex == -1 && currentIdx == defCount) {
				// Start first defined group
				currentGroupIndex = 0
				nextBoundary = defCount + nonDefGroups[0].second
				addCenteredTitle(nonDefGroups[0].first)
				addDivider()
				return
			}
			// Advance through defined groups as boundaries are reached
			while (currentGroupIndex in 0..(nonDefGroups.lastIndex) && currentIdx == nextBoundary) {
				currentGroupIndex++
				if (currentGroupIndex <= nonDefGroups.lastIndex) {
					addCenteredTitle(nonDefGroups[currentGroupIndex].first)
					addDivider()
					nextBoundary += nonDefGroups[currentGroupIndex].second
				} else if (!startedSkoroHotovo) {
					addCenteredTitle("Skoro hotovo!")
					addDivider()
					startedSkoroHotovo = true
					// No further boundaries to track
					nextBoundary = Int.MAX_VALUE
				}
			}
		}

        for (sectionIndex in 0 until totalSections) {
            // Compute section color using smooth rainbow mapping
            val hueRange = 300f
            val hueStart = 30f
            val hue = (hueStart + (sectionIndex.toFloat() / maxOf(1, totalSections - 1).toFloat()) * hueRange) % 360f
            val hsv = floatArrayOf(hue, 0.7f, 0.95f)
            val sectionColor = android.graphics.Color.HSVToColor(hsv)

			// Add 10 lessons (or remaining) in this section, based on reordered order
			val startIndexInclusive = sectionIndex * LessonProgress.QUESTIONS_PER_LESSON
			val endIndexExclusive = minOf(startIndexInclusive + LessonProgress.QUESTIONS_PER_LESSON, totalLessons)
			for (idx in startIndexInclusive until endIndexExclusive) {
				// Insert group headers according to requested grouping
				maybeStartNextGroup(idx)

				val lessonNumber = reorderedLessons[idx].lessonNumber
                // Map original lesson number to display order index (1-based)
                displayNumberByLesson[lessonNumber] = idx + 1
                val lessonView = LayoutInflater.from(this).inflate(R.layout.item_lesson, buttonContainer, false)
                val lessonButton = lessonView.findViewById<ImageButton>(R.id.lessonButton)
                val lessonText = lessonView.findViewById<TextView>(R.id.lessonText)

                // Set icon based on subcategory
                run {
                    val entry = plan.find { it.lessonNumber == lessonNumber }
                    val sub = entry?.subcategory?.trim()?.lowercase().orEmpty()
                    val iconFile = mapSubcategoryToIconAsset(sub)
                    val drawable = loadLessonIconFromAssets(iconFile)
                    if (drawable != null) {
                        lessonButton.setImageDrawable(drawable)
                        // Make icon about 50% smaller by adding larger padding (12dp -> 24dp)
                        val pad = (24 * density).toInt()
                        lessonButton.setPadding(pad, pad, pad, pad)
                    }
                }

                val subtitle = "10 otázek"
                lessonText.text = subtitle

                // Snake-like horizontal offset with smooth wave
                (lessonView.layoutParams as? LinearLayout.LayoutParams)?.apply {
                    topMargin = (8 * density).toInt()
                    bottomMargin = (8 * density).toInt()
                    val baseMaxOffsetDp = 250.0
                    val centerDp = baseMaxOffsetDp / 2.0
                    val amplitudeDp = centerDp * 0.8
                    val periodItems = 8.0
                    val angle = (globalWaveIndex.toDouble() / periodItems) * (2.0 * PI)
                    val offsetDp = centerDp + amplitudeDp * sin(angle)
                    marginStart = (offsetDp * density).toInt()
                }

                lessonCircleColorByNumber[lessonNumber] = sectionColor
                lessonButton.tag = lessonNumber

                lessonButton.setOnClickListener { v ->
                    val state = lessonProgress.getLessonState(lessonNumber)
                    val isReview = state.completed && state.incorrectQuestionIds.isNotEmpty()
                    showLessonPopup(v, lessonNumber, subtitle, isReview)
                }

                buttonContainer.addView(lessonView)
                globalWaveIndex++
            }
		}
		// Add a big celebratory title at the end of the route
		addBigCenteredTitle("Hotovo!")
		updateLessonButtons()
    }

    private fun mapSubcategoryToIconAsset(code: String): String {
        return when (code) {
            // CDT categories (C, D, T) - use tractor icon
            "c", "d", "t" -> "tractor-icon.png"
            // Accidents and relations to other participants
            "neh", "ost" -> "accident-icon.png"
            // Command signs
            "pri" -> "arrows-up-icon.png"
            // Prohibition signs
            "zak" -> "ban-sign-icon.png"
            // Public transport
            "mhd" -> "bus-symbol-icon.png"
            // Operator/insurance and road hazards
            "pro", "neb" -> "car-back-collision-icon.png"
            // Authorities, documents, and L17
            "pru", "l" -> "car-document-icon.png"
            // Passengers
            "spo" -> "car-door-icon.png"
            // Horizontal markings and surrounding equipment
            "vod", "sil" -> "car-driving-on-road-icon.png"
            // Technical inspections
            "stk" -> "car-repair-service-icon.png"
            // Vehicle dimensions/weights
            "mir" -> "car-report-icon.png"
            // Vehicles
            "aut" -> "car-top-view-icon.png"
            // Informative signs
            "inf" -> "construction-sign-icon.png"
            // Traffic cones / posts
            "slo" -> "construction-traffic-cone-icon.png"
            // Car parts
            "sve" -> "engine-motor-icon.png"
            // Warning signs
            "vys" -> "exclamation-triangle-icon.png"
            // Stopping/standing and priority-regulating signs
            "sta", "upr" -> "hand-line-icon.png"
            // Participants
            "uca" -> "hatchback-car-icon.png"
            // Systems and safety
            "sou" -> "lubricant-oil-icon.png"
            // Police and police instructions
            "pol", "pok" -> "officer-icon.png"
            // Lanes, zones, speeds and places
            "pra", "vec" -> "road-route-icon.png"
            // Vehicle costs
            "nak" -> "roadside-car-assistance-icon.png"
            // Direction changes and priority
            "sme" -> "route-arrows-up-icon.png"
            // Driving behavior and general behavior
            "rid", "cho" -> "seatbelt-icon.png"
            // Driving and vehicle safety
            "riz" -> "steering-wheel-icon.png"
            // Traffic lights and intersections
            "sem", "kri" -> "traffic-light-icon.png"
            // General terms
            "poj" -> "turn-right-arrow-icon.png"
            // Crosswalks and pedestrians
            "pre" -> "zebra-crossing-sign-icon.png"
            // Medical preparedness
            "med" -> "first-aid-kit.png"
            // Defaults / unknown
            else -> "traffic-light-icon.png"
        }
    }

    private fun loadLessonIconFromAssets(fileName: String): android.graphics.drawable.Drawable? {
        if (fileName.isBlank()) return null
        lessonIconCache[fileName]?.let { return it }
        return try {
            val am = assets
            am.open("images/lesson_icons/$fileName").use { input ->
                val bmp = android.graphics.BitmapFactory.decodeStream(input)
                val dr = android.graphics.drawable.BitmapDrawable(resources, bmp)
                lessonIconCache[fileName] = dr
                dr
            }
        } catch (_: Throwable) {
            null
        }
    }

    private fun getFirstLessonOnMap(): Int {
        val plan = lessonProgress.getGlobalLessonPlan()
        if (plan.isEmpty()) return 1
        val defSubcategoryCodes = setOf("uca", "aut", "vec", "cho", "poj")
        val defLessonsAll = plan.filter { it.subcategory.trim().lowercase() in defSubcategoryCodes }
        val defBlock = defLessonsAll.take(14)
        val nonDefLessons = plan.filter { it.subcategory.trim().lowercase() !in defSubcategoryCodes }
        val reorderedLessons = defBlock + nonDefLessons
        return reorderedLessons.firstOrNull()?.lessonNumber ?: 1
    }

    private fun updateLessonButtons() {
        val states = lessonProgress.getAllLessonStates()
        val fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)

        // Update all lesson views (works with nested curvy containers)
        for (i in 0 until buttonContainer.childCount) {
            val view = buttonContainer.getChildAt(i)
            updateLessonViewsIn(view, states, fadeIn)
        }
    }

    private fun centerNextLesson() {
        try {
            val hasAnyProgress = lessonProgress.getAllLessonStates().isNotEmpty()
            val nextLesson = if (hasAnyProgress) lessonProgress.getNextAvailableLesson() else getFirstLessonOnMap()

            // Find the view for this lessonNumber by scanning children with matching tag
            var targetView: View? = null
            fun search(view: View) {
                if (targetView != null) return
                if (view is ViewGroup) {
                    for (i in 0 until view.childCount) {
                        search(view.getChildAt(i))
                        if (targetView != null) return
                    }
                } else if (view.id == R.id.lessonButton && view is ImageButton) {
                    val tag = view.tag
                    if (tag is Int && tag == nextLesson) {
                        targetView = view
                    }
                }
            }
            search(buttonContainer)

            val buttonView = targetView ?: return

            // Compute scroll so the button is centered vertically within the ScrollView
            val scrollView = homeScrollView
            val contentView = buttonContainer
            val buttonY = (buttonView.parent as View).top + buttonView.top // parent item holds margins
            val desiredScrollY = (buttonY - (scrollView.height / 2) + (buttonView.height / 2)).coerceIn(0, maxOf(0, contentView.height - scrollView.height))

            scrollView.smoothScrollTo(0, desiredScrollY)
        } catch (_: Throwable) { }
    }

    private fun updateLessonViewsIn(view: View, states: List<LessonState>, fadeIn: android.view.animation.Animation) {
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                updateLessonViewsIn(view.getChildAt(i), states, fadeIn)
            }
            return
        }
        if (view.id == R.id.lessonButton && view is ImageButton) {
            val parent = view.parent as? ViewGroup
            val lessonText = parent?.findViewById<TextView>(R.id.lessonText)
            val lessonNumberTag = view.tag
            val lessonNumber = if (lessonNumberTag is Int) lessonNumberTag else null
            val state = if (lessonNumber != null) states.find { it.lessonNumber == lessonNumber } else null
            val sectionColor = if (lessonNumber != null) lessonCircleColorByNumber[lessonNumber] else null

            fun buildHueShiftGradient(baseColor: Int): GradientDrawable {
                // Create a vertical gradient that shifts hue while keeping similar saturation/value
                val hsv = FloatArray(3)
                Color.colorToHSV(baseColor, hsv)
                val baseHue = hsv[0]
                val sat = hsv[1]
                val valv = hsv[2]
                // Shift hue by 30 degrees (clockwise) for color transition
                val topHue = (baseHue - 20f + 360f) % 360f
                val bottomHue = (baseHue + 20f) % 360f
                val top = Color.HSVToColor(floatArrayOf(topHue, sat, valv))
                val bottom = Color.HSVToColor(floatArrayOf(bottomHue, sat, valv))
                return GradientDrawable(GradientDrawable.Orientation.TL_BR, intArrayOf(top, bottom)).apply {
                    shape = GradientDrawable.OVAL
                }
            }

            view.apply {
                isEnabled = true
                
                // Remove any default styling that might interfere
                backgroundTintList = null
                elevation = 0f
                stateListAnimator = null

                if (state?.completed == true) {
                    val incorrectSafe = state.incorrectQuestionIds ?: emptySet()
                    val correct = LessonProgress.QUESTIONS_PER_LESSON - incorrectSafe.size
                    val progress = correct.toFloat() / LessonProgress.QUESTIONS_PER_LESSON.toFloat()
                    val trackColor = ContextCompat.getColor(context, R.color.wrong_answer)
                    val progressColor = sectionColor ?: ContextCompat.getColor(context, R.color.correct_answer)
                    val stroke = resources.displayMetrics.density * 2f
                    
                    // Create a layer list drawable with the ring
                    // Thicker ring drawn closer to the outer edge
                    val ringStroke = resources.displayMetrics.density * 6f
                    val ringGap = resources.displayMetrics.density * 2f
                    val ringDrawable = RingProgressDrawable(progress, trackColor, progressColor, ringStroke, ringGap)
                    val base = run {
                        val baseClr = sectionColor ?: ContextCompat.getColor(context, R.color.button_normal)
                        buildHueShiftGradient(baseClr)
                    }
                    val layerDrawable = android.graphics.drawable.LayerDrawable(arrayOf(
                        base,
                        ringDrawable
                    ))
                    // Inset adjusted for larger button so the ring sits outside
                    val baseInset = (resources.displayMetrics.density * 12f).toInt()
                    layerDrawable.setLayerInset(0, baseInset, baseInset, baseInset, baseInset)
                    background = layerDrawable
                } else {
                    val base = run {
                        val normal = sectionColor ?: ContextCompat.getColor(context, R.color.button_normal)
                        buildHueShiftGradient(normal)
                    }
                    background = base
                }
                startAnimation(fadeIn)
            }

            lessonText?.apply {
                if (state?.completed == true) {
                    val incorrectCount = (state.incorrectQuestionIds ?: emptySet()).size
                    val currentText = text.toString()
                    val baseText = currentText.substringBefore(" (")
                    text = if (incorrectCount > 0) {
                        "$baseText (${(state.incorrectQuestionIds ?: emptySet()).size} chyb)"
                    } else {
                        "$baseText ✓"
                    }
                    val useBase = sectionColor ?: ContextCompat.getColor(context, R.color.button_normal)
                    val luminance = ColorUtils.calculateLuminance(useBase)
                    val textColor = if (luminance < 0.5) ContextCompat.getColor(context, android.R.color.white) else ContextCompat.getColor(context, android.R.color.black)
                    setTextColor(textColor)
                } else {
                    val normal = sectionColor ?: ContextCompat.getColor(context, R.color.button_normal)
                    val darker = ColorUtils.blendARGB(normal, android.graphics.Color.BLACK, 0.45f)
                    val textColor = if (ColorUtils.calculateLuminance(darker) < 0.5) ContextCompat.getColor(context, android.R.color.white) else ContextCompat.getColor(context, R.color.text_primary)
                    setTextColor(textColor)
                }
                startAnimation(fadeIn)
            }
        }
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

    private fun showLessonPopup(anchor: View, lessonNumber: Int, subtitle: String, isReview: Boolean) {
        val popupView = layoutInflater.inflate(R.layout.popup_lesson_info, null)
        val window = PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true)
        // We'll run a custom grow+fade animation; disable built-in window animation
        window.animationStyle = 0

        val titleText = popupView.findViewById<TextView>(R.id.popupTitle)
        val categoryText = popupView.findViewById<TextView>(R.id.popupCategory)
        val progressText = popupView.findViewById<TextView>(R.id.popupProgress)
        val startButton = popupView.findViewById<MaterialButton>(R.id.popupStartButton)

		// Title: show subcategory progress (e.g., "Křižovatky 2/5")
        val plan = lessonProgress.getGlobalLessonPlan()
        val entry = plan.find { it.lessonNumber == lessonNumber }
        val displayNum = displayNumberByLesson[lessonNumber] ?: lessonNumber
        titleText.text = if (entry != null) {
			val cat = entry.category.trim()
			val sub = entry.subcategory.trim()
			if (sub.isBlank()) {
				// No subcategory: show category name with category X/Y
				val sameCategory = plan
					.filter { it.category.trim().equals(cat, ignoreCase = true) }
					.sortedBy { it.lessonNumber }
				val idx = sameCategory.indexOfFirst { it.lessonNumber == lessonNumber }.let { if (it >= 0) it else 0 }
				val levelIndex = (idx + 1).coerceAtLeast(1)
				val totalLevels = sameCategory.size.coerceAtLeast(1)
				"${mapCategoryDisplayName(cat)} ${levelIndex}/${totalLevels}"
			} else {
				// Has subcategory: show subcategory name with subcategory X/Y
				var sameSubcategory = plan
					.filter { it.category.trim().equals(cat, ignoreCase = true) && it.subcategory.trim().equals(sub, ignoreCase = true) }
					.sortedBy { it.lessonNumber }
				if (sameSubcategory.isEmpty()) {
					// Fallback: group by subcategory only, regardless of category
					sameSubcategory = plan
						.filter { it.subcategory.trim().equals(sub, ignoreCase = true) }
						.sortedBy { it.lessonNumber }
				}
				val foundByNumber = sameSubcategory.indexOfFirst { it.lessonNumber == lessonNumber }
				val foundByEquality = if (foundByNumber == -1) sameSubcategory.indexOf(entry) else -1
				val idx = when {
					foundByNumber >= 0 -> foundByNumber
					foundByEquality >= 0 -> foundByEquality
					else -> 0
				}
				val subLevelIndex = (idx + 1).coerceAtLeast(1)
				val subTotalLevels = sameSubcategory.size.coerceAtLeast(1)
				"${mapSubcategoryDisplayName(sub)} ${subLevelIndex}/${subTotalLevels}"
			}
        } else {
            "Lekce ${displayNum}"
		}
        // Subtitle: two separate views
        val categoryLine = if (entry != null) mapCategoryDisplayName(entry.category) else ""
        val stateForLesson = lessonProgress.getLessonState(lessonNumber)
            val progressLine = when {
            !stateForLesson.completed -> ""
            (stateForLesson.incorrectQuestionIds ?: emptySet()).isEmpty() -> "Hotovo"
            else -> "Zbývá ${(stateForLesson.incorrectQuestionIds ?: emptySet()).size} otázek"
        }
        categoryText.text = categoryLine
        progressText.text = progressLine

        var isDismissing = false
        fun animateAndDismiss(after: (() -> Unit)? = null) {
            if (isDismissing) return
            isDismissing = true
            try {
                val content = window.contentView
                content.animate()
                    .scaleX(0.2f)
                    .scaleY(0.2f)
                    .alpha(0f)
                    .setDuration(75L)
                    .setInterpolator(android.view.animation.AccelerateDecelerateInterpolator())
                    .withEndAction {
                        try { window.dismiss() } catch (_: Throwable) {}
                        after?.invoke()
                    }
                    .start()
            } catch (_: Throwable) {
                try { window.dismiss() } catch (_: Throwable) {}
                after?.invoke()
            }
        }

        // Gating: allow starting only if already tried/completed, or it's the next lesson.
        // If no progress yet, allow only the first lesson shown on the map (Základní pojmy block).
        val tried = lessonProgress.getLessonState(lessonNumber).completed
        val hasAnyProgress = lessonProgress.getAllLessonStates().isNotEmpty()
        val nextAllowed = if (hasAnyProgress) lessonProgress.getNextAvailableLesson() else getFirstLessonOnMap()
        val canStart = tried || lessonNumber == nextAllowed

        // Always show info, but disable starting when locked
        startButton.isEnabled = canStart
        if (!canStart) {
            startButton.alpha = 0.5f
        } else {
            startButton.alpha = 1f
        }

        startButton.setOnClickListener {
            if (!canStart) return@setOnClickListener
            val prefs = getSharedPreferences("lesson_progress", MODE_PRIVATE)
            val infiniteLives = prefs.getBoolean("infinite_lives", false)
            val hearts = lessonProgress.getCurrentHearts()
            if (!infiniteLives && hearts <= 0) {
                // Block starting when no lives; show countdown bottom sheet
                animateAndDismiss { showNoHeartsBottomSheet() }
                return@setOnClickListener
            }
            animateAndDismiss { startQuiz(lessonNumber, isReview) }
        }

        window.isOutsideTouchable = true
        window.elevation = 24f

        // No background blur; keep only the popup

        // Light shadow highlight on the tapped star
        anchor.translationZ = 8f

        // Reset star elevation when popup is dismissed
        window.setOnDismissListener {
            anchor.translationZ = 0f
        }

        // Center horizontally (using showAsDropDown); clamp within screen bounds with a small margin
        val marginPx = (12 * resources.displayMetrics.density).toInt()
        try {
            val yOffset = (8 * resources.displayMetrics.density).toInt()
            window.contentView.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            val popupWidth = window.contentView.measuredWidth
            val popupHeight = window.contentView.measuredHeight

            val location = IntArray(2)
            anchor.getLocationOnScreen(location)
            val anchorX = location[0]
            val anchorY = location[1]

            val displayMetrics = resources.displayMetrics
            val screenWidth = displayMetrics.widthPixels
            val screenHeight = displayMetrics.heightPixels

            // Compute x so that the popup is centered relative to the screen while using showAsDropDown
            var x = ((screenWidth - popupWidth) / 2) - anchorX
            var y = yOffset

            // Clamp horizontally with margins
            val desiredLeft = anchorX + x
            val desiredRight = desiredLeft + popupWidth
            if (desiredLeft < marginPx) x += (marginPx - desiredLeft)
            if (desiredRight > screenWidth - marginPx) x -= (desiredRight - (screenWidth - marginPx))

            // Clamp vertically (relative y includes yOffset)
            val desiredTop = anchorY + anchor.height + y
            val desiredBottom = desiredTop + popupHeight
            if (desiredTop < marginPx) y += (marginPx - desiredTop)
            if (desiredBottom > screenHeight - marginPx) y -= (desiredBottom - (screenHeight - marginPx))

            window.isClippingEnabled = true
            window.showAsDropDown(anchor, x, y, Gravity.START)

            // Intercept outside touches to play close animation
            window.isOutsideTouchable = true
            window.setTouchInterceptor { _, event ->
                if (event?.action == MotionEvent.ACTION_OUTSIDE) {
                    animateAndDismiss()
                    true
                } else false
            }

            // Run a grow-from-anchor animation on the popup content
            try {
                val content = window.contentView
                val finalLeft = anchorX + x
                val pivotX = ((anchorX + anchor.width / 2f) - finalLeft).coerceIn(0f, content.measuredWidth.toFloat())
                content.pivotX = pivotX
                content.pivotY = 0f
                content.scaleX = 0.2f
                content.scaleY = 0.2f
                content.alpha = 0f
                content.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .alpha(1f)
                    .setDuration(90L)
                    .setInterpolator(android.view.animation.AccelerateDecelerateInterpolator())
                    .start()
            } catch (_: Throwable) { }
        } catch (_: Throwable) {
            // Fallback: center relative to root view
            window.showAtLocation(anchor.rootView, Gravity.CENTER, 0, 0)
        }
    }

    private fun showStreakBottomSheet() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.streak_bottom_sheet, null)
        dialog.setContentView(view)

        val streak = lessonProgress.getCurrentStreak()
        view.findViewById<android.widget.ImageView>(R.id.bottomSheetFlame).setImageResource(R.drawable.ic_streak)
        view.findViewById<TextView>(R.id.bottomSheetStreakNumber).text = streak.toString()
        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.bottomSheetOk).setOnClickListener {
            dialog.dismiss()
        }
        // Hide actions that are only for hearts
        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.bottomSheetPlus)?.visibility = View.GONE
        view.findViewById<View>(R.id.bottomSheetRewardContainer)?.visibility = View.GONE
        dialog.show()
    }

    private fun showNoHeartsBottomSheet() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.streak_bottom_sheet, null)
        dialog.setContentView(view)

        // Reuse the same layout ids but with different content
        val emoji = view.findViewById<android.widget.ImageView>(R.id.bottomSheetFlame)
        val number = view.findViewById<TextView>(R.id.bottomSheetStreakNumber)
        val subtitle = view.findViewById<TextView>(R.id.bottomSheetSubtitle)
        emoji.setImageResource(R.drawable.ic_live)
        number.text = "0"

        fun format(ms: Long): String {
            val totalSec = (ms / 1000).toInt()
            val m = (totalSec / 60) % 60
            val s = totalSec % 60
            return String.format(java.util.Locale.getDefault(), "%02d:%02d", m, s)
        }

        var handler: android.os.Handler? = android.os.Handler(mainLooper)
        val runnable = object : Runnable {
            override fun run() {
                val until = lessonProgress.millisUntilNextHeart()
                subtitle.text = "Do doplnění života: ${format(until)}"
                handler?.postDelayed(this, 1000L)
            }
        }
        runnable.run()

        dialog.setOnDismissListener {
            handler?.removeCallbacksAndMessages(null)
            handler = null
        }
        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.bottomSheetOk).setOnClickListener {
            dialog.dismiss()
        }
        // Hide actions that are only for hearts
        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.bottomSheetPlus)?.visibility = View.GONE
        view.findViewById<View>(R.id.bottomSheetRewardContainer)?.visibility = View.GONE
        dialog.show()
    }

    private fun showPointsBottomSheet() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.streak_bottom_sheet, null)
        dialog.setContentView(view)

        val points = lessonProgress.getTotalPoints()
        view.findViewById<android.widget.ImageView>(R.id.bottomSheetFlame).setImageResource(R.drawable.ic_coin)
        view.findViewById<TextView>(R.id.bottomSheetStreakNumber).text = points.toString()
        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.bottomSheetOk).setOnClickListener {
            dialog.dismiss()
        }
        // Hide actions that are only for hearts
        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.bottomSheetPlus)?.visibility = View.GONE
        view.findViewById<View>(R.id.bottomSheetRewardContainer)?.visibility = View.GONE
        dialog.show()
    }

    private fun showHeartsBottomSheet() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.streak_bottom_sheet, null)
        dialog.setContentView(view)

        val emoji = view.findViewById<android.widget.ImageView>(R.id.bottomSheetFlame)
        val number = view.findViewById<TextView>(R.id.bottomSheetStreakNumber)
        val subtitle = view.findViewById<TextView>(R.id.bottomSheetSubtitle)
        val plusButton = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.bottomSheetPlus)
        val okButton = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.bottomSheetOk)
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
        okButton.setOnClickListener {
            dialog.dismiss()
        }
        plusButton?.setOnClickListener {
            val intent = android.content.Intent(this, MainActivity::class.java)
            intent.putExtra(MainActivity.EXTRA_RANDOM_COUNT, 10)
            startActivity(intent)
            dialog.dismiss()
        }
        HeartsRewardAds.wireForHeartsSheet(this, view, lessonProgress, number, plusButton, okButton)
        dialog.show()
    }

    private fun startQuiz(lessonNumber: Int, isReview: Boolean) {
        // Global guard: prevent starting lessons other than already tried or the next one.
        // If no progress yet, allow only the first lesson shown on the map (Základní pojmy block).
        run {
            val tried = lessonProgress.getLessonState(lessonNumber).completed
            val hasAnyProgress = lessonProgress.getAllLessonStates().isNotEmpty()
            val nextAllowed = if (hasAnyProgress) lessonProgress.getNextAvailableLesson() else getFirstLessonOnMap()
            val canStart = tried || lessonNumber == nextAllowed
            if (!canStart) {
                return
            }
        }
        // Decide whether to show topic intro (reading lesson) or jump straight to questions
        val plan = lessonProgress.getGlobalLessonPlan()
        val entry = plan.find { it.lessonNumber == lessonNumber }
        val categoryCode = getCategoryForLesson(lessonNumber)

        // Determine grouping key: prefer subcategory code when present, otherwise category display name
        val groupKey = if (entry != null && entry.subcategory.trim().isNotEmpty()) {
            entry.subcategory.trim().lowercase()
        } else {
            (entry?.category?.trim()?.lowercase() ?: (categoryCode ?: "")).ifEmpty { "general" }
        }

        // Find first lesson number within this topic/subtopic
        val sameGroup = if (entry != null) {
            if (entry.subcategory.trim().isNotEmpty()) {
                plan.filter { it.subcategory.trim().equals(entry.subcategory.trim(), ignoreCase = true) }
            } else {
                plan.filter { it.category.trim().equals(entry.category.trim(), ignoreCase = true) }
            }
        } else emptyList()
        val firstLessonInGroup = sameGroup.minByOrNull { it.lessonNumber }?.lessonNumber ?: lessonNumber

        // Show intro only if this is the first lesson of the group and not shown before
        val prefs = getSharedPreferences("topic_intros", MODE_PRIVATE)
        val hasShownKey = "intro_shown_" + groupKey
        val shouldShowIntro = (lessonNumber == firstLessonInGroup) && !prefs.getBoolean(hasShownKey, false)

        if (shouldShowIntro && categoryCode != null) {
            // Mark as shown immediately on first open
            prefs.edit().putBoolean(hasShownKey, true).apply()
            val intent = Intent(this, ReadingLessonActivity::class.java).apply {
                putExtra(ReadingLessonActivity.EXTRA_CATEGORY, categoryCode)
                putExtra(ReadingLessonActivity.EXTRA_LESSON_NUMBER, lessonNumber)
                putExtra(ReadingLessonActivity.EXTRA_IS_REVIEW, isReview)
                // Also pass display lesson number so MainActivity shows correct numbering after intro
                displayNumberByLesson[lessonNumber]?.let { putExtra(MainActivity.EXTRA_DISPLAY_LESSON_NUMBER, it) }
            }
            startActivity(intent)
            return
        }

        // Otherwise go straight to questions
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra(MainActivity.EXTRA_LESSON_NUMBER, lessonNumber)
            putExtra(MainActivity.EXTRA_IS_REVIEW, isReview)
            // Do NOT pass category for normal lessons; this would incorrectly trigger practice mode
            putExtra(MainActivity.EXTRA_CATEGORY, "")
            displayNumberByLesson[lessonNumber]?.let { putExtra(MainActivity.EXTRA_DISPLAY_LESSON_NUMBER, it) }
        }
        startActivity(intent)
    }

    private fun getCategoryForLesson(lessonNumber: Int): String? {
        val plan = lessonProgress.getGlobalLessonPlan()
        val entry = plan.find { it.lessonNumber == lessonNumber } ?: return null
        val categoryName = entry.category
        val sub = entry.subcategory
        // Prefer subcategory code mapping if it's already one of the known codes
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

    private fun showReviewDialog(lessonNumber: Int) {
        AlertDialog.Builder(this)
            .setTitle("Opakování")
            .setMessage("Chcete opakovat otázky, které jste zodpověděli špatně?")
            .setPositiveButton("Ano") { _, _ ->
                startQuiz(lessonNumber, true)
            }
            .setNegativeButton("Ne", null)
            .show()
    }

    // Removed curvy path drawing; using simple vertical list for lessons
} 