package cz.autokolk

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import android.widget.ImageView
import android.widget.LinearLayout
import com.google.android.material.button.MaterialButton
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import androidx.appcompat.app.AlertDialog
import com.google.android.material.switchmaterial.SwitchMaterial
import android.widget.EditText
import android.view.ViewGroup
import android.view.Gravity
import android.util.TypedValue
import android.view.View
import android.graphics.drawable.ColorDrawable

class SettingsActivity : AutokolkActivity() {

    companion object {
        /** Změň v repu podle potřeby; stejné heslo platí pro debug i release. */
        private const val DEVELOPER_OPTIONS_PASSWORD = "autokolk_dev"
    }

    private lateinit var lessonProgress: LessonProgress
    private lateinit var streakButton: MaterialButton
    private lateinit var xpButton: MaterialButton
    private lateinit var heartsButton: MaterialButton
    private lateinit var debuggingHeader: LinearLayout
    private lateinit var debuggingContent: LinearLayout
    private lateinit var debuggingArrow: ImageView
    private var isDebuggingExpanded = false
    private val handler = android.os.Handler()
    private val devPrefs by lazy { getSharedPreferences("developer_options", MODE_PRIVATE) }
    private val hungerDebugUpdater = object : Runnable {
        override fun run() {
            try {
                updateHungerDebugBox()
            } finally {
                handler.postDelayed(this, 60_000L)
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        lessonProgress = LessonProgress(this)
        streakButton = findViewById(R.id.streakButton)
        xpButton = findViewById(R.id.xpButton)
        heartsButton = findViewById(R.id.heartsButton)
        debuggingHeader = findViewById(R.id.debugging_header)
        debuggingContent = findViewById(R.id.debugging_content)
        debuggingArrow = findViewById(R.id.debugging_arrow)
        updateStreakHeader()
        updatePointsHeader()
        updateHeartsHeader()

        streakButton.setOnClickListener { showStreakBottomSheet() }
        xpButton.setOnClickListener { showPointsBottomSheet() }
        heartsButton.setOnClickListener { showHeartsBottomSheet() }

        // Check if developer options are unlocked and hide content if locked
        if (!devPrefs.getBoolean("unlocked", false)) {
            debuggingContent.visibility = LinearLayout.GONE
        }

        // Debugging dropdown toggle
        debuggingHeader.setOnClickListener {
            if (devPrefs.getBoolean("unlocked", false)) {
                toggleDebuggingDropdown()
            } else {
                showPasswordDialog { unlocked ->
                    if (unlocked) {
                        devPrefs.edit().putBoolean("unlocked", true).apply()
                        toggleDebuggingDropdown()
                    }
                }
            }
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.setBackgroundColor(Color.TRANSPARENT)
        bottomNav.background = null
        ViewCompat.setElevation(bottomNav, 0f)
        bottomNav.backgroundTintList = android.content.res.ColorStateList.valueOf(Color.TRANSPARENT)
        bottomNav.itemRippleColor = null
        try { bottomNav.setItemBackground(null) } catch (_: Throwable) { }
        try { bottomNav.isItemActiveIndicatorEnabled = false } catch (_: Throwable) { }

        bottomNav.selectedItemId = R.id.nav_settings
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
                R.id.nav_test -> {
                    val intent = android.content.Intent(this, TestAttemptActivity::class.java)
                        .addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP or android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_settings -> true
                else -> false
            }
        }
        findViewById<TextView>(R.id.pageTitle)?.text = "⚙️ Settings"

        // Footer version click -> open changelog
        findViewById<TextView>(R.id.settings_footer)?.setOnClickListener {
            val intent = android.content.Intent(this, ChangelogActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        // Infinite lives toggle
        val prefs = getSharedPreferences("lesson_progress", MODE_PRIVATE)
        val infiniteSwitch = findViewById<SwitchMaterial>(R.id.switch_test1)
        infiniteSwitch?.isChecked = prefs.getBoolean("infinite_lives", false)
        infiniteSwitch?.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("infinite_lives", isChecked).apply()
        }

        // Hunger test card
        findViewById<android.view.View>(R.id.card_hunger_test)?.setOnClickListener {
            showHungerTestDialog()
        }

        // Streak test card
        findViewById<android.view.View>(R.id.card_streak_test)?.setOnClickListener {
            showStreakTestDialog()
        }

        // Points test card
        findViewById<android.view.View>(R.id.card_points_test)?.setOnClickListener {
            showPointsTestDialog()
        }

        // Hearts (lives) test card
        findViewById<android.view.View>(R.id.card_hearts_test)?.setOnClickListener {
            showHeartsTestDialog()
        }

        // Trigger random event (debug)
        findViewById<android.view.View>(R.id.card_trigger_event)?.setOnClickListener {
            val isUnlocked = devPrefs.getBoolean("unlocked", false)
            if (isUnlocked) {
                val manager = RandomEventManager(this)
                manager.showRandomNow(this) {
                    updatePointsHeader()
                    updateHeartsHeader()
                }
            } else {
                showPasswordDialog { unlocked ->
                    if (unlocked) {
                        devPrefs.edit().putBoolean("unlocked", true).apply()
                        val manager = RandomEventManager(this)
                        manager.showRandomNow(this) {
                            updatePointsHeader()
                            updateHeartsHeader()
                        }
                    }
                }
            }
        }

        // Achievements card
        findViewById<android.view.View>(R.id.card_achievements)?.setOnClickListener {
            val intent = android.content.Intent(this, AchievementsActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        findViewById<android.view.View>(R.id.card_rename_lion)?.setOnClickListener {
            showRenameLionDialog()
        }

        // Clear-all card
        findViewById<android.view.View>(R.id.card_clear_all)?.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Vymazat vše")
                .setMessage("Opravdu chcete smazat veškerý postup? To nelze vrátit.")
                .setPositiveButton("Vymazat") { dialog, _ ->
                    dialog.dismiss()
                    lessonProgress.clearProgress()
                    // Also disable kamení (unfreeze hunger decay)
                    HungerManager(this).freezeDecayUntil(0L)
                    // Clear achievements
                    AchievementsManager(this).clearAll()
                    // Also clear topic intro flags so intros show again
                    getSharedPreferences("topic_intros", MODE_PRIVATE).edit().clear().apply()
                    // Clear tutorial flags so tutorials show again
                    getSharedPreferences("tutorial_overlays", MODE_PRIVATE).edit().clear().apply()
                    // Reset UI headers
                    updateStreakHeader()
                    updatePointsHeader()
                    updateHeartsHeader()
                    // Refresh hunger debug box to reflect kamení disabled
                    updateHungerDebugBox()
                    findViewById<SwitchMaterial>(R.id.switch_test1)?.isChecked = false
                    android.widget.Toast.makeText(this, "Vše vymazáno", android.widget.Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Zrušit") { dialog, _ -> dialog.dismiss() }
                .show()
        }
        updateHungerDebugBox()
        handler.postDelayed(hungerDebugUpdater, 60_000L)
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
        val plusButton = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.bottomSheetPlus)
        val okButton = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.bottomSheetOk)
        HeartsRewardAds.wireForHeartsSheet(this, view, lessonProgress, number, plusButton, okButton)
        dialog.show()
    }

    private fun showHungerTestDialog() {
        val hungerManager = HungerManager(this)
        val currentHunger = hungerManager.getCurrentHunger()
        
        val editText = createDarkDialogEditText().apply {
            setText(currentHunger.toString())
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }
        
        val hungerBuilder = AlertDialog.Builder(this)
            .setCustomTitle(createDarkDialogTitle("Nastavit hlad Alexe"))
            .setMessage("Aktuální hlad: $currentHunger/100\nZadejte novou hodnotu (0-100):")
            .setView(editText)
            .setPositiveButton("Nastavit") { dialog, _ ->
                try {
                    val newHunger = editText.text.toString().toInt().coerceIn(0, 100)
                    hungerManager.setCurrentHunger(newHunger)
                    android.widget.Toast.makeText(this, "Hlad nastaven na $newHunger", android.widget.Toast.LENGTH_SHORT).show()
                } catch (e: NumberFormatException) {
                    android.widget.Toast.makeText(this, "Neplatná hodnota", android.widget.Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Zrušit") { dialog, _ -> dialog.dismiss() }
        showStyledDialog(hungerBuilder)
    }

    private fun showStreakTestDialog() {
        val current = lessonProgress.getCurrentStreak()
        val editText = createDarkDialogEditText().apply {
            setText(current.toString())
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }
        val streakBuilder = AlertDialog.Builder(this)
            .setCustomTitle(createDarkDialogTitle("Nastavit streak"))
            .setMessage("Aktuální streak: $current\nZadejte novou hodnotu (>=0):")
            .setView(editText)
            .setPositiveButton("Nastavit") { dialog, _ ->
                try {
                    val newValue = editText.text.toString().toInt().coerceAtLeast(0)
                    lessonProgress.setStreakForToday(newValue)
                    updateStreakHeader()
                    android.widget.Toast.makeText(this, "Streak nastaven na $newValue", android.widget.Toast.LENGTH_SHORT).show()
                } catch (_: NumberFormatException) {
                    android.widget.Toast.makeText(this, "Neplatná hodnota", android.widget.Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Zrušit") { dialog, _ -> dialog.dismiss() }
        showStyledDialog(streakBuilder)
    }

    private fun showPointsTestDialog() {
        val current = lessonProgress.getTotalPoints()
        val editText = createDarkDialogEditText().apply {
            setText(current.toString())
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }
        val pointsBuilder = AlertDialog.Builder(this)
            .setCustomTitle(createDarkDialogTitle("Nastavit body"))
            .setMessage("Aktuální body: $current\nZadejte novou hodnotu (>=0):")
            .setView(editText)
            .setPositiveButton("Nastavit") { dialog, _ ->
                try {
                    val newValue = editText.text.toString().toInt().coerceAtLeast(0)
                    lessonProgress.setTotalPoints(newValue)
                    updatePointsHeader()
                    android.widget.Toast.makeText(this, "Body nastaveny na $newValue", android.widget.Toast.LENGTH_SHORT).show()
                } catch (_: NumberFormatException) {
                    android.widget.Toast.makeText(this, "Neplatná hodnota", android.widget.Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Zrušit") { dialog, _ -> dialog.dismiss() }
        showStyledDialog(pointsBuilder)
    }

    private fun showHeartsTestDialog() {
        val current = lessonProgress.getCurrentHearts()
        val editText = createDarkDialogEditText().apply {
            setText(current.toString())
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }
        val heartsBuilder = AlertDialog.Builder(this)
            .setCustomTitle(createDarkDialogTitle("Nastavit životy"))
            .setMessage("Aktuální životy: $current/15\nZadejte novou hodnotu (0–15):")
            .setView(editText)
            .setPositiveButton("Nastavit") { dialog, _ ->
                try {
                    val newValue = editText.text.toString().toInt().coerceIn(0, 15)
                    lessonProgress.setHearts(newValue)
                    updateHeartsHeader()
                    android.widget.Toast.makeText(this, "Životy nastaveny na $newValue", android.widget.Toast.LENGTH_SHORT).show()
                } catch (_: NumberFormatException) {
                    android.widget.Toast.makeText(this, "Neplatná hodnota", android.widget.Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Zrušit") { dialog, _ -> dialog.dismiss() }
        showStyledDialog(heartsBuilder)
    }

    private fun showRenameLionDialog() {
        val prefs = getSharedPreferences("lesson_progress", MODE_PRIVATE)
        val currentName = prefs.getString("lion_name", "Alex") ?: "Alex"
        
        val editText = createDarkDialogEditText().apply {
            setText(currentName)
            inputType = android.text.InputType.TYPE_CLASS_TEXT
            hint = "Zadejte nové jméno lva"
        }
        
        val renameBuilder = AlertDialog.Builder(this)
            .setCustomTitle(createDarkDialogTitle("Přejmenovat lva"))
            .setMessage("Aktuální jméno: $currentName\nZadejte nové jméno:")
            .setView(editText)
            .setPositiveButton("Uložit") { dialog, _ ->
                val newName = editText.text.toString().trim()
                if (newName.isNotEmpty()) {
                    prefs.edit().putString("lion_name", newName).apply()
                    android.widget.Toast.makeText(this, "Jméno lva změněno na: $newName", android.widget.Toast.LENGTH_SHORT).show()
                } else {
                    android.widget.Toast.makeText(this, "Jméno nemůže být prázdné", android.widget.Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Zrušit") { dialog, _ -> dialog.dismiss() }
        showStyledDialog(renameBuilder)
    }

    private fun createDarkDialogEditText(): EditText {
        val editText = EditText(this)
        val padding = (16 * resources.displayMetrics.density).toInt()
        editText.setPadding(padding, padding, padding, padding)
        editText.setTextColor(ContextCompat.getColor(this, R.color.text_primary))
        editText.setHintTextColor(ContextCompat.getColor(this, R.color.text_secondary))
        editText.setBackgroundColor(ContextCompat.getColor(this, R.color.card_dark))
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        val params = ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val margin = (8 * resources.displayMetrics.density).toInt()
        params.setMargins(0, margin, 0, 0)
        editText.layoutParams = params
        editText.gravity = Gravity.START or Gravity.CENTER_VERTICAL
        return editText
    }

    private fun createDarkDialogTitle(titleText: String): TextView {
        val title = TextView(this)
        val padding = (16 * resources.displayMetrics.density).toInt()
        title.text = titleText
        title.setPadding(padding, padding, padding, padding)
        title.setTextColor(ContextCompat.getColor(this, R.color.text_primary))
        title.setBackgroundColor(ContextCompat.getColor(this, R.color.card_dark))
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        title.setTypeface(title.typeface, android.graphics.Typeface.BOLD)
        return title
    }

    private fun showStyledDialog(builder: AlertDialog.Builder): AlertDialog {
        val dialog = builder.create()
        dialog.show()
        val dark = ContextCompat.getColor(this, R.color.card_dark)
        val primary = ContextCompat.getColor(this, R.color.text_primary)
        dialog.window?.setBackgroundDrawable(ColorDrawable(dark))
        (dialog.findViewById<TextView>(androidx.appcompat.R.id.message))?.setTextColor(primary)
        dialog.findViewById<View>(androidx.appcompat.R.id.topPanel)?.setBackgroundColor(dark)
        dialog.findViewById<View>(androidx.appcompat.R.id.contentPanel)?.setBackgroundColor(dark)
        dialog.findViewById<View>(androidx.appcompat.R.id.buttonPanel)?.setBackgroundColor(dark)
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(primary)
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(primary)
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL)?.setTextColor(primary)
        return dialog
    }

    private fun updateHungerDebugBox() {
        val textView = findViewById<TextView>(R.id.hunger_debug_text) ?: return
        val hungerManager = HungerManager(this)
        val current = hungerManager.getCurrentHunger()
        val ratePerHour = hungerManager.getHourlyDecayRate()
        val untilNext = hungerManager.millisUntilNextPoint()
        val frozen = hungerManager.isFrozenNow()
        val freezeUntil = hungerManager.getFreezeUntilEpochMillis()
        val status = if (frozen) {
            val fmt = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
            fmt.format(java.util.Date(freezeUntil))
            "kamení aktivní do ${fmt.format(java.util.Date(freezeUntil))}"
        } else {
            "kamení neaktivní"
        }
        val rateText = String.format(java.util.Locale.getDefault(), "%.2f", ratePerHour)
        val nextText = if (frozen) {
            "Další ubývání po skončení kamení"
        } else if (current <= 0) {
            "Hlad je na nule"
        } else if (untilNext > 0L) {
            val totalSec = (untilNext / 1000).toInt()
            val h = totalSec / 3600
            val m = (totalSec % 3600) / 60
            val s = totalSec % 60
            if (h > 0) String.format(java.util.Locale.getDefault(), "Další -1 za %d:%02d:%02d", h, m, s)
            else String.format(java.util.Locale.getDefault(), "Další -1 za %02d:%02d", m, s)
        } else {
            "Další -1 brzy"
        }
        textView.text = "Aktuální hlad: $current / ${HungerManager.MAX_HUNGER}\nRychlost: -$rateText / hod\n$nextText\n$status"
    }

    private fun toggleDebuggingDropdown() {
        isDebuggingExpanded = !isDebuggingExpanded
        
        if (isDebuggingExpanded) {
            debuggingContent.visibility = LinearLayout.VISIBLE
            debuggingArrow.rotation = 180f
        } else {
            debuggingContent.visibility = LinearLayout.GONE
            debuggingArrow.rotation = 0f
        }
    }

    private fun showPasswordDialog(onSuccess: (Boolean) -> Unit) {
        val expected = DEVELOPER_OPTIONS_PASSWORD
        val editText = createDarkDialogEditText().apply {
            inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            hint = "Zadejte heslo"
        }
        
        val passwordBuilder = AlertDialog.Builder(this)
            .setCustomTitle(createDarkDialogTitle("Heslo pro vývojářské možnosti"))
            .setMessage("Zadejte heslo pro přístup k vývojářským možnostem:")
            .setView(editText)
            .setPositiveButton("Odeslat") { dialog, _ ->
                val enteredPassword = editText.text.toString()
                if (enteredPassword == expected) {
                    onSuccess(true)
                    android.widget.Toast.makeText(this, "Přístup povolen", android.widget.Toast.LENGTH_SHORT).show()
                } else {
                    onSuccess(false)
                    android.widget.Toast.makeText(this, "Nesprávné heslo", android.widget.Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Zrušit") { dialog, _ -> 
                onSuccess(false)
                dialog.dismiss() 
            }
        showStyledDialog(passwordBuilder)
    }
}


