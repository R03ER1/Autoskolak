package cz.autokolk

import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject
import kotlin.concurrent.thread

class AlexActivity : AppCompatActivity() {
    private lateinit var lessonProgress: LessonProgress
    private lateinit var streakButton: MaterialButton
    private lateinit var xpButton: MaterialButton
    private lateinit var heartsButton: MaterialButton
    // Hunger overlay views for food screen
    private var hungerOverlayView: View? = null
    private var hungerOverlayProgress: android.widget.ProgressBar? = null
    private var hungerOverlayLabel: TextView? = null
    
    // Simple overlay system variables
    private lateinit var page1Container: FrameLayout
    private lateinit var page2Overlay: FrameLayout
    private lateinit var page2ContentContainer: FrameLayout
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alex)

        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        lessonProgress = LessonProgress(this)
        streakButton = findViewById(R.id.streakButton)
        xpButton = findViewById(R.id.xpButton)
        heartsButton = findViewById(R.id.heartsButton)
        
        // Initialize overlay system
        page1Container = findViewById(R.id.page1Container)
        page2Overlay = findViewById(R.id.page2Overlay)
        page2ContentContainer = findViewById(R.id.page2ContentContainer)
        
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

        bottomNav.selectedItemId = R.id.nav_lion
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
                R.id.nav_lion -> true
                R.id.nav_exercise -> {
                    val intent = android.content.Intent(this, PracticeActivity::class.java)
                        .addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP or android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_test -> {
                    val intent = android.content.Intent(this, TestAttemptActivity::class.java)
                        .addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP or android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
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
        
        // Check if lion is dead before loading content
        val manager = HungerManager(this)
        val prefs = getSharedPreferences("hunger_prefs", MODE_PRIVATE)
        val reviveTimestamp = prefs.getLong("revive_timestamp", 0L)
        val now = System.currentTimeMillis()
        val timeSinceRevive = now - reviveTimestamp
        
        // If we just revived (within grace period), skip death screen check
        val justRevived = reviveTimestamp > 0 && timeSinceRevive < 2000L
        
        if (!justRevived) {
            val hunger = manager.getCurrentHunger()
            if (hunger <= 0) {
                val intent = android.content.Intent(this, AlexDeathActivity::class.java)
                startActivity(intent)
                // Don't load content if dead
                return
            }
        }
        
        // Load page 1 content
        loadPage1Content()
        
        // Page 2 overlays (food/shop) are opened on demand via buttons
        
    }

    // Backward-compatible wrapper used by callers expecting a single food overlay loader
    private fun loadPage2Content() {
        loadFoodOverlay()
    }

    override fun onResume() {
        super.onResume()
        lessonProgress.normalizeStreakForToday()
        updateStreakHeader()
        updatePointsHeader()
        updateHeartsHeader()
        // Refresh the lion name in case it was changed in settings
        val lionName = getLionName()
        val manager = HungerManager(this)
        val prefs = getSharedPreferences("hunger_prefs", MODE_PRIVATE)
        val reviveTimestamp = prefs.getLong("revive_timestamp", 0L)
        val now = System.currentTimeMillis()
        val timeSinceRevive = now - reviveTimestamp
        
        // If we just revived (within grace period), skip death screen check
        val justRevived = reviveTimestamp > 0 && timeSinceRevive < 2000L
        
        if (!justRevived) {
            val hunger = manager.getCurrentHunger()
            // Check if lion is dead and show death screen
            if (hunger <= 0) {
                val intent = android.content.Intent(this, AlexDeathActivity::class.java)
                startActivity(intent)
                return
            }
        }
        
        val hunger = manager.getCurrentHunger()
        
        // Update hunger message as title
        page1Container.findViewById<TextView>(R.id.pageTitle)?.text = getHungerMessage(hunger, lionName)
        page2ContentContainer.findViewById<TextView>(R.id.foodTitle)?.text = "Jídlo pro $lionName"

        // Tutorial overlay for Alex page
        TutorialManager.showIfNeeded(
            this,
            key = "tutorial_alex",
            message = "Tady jsem já! Odpovídáním na otázky budeš dostávat [mince], za které mi můžeš kupovat jídlo nebo módní doplňky. Prosím, udrž mě naživu!"
        )
    }

    override fun onBackPressed() {
        if (page2Overlay.isVisible) {
            hidePage2()
            return
        }
        super.onBackPressed()
    }

    private fun updateStreakHeader() {
        val streak = lessonProgress.getCurrentStreak()
        streakButton.text = streak.toString()
    }

    private fun updatePointsHeader() {
        val points = lessonProgress.getTotalPoints()
        xpButton.text = points.toString()
    }

    fun refreshPointsHeader() {
        updatePointsHeader()
    }

    private fun updateHeartsHeader() {
        val hearts = lessonProgress.getCurrentHearts()
        heartsButton.text = hearts.toString()
    }

    private fun getLionName(): String {
        val prefs = getSharedPreferences("lesson_progress", MODE_PRIVATE)
        return prefs.getString("lion_name", "Alex") ?: "Alex"
    }

    private fun setLionName(newName: String) {
        val clean = newName.trim().ifEmpty { "Alex" }
        val prefs = getSharedPreferences("lesson_progress", MODE_PRIVATE)
        prefs.edit().putString("lion_name", clean).apply()
    }

    private fun getHungerMessage(hunger: Int, lionName: String): String {
        val hungerLevel = (hunger / 10) * 10 // Round down to nearest 10
        return when (hungerLevel) {
            0 -> "$lionName je úplně vyhladovělý! 😰"
            10 -> "$lionName má obrovský hlad! 😢"
            20 -> "$lionName je velmi hladový! 😔"
            30 -> "$lionName má velký hlad! 😕"
            40 -> "$lionName má hlad! 😐"
            50 -> "$lionName je trochu hladový! 🤔"
            60 -> "$lionName je v pořádku! 😊"
            70 -> "$lionName se cítí dobře! 😄"
            80 -> "$lionName je spokojený! 😁"
            90 -> "$lionName je velmi spokojený! 😍"
            100 -> "$lionName se velmi dobře napapal! 🤤"
            else -> "$lionName je v pořádku! 😊"
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
        // Hide + button for streak sheet
        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.bottomSheetPlus)?.visibility = View.GONE
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
        // Hide + button for points sheet
        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.bottomSheetPlus)?.visibility = View.GONE
        dialog.show()
    }

    private fun showHeartsBottomSheet() {
        val dialog = BottomSheetDialog(this)
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
        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.bottomSheetOk).setOnClickListener {
            dialog.dismiss()
        }
        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.bottomSheetPlus)?.setOnClickListener {
            val intent = android.content.Intent(this, MainActivity::class.java)
            intent.putExtra(MainActivity.EXTRA_RANDOM_COUNT, 10)
            startActivity(intent)
            dialog.dismiss()
        }
        dialog.show()
    }

    
    private fun loadPage1Content() {
        val page1View = LayoutInflater.from(this).inflate(R.layout.fragment_alex_page_one, page1Container, false)
        page1Container.removeAllViews()
        page1Container.addView(page1View)
        
        // Initialize page 1 fragment logic
        val manager = HungerManager(this)
        val hunger = manager.getCurrentHunger()
        val lionName = getLionName()
        
        // Set hunger message as title
        page1View.findViewById<TextView>(R.id.pageTitle)?.text = getHungerMessage(hunger, lionName)
        
        page1View.findViewById<android.widget.ProgressBar>(R.id.healthProgress)?.apply {
            max = HungerManager.MAX_HUNGER
            progress = hunger
            // Animate a subtle leftward sweep using secondaryProgress, unless frozen (kamení active)
            val isFrozen = HungerManager(this@AlexActivity).isFrozenNow()
            if (!isFrozen) {
                try {
                    val sweepAnimator = ValueAnimator.ofFloat(0f, 1f)
                    sweepAnimator.duration = 1500L
                    sweepAnimator.repeatMode = ValueAnimator.RESTART
                    sweepAnimator.repeatCount = ValueAnimator.INFINITE
                    sweepAnimator.addUpdateListener {
                        val frac = it.animatedValue as Float
                        val p = progress
                        // Sweep only within the filled part, moving right->left
                        secondaryProgress = (p * (1f - frac)).toInt().coerceIn(0, p)
                    }
                    sweepAnimator.start()
                    // Store animator on view tag so GC keeps it and we can stop later if needed
                    setTag(R.id.healthProgress, sweepAnimator)
                } catch (_: Throwable) { }
            } else {
                // Ensure no sweep overlay when frozen
                try { (getTag(R.id.healthProgress) as? ValueAnimator)?.cancel() } catch (_: Throwable) { }
                secondaryProgress = progress
            }
        }
        page1View.findViewById<TextView>(R.id.healthProgressLabel)?.apply {
            text = "$hunger%"
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_food, 0, 0, 0)
            compoundDrawableTintList = android.content.res.ColorStateList.valueOf(ContextCompat.getColor(context, android.R.color.white))
        }
        page1View.findViewById<TextView>(R.id.hungerFreezeLabel)?.apply {
            val manager = HungerManager(this@AlexActivity)
            if (manager.isFrozenNow()) {
                val now = System.currentTimeMillis()
                val until = manager.getFreezeUntilEpochMillis()
                val remaining = (until - now).coerceAtLeast(0L)
                val hours = (remaining / 3_600_000L).toInt()
                val minutes = ((remaining % 3_600_000L) / 60_000L).toInt()
                text = "Hladovění začne za ${hours} hod a ${minutes} min"
                visibility = View.VISIBLE
            } else {
                text = ""
                visibility = View.GONE
            }
        }
        
        // Load Alex image based on hunger level
        updateAlexImage(page1View, hunger)
        
        // Two buttons: food and shop
        page1View.findViewById<com.google.android.material.button.MaterialButton>(R.id.alexFoodButton)?.setOnClickListener {
            loadFoodOverlay()
        }
        page1View.findViewById<com.google.android.material.button.MaterialButton>(R.id.alexShopButton)?.setOnClickListener {
            loadShopOverlay()
        }
    }

    private fun showRenameDialog() {
        val context = this
        val textPrimary = ContextCompat.getColor(context, R.color.text_primary)
        val cardDark = ContextCompat.getColor(context, R.color.card_dark)
        val hintColor = android.graphics.Color.argb(178, android.graphics.Color.red(textPrimary), android.graphics.Color.green(textPrimary), android.graphics.Color.blue(textPrimary))

        val input = android.widget.EditText(context).apply {
            hint = "Jméno lva"
            setText(getLionName())
            setSingleLine(true)
            setTextColor(textPrimary)
            setHintTextColor(hintColor)
            background = null // remove default light background
            backgroundTintList = android.content.res.ColorStateList.valueOf(textPrimary)
        }
        val container = android.widget.LinearLayout(context).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            val padding = (16 * resources.displayMetrics.density).toInt()
            setPadding(padding, padding, padding, 0)
            setBackgroundColor(cardDark)
            addView(input)
        }
        val titleSpannable = android.text.SpannableString("Přejmenovat lva").apply {
            setSpan(android.text.style.ForegroundColorSpan(textPrimary), 0, length, 0)
        }
        val dialog = com.google.android.material.dialog.MaterialAlertDialogBuilder(context)
            .setTitle(titleSpannable)
            .setView(container)
            .setPositiveButton("Uložit") { d, _ ->
                val name = input.text?.toString() ?: ""
                setLionName(name)
                // Update page 1 title immediately
                val hunger = HungerManager(this).getCurrentHunger()
                page1Container.findViewById<TextView>(R.id.pageTitle)?.text = getHungerMessage(hunger, getLionName())
                d.dismiss()
            }
            .setNegativeButton("Zrušit") { d, _ -> d.dismiss() }
            .create()
        dialog.show()
        // Darken full dialog background (title area and buttons area)
        dialog.window?.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(cardDark))
        // Color dialog buttons
        val pos = dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)
        val neg = dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE)
        pos?.setTextColor(textPrimary)
        neg?.setTextColor(textPrimary)
    }

    private fun addCloseButton() {
        val size = (40 * resources.displayMetrics.density).toInt()
        val margin = (24 * resources.displayMetrics.density).toInt()
        val padding = (8 * resources.displayMetrics.density).toInt()
        val closeButton = com.google.android.material.button.MaterialButton(this).apply {
            text = "✕"
            setAllCaps(false)
            setTextColor(android.graphics.Color.WHITE)
            cornerRadius = 0
            background = androidx.core.content.ContextCompat.getDrawable(this@AlexActivity, R.drawable.button_gradient_green_blue)
            backgroundTintList = null
            setPadding(padding, padding, padding, padding)
            setOnClickListener { hidePage2() }
            layoutParams = android.widget.FrameLayout.LayoutParams(
                size,
                size
            ).apply {
                gravity = android.view.Gravity.TOP or android.view.Gravity.START
                setMargins(margin, margin, 0, 0)
            }
        }
        page2ContentContainer.addView(closeButton)
    }

    private fun loadFoodOverlay() {
        val page2View = LayoutInflater.from(this).inflate(R.layout.fragment_alex_page_two, page2ContentContainer, false)
        page2ContentContainer.removeAllViews()
        page2ContentContainer.addView(page2View)
        // Ensure popup container exists and is hidden initially
        findViewById<View>(R.id.foodPopupContainer)?.visibility = View.GONE
        val lionName = getLionName()
        page2View.findViewById<TextView>(R.id.foodTitle)?.text = "Jídlo pro $lionName"
        setFoodImages(page2View)
        // Show floating hunger bar overlay for food screen
        showHungerOverlay()
        addCloseButton()
        setupPage2ClickHandlers(page2View)
        showPage2()
    }

    private fun loadShopOverlay() {
        // Remove hunger overlay if present (only shown for food)
        removeHungerOverlay()
        val page2View = LayoutInflater.from(this).inflate(R.layout.fragment_alex_shop_sunglasses, page2ContentContainer, false)
        page2ContentContainer.removeAllViews()
        page2ContentContainer.addView(page2View)
        // Set images for item icons
        setFoodImages(page2View)
        // Set the lion name dynamically in the shop title
        val lionName = getLionName()
        page2View.findViewById<TextView>(R.id.shopTitle)?.text = "Doplňky pro $lionName"
        // Wire sunglasses purchase button and toggle
        val buyButton = page2View.findViewById<com.google.android.material.button.MaterialButton>(R.id .buy_sunglasses_button)
        val toggle = page2View.findViewById<com.google.android.material.switchmaterial.SwitchMaterial>(R.id.toggle_sunglasses)
        val progress = LessonProgress(this)
        fun updateSunglassesControls() {
            val owned = progress.hasSunglasses()
            if (owned) {
                buyButton.visibility = View.GONE
                toggle.visibility = View.VISIBLE
                toggle.isChecked = progress.isSunglassesEnabled()
                toggle.text = if (toggle.isChecked) "Zapnuto" else "Vypnuto"
            } else {
                buyButton.visibility = View.VISIBLE
                toggle.visibility = View.GONE
                buyButton.isEnabled = true
                buyButton.text = "Koupit"
            }
        }
        updateSunglassesControls()
        buyButton.setOnClickListener {
            val ok = progress.buySunglassesIfAffordable(1000)
            if (!ok) {
                // Show in-app popup on insufficient points
                // Reuse food popup container
                val popup = findViewById<View>(R.id.foodPopupContainer)
                if (popup != null) {
                    val icon = popup.findViewById<android.widget.ImageView>(R.id.coinPopupIcon)
                    val label = popup.findViewById<TextView>(R.id.coinPopupText)
                    icon?.setImageResource(R.drawable.ic_coin)
                    icon?.imageTintList = null // keep coin yellow
                    label?.text = "Nedostatek bodů"
                    popup.visibility = View.VISIBLE
                    popup.alpha = 1.0f
                    popup.scaleX = 0.5f
                    popup.scaleY = 0.5f
                    try { popup.animate().cancel() } catch (_: Throwable) { }
                    popup.animate().scaleX(1f).scaleY(1f).setDuration(200).withEndAction {
                        android.os.Handler(mainLooper).postDelayed({
                            popup.animate().alpha(0f).scaleX(0.8f).scaleY(0.8f).setDuration(1000).withEndAction { popup.visibility = View.GONE }
                        }, 1000)
                    }
                }
                return@setOnClickListener
            }
            // Success popup
            val popup = findViewById<View>(R.id.foodPopupContainer)
            if (popup != null) {
                val icon = popup.findViewById<android.widget.ImageView>(R.id.coinPopupIcon)
                val label = popup.findViewById<TextView>(R.id.coinPopupText)
                icon?.setImageResource(R.drawable.ic_shop)
                icon?.imageTintList = android.content.res.ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.white))
                label?.text = "Zakoupeno"
                popup.visibility = View.VISIBLE
                popup.alpha = 1.0f
                popup.scaleX = 0.5f
                popup.scaleY = 0.5f
                try { popup.animate().cancel() } catch (_: Throwable) { }
                popup.animate().scaleX(1f).scaleY(1f).setDuration(200).withEndAction {
                    android.os.Handler(mainLooper).postDelayed({
                        popup.animate().alpha(0f).scaleX(0.8f).scaleY(0.8f).setDuration(1000).withEndAction { popup.visibility = View.GONE }
                    }, 1000)
                }
            }
            refreshPointsHeader()
            updateSunglassesControls()
            val hunger = HungerManager(this).getCurrentHunger()
            updateAlexImage(page1Container, hunger)
        }
        toggle.setOnCheckedChangeListener { _, isChecked ->
            progress.setSunglassesEnabled(isChecked)
            toggle.text = if (isChecked) "Zapnuto" else "Vypnuto"
            val hunger = HungerManager(this).getCurrentHunger()
            updateAlexImage(page1Container, hunger)
        }
        // Rename unlock UI
        val renameButton = page2View.findViewById<com.google.android.material.button.MaterialButton>(R.id.rename_feature_button)
        fun updateRenameButton() {
            val unlocked = progress.hasRenameUnlocked()
            renameButton.text = if (unlocked) "Přejmenovat" else "Koupit"
        }
        updateRenameButton()
        renameButton.setOnClickListener {
            val unlocked = progress.hasRenameUnlocked()
            if (!unlocked) {
                val okBuy = progress.buyRenameIfAffordable(2000)
                if (!okBuy) {
                    val popup = findViewById<View>(R.id.foodPopupContainer)
                    if (popup != null) {
                        val icon = popup.findViewById<android.widget.ImageView>(R.id.coinPopupIcon)
                        val label = popup.findViewById<TextView>(R.id.coinPopupText)
                        icon?.setImageResource(R.drawable.ic_coin)
                        icon?.imageTintList = null
                        label?.text = "Nedostatek bodů"
                        popup.visibility = View.VISIBLE
                        popup.alpha = 1.0f
                        popup.scaleX = 0.5f
                        popup.scaleY = 0.5f
                        try { popup.animate().cancel() } catch (_: Throwable) { }
                        popup.animate().scaleX(1f).scaleY(1f).setDuration(200).withEndAction {
                            android.os.Handler(mainLooper).postDelayed({
                                popup.animate().alpha(0f).scaleX(0.8f).scaleY(0.8f).setDuration(1000).withEndAction { popup.visibility = View.GONE }
                            }, 1000)
                        }
                    }
                    return@setOnClickListener
                }
                val popup = findViewById<View>(R.id.foodPopupContainer)
                if (popup != null) {
                    val icon = popup.findViewById<android.widget.ImageView>(R.id.coinPopupIcon)
                    val label = popup.findViewById<TextView>(R.id.coinPopupText)
                    icon?.setImageResource(R.drawable.ic_shop)
                    icon?.imageTintList = android.content.res.ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.white))
                    label?.text = "Odemknuto"
                    popup.visibility = View.VISIBLE
                    popup.alpha = 1.0f
                    popup.scaleX = 0.5f
                    popup.scaleY = 0.5f
                    try { popup.animate().cancel() } catch (_: Throwable) { }
                    popup.animate().scaleX(1f).scaleY(1f).setDuration(200).withEndAction {
                        android.os.Handler(mainLooper).postDelayed({
                            popup.animate().alpha(0f).scaleX(0.8f).scaleY(0.8f).setDuration(1000).withEndAction { popup.visibility = View.GONE }
                        }, 1000)
                    }
                }
                refreshPointsHeader()
                updateRenameButton()
            } else {
                // Show rename dialog
                showRenameDialog()
            }
        }

        addCloseButton()
        showPage2()
    }
    
    private fun showHungerOverlay() {
        // If already shown, just update it
        if (hungerOverlayView != null) {
            updateHungerOverlay()
            return
        }
        val overlay = LayoutInflater.from(this).inflate(R.layout.view_hunger_overlay, page2Overlay, false)
        val lp = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = android.view.Gravity.TOP or android.view.Gravity.CENTER_HORIZONTAL
        }
        overlay.layoutParams = lp
        hungerOverlayView = overlay
        hungerOverlayProgress = overlay.findViewById(R.id.hungerProgress)
        hungerOverlayLabel = overlay.findViewById(R.id.hungerProgressLabel)
        page2Overlay.addView(overlay)
        updateHungerOverlay()
    }

    private fun updateHungerOverlay() {
        val progressBar = hungerOverlayProgress ?: return
        val label = hungerOverlayLabel ?: return
        val hunger = HungerManager(this).getCurrentHunger()
        progressBar.max = HungerManager.MAX_HUNGER
        progressBar.progress = hunger
        label.text = "$hunger%"
    }

    private fun removeHungerOverlay() {
        val view = hungerOverlayView ?: return
        try { page2Overlay.removeView(view) } catch (_: Throwable) { }
        hungerOverlayView = null
        hungerOverlayProgress = null
        hungerOverlayLabel = null
    }

    private fun setFoodImages(container: View) {
        fun loadImage(imageView: android.widget.ImageView?, baseName: String, assetName: String) {
            if (imageView == null) return
            // Try drawable first (expects files like res/drawable/<baseName>.png)
            val resId = resources.getIdentifier(baseName, "drawable", packageName)
            if (resId != 0) {
                imageView.setImageResource(resId)
                return
            }
            // Fallback to assets
            try {
                assets.open("images/$assetName").use { input ->
                    val bmp = android.graphics.BitmapFactory.decodeStream(input)
                    imageView.setImageBitmap(bmp)
                }
            } catch (_: Throwable) { }
        }
        loadImage(container.findViewById(R.id.image_klobaska), "sausage", "sausage.png")
        loadImage(container.findViewById(R.id.image_kure), "chicken", "chicken.png")
        loadImage(container.findViewById(R.id.image_zmrzlina), "icecream", "IceCream.png")
        loadImage(container.findViewById(R.id.image_mrkev), "carrot", "carrot.png")
        loadImage(container.findViewById(R.id.image_pivo), "beer", "beer.png")
        loadImage(container.findViewById(R.id.image_kameni), "stone", "stone.png")
        // Accessories icons removed from shop layout; no image views to populate here anymore
    }
    
    private fun setupPage2ClickHandlers(view: View) {
        fun showFoodPopup(text: String) {
            val popup = findViewById<View>(R.id.foodPopupContainer) ?: return
            val icon = popup.findViewById<android.widget.ImageView>(R.id.coinPopupIcon)
            val label = popup.findViewById<TextView>(R.id.coinPopupText)
            icon?.setImageResource(R.drawable.ic_food)
            // Ensure the food icon appears white in the popup
            icon?.imageTintList = android.content.res.ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.white))
            label?.text = text
            // Cancel any ongoing animations and pending fade-outs before restarting
            try { popup.animate().cancel() } catch (_: Throwable) { }
            try { popup.clearAnimation() } catch (_: Throwable) { }

            popup.visibility = View.VISIBLE
            popup.alpha = 1.0f
            popup.scaleX = 0.5f
            popup.scaleY = 0.5f
            popup.animate()
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setDuration(200)
                .withEndAction {
                    // Schedule fade-out with a Handler we store on the view, so it can be cancelled by the next popup
                    val handler = android.os.Handler(mainLooper)
                    val fadeOut = Runnable {
                        try { popup.animate().cancel() } catch (_: Throwable) { }
                        popup.animate()
                            .alpha(0.0f)
                            .scaleX(0.8f)
                            .scaleY(0.8f)
                            .setDuration(1000)
                            .withEndAction { popup.visibility = View.GONE }
                    }
                    // Cancel any previous stored callback
                    (popup.getTag(R.id.coinPopup) as? android.os.Handler)?.removeCallbacksAndMessages(null)
                    // Store and post new callback
                    popup.setTag(R.id.coinPopup, handler)
                    popup.setTag(R.id.coinPopupContainer, fadeOut)
                    handler.postDelayed(fadeOut, 1000)
                }
        }

        fun handlePurchase(hungerDelta: Int, pointCost: Int, successMsg: String, foodKey: String) {
            val progress = LessonProgress(this@AlexActivity)
            val hunger = HungerManager(this@AlexActivity)
            val currentHunger = hunger.getCurrentHunger()
            if (currentHunger + hungerDelta > HungerManager.MAX_HUNGER) {
                showFoodPopup("Nelze přes 100")
                return
            }
            val ok = progress.spendPoints(pointCost)
            if (!ok) {
                showFoodPopup("Nedostatek bodů")
                return
            }
            val newValue = (currentHunger + hungerDelta).coerceAtMost(HungerManager.MAX_HUNGER)
            hunger.setCurrentHunger(newValue)
            showFoodPopup(successMsg.replace("🍖", ""))
            refreshPointsHeader()
            // Update page 1 health display
            updatePage1Health()
            // Update hunger overlay on food screen
            updateHungerOverlay()
            try { AchievementsManager(this@AlexActivity).onFed(foodKey) } catch (_: Throwable) { }
        }

        view.findViewById<LinearLayout>(R.id.food_klobaska)?.setOnClickListener {
            handlePurchase(hungerDelta = 1, pointCost = 4, successMsg = "+ 1 🍖", foodKey = "klobaska")
        }
        view.findViewById<LinearLayout>(R.id.food_kure)?.setOnClickListener {
            handlePurchase(hungerDelta = 10, pointCost = 30, successMsg = "+ 10 🍖", foodKey = "kure")
        }
        view.findViewById<LinearLayout>(R.id.food_zmrzlina)?.setOnClickListener {
            handlePurchase(hungerDelta = 3, pointCost = 10, successMsg = "+ 3 🍖", foodKey = "zmrzlina")
        }
        view.findViewById<LinearLayout>(R.id.food_mrkev)?.setOnClickListener {
            handlePurchase(hungerDelta = 5, pointCost = 16, successMsg = "+ 5 🍖", foodKey = "mrkev")
        }
        view.findViewById<LinearLayout>(R.id.food_pivo)?.setOnClickListener {
            val progress = LessonProgress(this@AlexActivity)
            val hunger = HungerManager(this@AlexActivity)
            val currentHunger = hunger.getCurrentHunger()
            if (currentHunger >= HungerManager.MAX_HUNGER) {
                showFoodPopup("Už na maximu")
                return@setOnClickListener
            }
            val ok = progress.spendPoints(150)
            if (!ok) {
                showFoodPopup("Nedostatek bodů")
                return@setOnClickListener
            }
            hunger.setCurrentHunger(HungerManager.MAX_HUNGER)
            showFoodPopup("+ MAX")
            refreshPointsHeader()
            updatePage1Health()
            updateHungerOverlay()
            try { AchievementsManager(this@AlexActivity).onFed("pivo") } catch (_: Throwable) { }
        }

        // Kamení: costs 80 points and freezes hunger decay for 48 hours
        view.findViewById<LinearLayout>(R.id.food_kameni)?.setOnClickListener {
            val progress = LessonProgress(this@AlexActivity)
            val ok = progress.spendPoints(80)
            if (!ok) {
                showFoodPopup("Nedostatek bodů")
                return@setOnClickListener
            }
            val hunger = HungerManager(this@AlexActivity)
            hunger.freezeDecayForHours(48)
            showFoodPopup("+ 48h bez hladu")
            refreshPointsHeader()
            // Reflect kamení state immediately on the main page
            updatePage1Health()
            try { AchievementsManager(this@AlexActivity).onFed("kameni") } catch (_: Throwable) { }
        }

    }
    
    private fun updatePage1Health() {
        val manager = HungerManager(this)
        val hunger = manager.getCurrentHunger()
        val lionName = getLionName()
        page1Container.findViewById<android.widget.ProgressBar>(R.id.healthProgress)?.apply {
            max = HungerManager.MAX_HUNGER
            progress = hunger
            val isFrozen = manager.isFrozenNow()
            if (isFrozen) {
                // Stop sweep animation when frozen
                try { (getTag(R.id.healthProgress) as? ValueAnimator)?.cancel() } catch (_: Throwable) { }
                secondaryProgress = progress
            } else {
                // Ensure sweep animation is present
                val existing = (getTag(R.id.healthProgress) as? ValueAnimator)
                if (existing == null) {
                    try {
                        val sweepAnimator = ValueAnimator.ofFloat(0f, 1f)
                        sweepAnimator.duration = 1500L
                        sweepAnimator.repeatMode = ValueAnimator.RESTART
                        sweepAnimator.repeatCount = ValueAnimator.INFINITE
                        sweepAnimator.addUpdateListener {
                            val frac = it.animatedValue as Float
                            val p = progress
                            secondaryProgress = (p * (1f - frac)).toInt().coerceIn(0, p)
                        }
                        sweepAnimator.start()
                        setTag(R.id.healthProgress, sweepAnimator)
                    } catch (_: Throwable) { }
                }
                if (secondaryProgress <= 0) secondaryProgress = (max * 0.2f).toInt()
            }
        }
        page1Container.findViewById<TextView>(R.id.healthProgressLabel)?.apply {
            text = "$hunger%"
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_food, 0, 0, 0)
            compoundDrawableTintList = android.content.res.ColorStateList.valueOf(ContextCompat.getColor(context, android.R.color.white))
        }
        page1Container.findViewById<TextView>(R.id.hungerFreezeLabel)?.apply {
            if (manager.isFrozenNow()) {
                val now = System.currentTimeMillis()
                val until = manager.getFreezeUntilEpochMillis()
                val remaining = (until - now).coerceAtLeast(0L)
                val hours = (remaining / 3_600_000L).toInt()
                val minutes = ((remaining % 3_600_000L) / 60_000L).toInt()
                text = "Hladovění začne za ${hours} hod a ${minutes} min"
                visibility = View.VISIBLE
            } else {
                text = ""
                visibility = View.GONE
            }
        }
        
        // Update hunger message as title
        page1Container.findViewById<TextView>(R.id.pageTitle)?.text = getHungerMessage(hunger, lionName)
        
        // Update Alex image based on hunger level
        updateAlexImage(page1Container, hunger)
    }
    
    private fun getAlexImageName(hunger: Int): String {
        val base = when {
            hunger <= 20 -> "AlexHungry.png"
            hunger <= 40 -> "AlexSad.png"
            hunger <= 60 -> "Alex.png"
            hunger <= 80 -> "AlexHappy.png"
            else -> "AlexCool.png"
        }
        val progress = LessonProgress(this)
        if (progress.isSunglassesEnabled()) {
            // Prefix with C variant when sunglasses are enabled
            return "C$base"
        }
        return base
    }
    
    private fun updateAlexImage(container: View, hunger: Int) {
        try {
            val imageView = container.findViewById<android.widget.ImageView>(R.id.alexImage)
            if (imageView == null) return

            val moduleInstalled = try {
                SplitInstallManagerFactory.create(this).installedModules.contains("imageassets")
            } catch (_: Throwable) { false }
            val imageName = if (hunger <= 0) "AlexDead.png" else getAlexImageName(hunger)
            // #region agent log
            debugLog(
                hypothesisId = "H1",
                location = "AlexActivity.kt:updateAlexImage:pre",
                message = "before_load",
                data = mapOf(
                    "hunger" to hunger,
                    "imageName" to imageName,
                    "moduleInstalled" to moduleInstalled,
                    "sunglassesEnabled" to LessonProgress(this).isSunglassesEnabled()
                )
            )
            // #endregion

            if (hunger <= 0) {
                // Show dead Alex rotated 90 degrees to the right
                assets.open("alex/$imageName").use { input ->
                    val bmp = android.graphics.BitmapFactory.decodeStream(input)
                    imageView.setImageBitmap(bmp)
                    // #region agent log
                    debugLog(
                        hypothesisId = "H2",
                        location = "AlexActivity.kt:updateAlexImage:dead",
                        message = "load_success",
                        data = mapOf(
                            "imageName" to imageName,
                            "width" to bmp?.width,
                            "height" to bmp?.height,
                            "moduleInstalled" to moduleInstalled
                        )
                    )
                    // #endregion
                }
                imageView.rotation = 90f
            } else {
                assets.open("images/alex/$imageName").use { input ->
                    val bmp = android.graphics.BitmapFactory.decodeStream(input)
                    imageView.setImageBitmap(bmp)
                    // #region agent log
                    debugLog(
                        hypothesisId = "H2",
                        location = "AlexActivity.kt:updateAlexImage:alive",
                        message = "load_success",
                        data = mapOf(
                            "imageName" to imageName,
                            "width" to bmp?.width,
                            "height" to bmp?.height,
                            "moduleInstalled" to moduleInstalled
                        )
                    )
                    // #endregion
                }
                // Ensure normal orientation for non-dead states
                imageView.rotation = 0f
            }
        } catch (e: Throwable) {
            // #region agent log
            debugLog(
                hypothesisId = "H3",
                location = "AlexActivity.kt:updateAlexImage:catch",
                message = "load_failed",
                data = mapOf(
                    "message" to (e.message ?: "unknown"),
                    "cause" to (e.cause?.message ?: "none")
                )
            )
            // #endregion
        }
    }
    
    private fun showPage2() {
        page2Overlay.visibility = View.VISIBLE
    }
    
    private fun hidePage2() {
        // Clean any floating overlays when closing
        removeHungerOverlay()
        page2Overlay.visibility = View.GONE
    }

    private fun debugLog(
        hypothesisId: String,
        location: String,
        message: String,
        data: Map<String, Any?>
    ) {
        try {
            // #region agent log
            val payload = JSONObject().apply {
                put("sessionId", "debug-session")
                put("runId", "pre-fix")
                put("hypothesisId", hypothesisId)
                put("location", location)
                put("message", message)
                put("data", JSONObject(data))
                put("timestamp", System.currentTimeMillis())
            }.toString()

            thread {
                val endpoints = listOf(
                    "http://127.0.0.1:7242/ingest/fb755fe0-ce46-4b6a-aa4a-ef69553db45f",
                    // Emulator-to-host alias fallback
                    "http://10.0.2.2:7242/ingest/fb755fe0-ce46-4b6a-aa4a-ef69553db45f",
                    // Localhost (some devices map this differently)
                    "http://localhost:7242/ingest/fb755fe0-ce46-4b6a-aa4a-ef69553db45f"
                )
                for (urlStr in endpoints) {
                    try {
                        val url = URL(urlStr)
                        val conn = (url.openConnection() as HttpURLConnection).apply {
                            requestMethod = "POST"
                            setRequestProperty("Content-Type", "application/json")
                            doOutput = true
                            connectTimeout = 4000
                            readTimeout = 4000
                        }
                        conn.outputStream.use { it.write(payload.toByteArray(Charsets.UTF_8)) }
                        conn.inputStream.use { /* drain */ }
                        conn.disconnect()
                        break
                    } catch (_: Throwable) {
                        // try next endpoint
                    }
                }
            }
            // #endregion
        } catch (_: Throwable) {
            // Swallow logging failures to avoid crashing
        }
    }
}