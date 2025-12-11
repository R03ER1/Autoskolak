package cz.autokolk

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.view.ViewGroup
import android.view.View
import android.widget.LinearLayout
import android.graphics.drawable.GradientDrawable

class PracticeActivity : AppCompatActivity() {
    private lateinit var lessonProgress: LessonProgress
    private lateinit var streakButton: MaterialButton
    private lateinit var xpButton: MaterialButton
    private lateinit var heartsButton: MaterialButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_practice)

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

        bottomNav.selectedItemId = R.id.nav_exercise
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
                R.id.nav_exercise -> true
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
        findViewById<TextView>(R.id.pageTitle)?.text = "💪 Practice"

        // Build practice category boxes
        buildPracticeBoxes()
    }

    override fun onResume() {
        super.onResume()
        lessonProgress.normalizeStreakForToday()
        updateStreakHeader()
        updatePointsHeader()
        updateHeartsHeader()
        // Refresh boxes to reflect latest practice progress
        buildPracticeBoxes()

        // Tutorial overlay for Practice page
        TutorialManager.showIfNeeded(
            this,
            key = "tutorial_practice",
            message = "Tady můžeš projíždět otázky jednotlivých kategorií, zkoušet otázky, které ti tolik nejdou, nebo náhodně klikat na odpovědi."
        )
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
        // Hide + button for non-streak sheets
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

    private fun buildPracticeBoxes() {
        val container = findViewById<LinearLayout>(R.id.practiceContainer) ?: return
        container.removeAllViews()

        // Title already present as first child; ensure spacing
        val titleView = findViewById<TextView>(R.id.pageTitle)
        titleView?.text = "💪 Practice"

        val desiredOrder = listOf("def", "bez", "prav", "znak", "res", "voz", "souv", "cdt")
        val categoryGroups = lessonProgress.getCategoryGroups()
            .sortedBy { group ->
                val idx = desiredOrder.indexOf(group.category.lowercase())
                if (idx >= 0) idx else Int.MAX_VALUE
            }

        for (group in categoryGroups) {
            val box = createCategoryBox(
                categoryCode = group.category,
                subcategories = group.subcategories
            )
            container.addView(box)
        }
    }

    private fun createCategoryBox(categoryCode: String, subcategories: List<SubcategoryGroup>): View {
        val outer = LinearLayout(this)
        outer.orientation = LinearLayout.VERTICAL
        val outerLp = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        outerLp.topMargin = dp(12)
        outer.layoutParams = outerLp
        outer.setPadding(dp(16), dp(14), dp(16), dp(14))

        val background = GradientDrawable()
        background.setColor(Color.parseColor("#1E1E1E"))
        background.cornerRadius = dp(12).toFloat()
        outer.background = background

        // Category title
        val title = TextView(this)
        title.text = mapCategoryDisplayName(categoryCode)
        title.setTextColor(ContextCompat.getColor(this, R.color.text_primary))
        title.textSize = 18f
        title.setPadding(0, 0, 0, dp(6))
        outer.addView(title)

        // Subcategory lines
        for (sub in subcategories) {
            val row = LinearLayout(this)
            row.orientation = LinearLayout.HORIZONTAL
            val rowLp = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            row.layoutParams = rowLp

            val left = TextView(this)
            left.text = "• " + mapSubcategoryDisplayName(sub.subcategory)
            left.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))
            left.textSize = 14f
            val leftLp = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
            leftLp.weight = 1f
            left.layoutParams = leftLp

            val right = TextView(this)
            right.text = "${sub.questionCount}"
            right.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))
            right.textSize = 14f

            row.addView(left)
            row.addView(right)
            outer.addView(row)
        }

        // Status chips row using practice progress: ✅ correct | ❌ wrong | ❔ unanswered
        val totalCount = subcategories.sumOf { it.questionCount }
        val (correctIds, wrongIds) = lessonProgress.getPracticeStatus(categoryCode)
        val correctCount = correctIds.size
        val wrongCount = wrongIds.size
        val unansweredCount = (totalCount - correctCount - wrongCount).coerceAtLeast(0)
        val chipsRow = LinearLayout(this)
        chipsRow.orientation = LinearLayout.HORIZONTAL
        chipsRow.gravity = android.view.Gravity.CENTER_HORIZONTAL
        val chipsLp = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        chipsLp.topMargin = dp(8)
        chipsRow.layoutParams = chipsLp

        fun makeChip(text: String): TextView {
            val tv = TextView(this)
            tv.text = text
            tv.setTextColor(ContextCompat.getColor(this, R.color.text_primary))
            tv.textSize = 13f
            tv.setPadding(dp(8), dp(4), dp(8), dp(4))
            val chipBg = GradientDrawable()
            chipBg.setColor(Color.parseColor("#2A2A2A"))
            chipBg.cornerRadius = dp(8).toFloat()
            tv.background = chipBg
            tv.isClickable = true
            tv.isFocusable = true
            tv.gravity = android.view.Gravity.CENTER
            val lp = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
            lp.weight = 1f
            lp.leftMargin = dp(4)
            lp.rightMargin = dp(4)
            tv.layoutParams = lp
            return tv
        }

        val correctChip = makeChip("✅ $correctCount")
        val wrongChip = makeChip("❌ $wrongCount")
        val unansweredChip = makeChip("❔$unansweredCount")

        correctChip.setOnClickListener {
            val intent = android.content.Intent(this, MainActivity::class.java)
            intent.putExtra(MainActivity.EXTRA_CATEGORY, categoryCode)
            intent.putExtra(MainActivity.EXTRA_PRACTICE_MODE, MainActivity.PRACTICE_MODE_CORRECT)
            startActivity(intent)
        }
        wrongChip.setOnClickListener {
            val intent = android.content.Intent(this, MainActivity::class.java)
            intent.putExtra(MainActivity.EXTRA_CATEGORY, categoryCode)
            intent.putExtra(MainActivity.EXTRA_PRACTICE_MODE, MainActivity.PRACTICE_MODE_WRONG)
            startActivity(intent)
        }
        unansweredChip.setOnClickListener {
            val intent = android.content.Intent(this, MainActivity::class.java)
            intent.putExtra(MainActivity.EXTRA_CATEGORY, categoryCode)
            intent.putExtra(MainActivity.EXTRA_PRACTICE_MODE, MainActivity.PRACTICE_MODE_UNANSWERED)
            startActivity(intent)
        }

        chipsRow.addView(correctChip)
        chipsRow.addView(wrongChip)
        chipsRow.addView(unansweredChip)
        outer.addView(chipsRow)

        // Click starts practice for this category
        outer.isClickable = true
        outer.setOnClickListener {
            val intent = android.content.Intent(this, MainActivity::class.java)
            intent.putExtra(MainActivity.EXTRA_CATEGORY, categoryCode)
            startActivity(intent)
        }

        return outer
    }

    private fun dp(value: Int): Int {
        val density = resources.displayMetrics.density
        return (value * density).toInt()
    }

    private fun capitalizeFirst(text: String): String {
        if (text.isEmpty()) return text
        return text.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
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
            // CDT merged category - show as subcategories
            "c" -> "skupina C"
            "d" -> "skupina D"
            "t" -> "skupina T"
            else -> code
        }
        return capitalizeFirst(name)
    }
}


