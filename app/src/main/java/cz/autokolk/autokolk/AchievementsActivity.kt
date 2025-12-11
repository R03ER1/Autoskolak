package cz.autokolk

import android.graphics.PorterDuff
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class AchievementsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_achievements)

        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        val mgr = AchievementsManager(this)
        val yellowStar = ContextCompat.getColor(this, R.color.secondary) // #FFC107
        val greyStar = 0xFF666666.toInt()

        fun setStarColor(starId: Int, isUnlocked: Boolean) {
            findViewById<ImageView>(starId)?.apply {
                val color = if (isUnlocked) yellowStar else greyStar
                setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
            }
        }

        fun setProgress(progressId: Int, current: Int, tiers: IntArray, unlockedKeys: List<String>) {
            findViewById<TextView>(progressId)?.let { tv ->
                // Find next tier not yet unlocked
                var nextTier = -1
                var nextThreshold = -1
                for (i in tiers.indices) {
                    if (!mgr.isUnlocked(unlockedKeys[i])) {
                        nextTier = i
                        nextThreshold = tiers[i]
                        break
                    }
                }
                if (nextTier == -1) {
                    // All tiers unlocked
                    tv.text = "100%"
                } else {
                    val percent = ((current.toFloat() / nextThreshold.toFloat()) * 100f).toInt().coerceIn(0, 99)
                    tv.text = "${percent}%"
                }
            }
        }

        fun setStars(starIds: List<Int>, unlockedKeys: List<String>) {
            unlockedKeys.forEachIndexed { idx, key ->
                if (idx < starIds.size) {
                    setStarColor(starIds[idx], mgr.isUnlocked(key))
                }
            }
        }

        // Streak
        val streak = LessonProgress(this).getCurrentStreak()
        val streakTiers = intArrayOf(5, 25, 100)
        val streakKeys = listOf("streak_tier_1_unlocked", "streak_tier_2_unlocked", "streak_tier_3_unlocked")
        setStars(listOf(R.id.star_streak_1, R.id.star_streak_2, R.id.star_streak_3), streakKeys)
        setProgress(R.id.progress_streak, streak, streakTiers, streakKeys)

        // Fixes
        val fixes = mgr.getValue("fixes_total")
        val fixesTiers = intArrayOf(20, 50, 200)
        val fixesKeys = listOf("fixes_tier_1_unlocked", "fixes_tier_2_unlocked", "fixes_tier_3_unlocked")
        setStars(listOf(R.id.star_fixes_1, R.id.star_fixes_2, R.id.star_fixes_3), fixesKeys)
        setProgress(R.id.progress_fixes, fixes, fixesTiers, fixesKeys)

        // Correct answers
        val correct = mgr.getValue("answers_correct_total")
        val correctTiers = intArrayOf(100, 500, 1140)
        val correctKeys = listOf("answers_correct_tier_1_unlocked", "answers_correct_tier_2_unlocked", "answers_correct_tier_3_unlocked")
        setStars(listOf(R.id.star_correct_1, R.id.star_correct_2, R.id.star_correct_3), correctKeys)
        setProgress(R.id.progress_correct, correct, correctTiers, correctKeys)

        // Coins earned
        val earned = mgr.getValue("coins_earned_total")
        val earnedTiers = intArrayOf(50, 250, 1000)
        val earnedKeys = listOf("coins_earned_tier_1_unlocked", "coins_earned_tier_2_unlocked", "coins_earned_tier_3_unlocked")
        setStars(listOf(R.id.star_earned_1, R.id.star_earned_2, R.id.star_earned_3), earnedKeys)
        setProgress(R.id.progress_earned, earned, earnedTiers, earnedKeys)

        // Coins spent
        val spent = mgr.getValue("coins_spent_total")
        val spentTiers = intArrayOf(150, 400, 1500)
        val spentKeys = listOf("coins_spent_tier_1_unlocked", "coins_spent_tier_2_unlocked", "coins_spent_tier_3_unlocked")
        setStars(listOf(R.id.star_spent_1, R.id.star_spent_2, R.id.star_spent_3), spentKeys)
        setProgress(R.id.progress_spent, spent, spentTiers, spentKeys)

        // Alex feed streak
        val feedStreak = mgr.getValue("feed_streak")
        val feedStreakTiers = intArrayOf(10, 20, 40)
        val feedStreakKeys = listOf("feed_streak_tier_1_unlocked", "feed_streak_tier_2_unlocked", "feed_streak_tier_3_unlocked")
        setStars(listOf(R.id.star_feed_streak_1, R.id.star_feed_streak_2, R.id.star_feed_streak_3), feedStreakKeys)
        setProgress(R.id.progress_feed_streak, feedStreak, feedStreakTiers, feedStreakKeys)

        // Food items - all three stars turn yellow when target is reached
        fun setFoodProgress(progressId: Int, starIds: List<Int>, current: Int, target: Int, unlockedKey: String) {
            val unlocked = mgr.isUnlocked(unlockedKey)
            starIds.forEach { starId ->
                setStarColor(starId, unlocked)
            }
            findViewById<TextView>(progressId)?.let { tv ->
                if (unlocked) {
                    tv.text = "100%"
                } else {
                    val percent = ((current.toFloat() / target.toFloat()) * 100f).toInt().coerceIn(0, 99)
                    tv.text = "${percent}%"
                }
            }
        }

        setFoodProgress(R.id.progress_food_mrkev, listOf(R.id.star_food_mrkev_1, R.id.star_food_mrkev_2, R.id.star_food_mrkev_3), mgr.getValue("food_mrkev_count"), 60, "food_mrkev_unlocked")
        setFoodProgress(R.id.progress_food_zmrzlina, listOf(R.id.star_food_zmrzlina_1, R.id.star_food_zmrzlina_2, R.id.star_food_zmrzlina_3), mgr.getValue("food_zmrzlina_count"), 90, "food_zmrzlina_unlocked")
        setFoodProgress(R.id.progress_food_kure, listOf(R.id.star_food_kure_1, R.id.star_food_kure_2, R.id.star_food_kure_3), mgr.getValue("food_kure_count"), 30, "food_kure_unlocked")
        setFoodProgress(R.id.progress_food_klobaska, listOf(R.id.star_food_klobaska_1, R.id.star_food_klobaska_2, R.id.star_food_klobaska_3), mgr.getValue("food_klobaska_count"), 250, "food_klobaska_unlocked")
        setFoodProgress(R.id.progress_food_pivo, listOf(R.id.star_food_pivo_1, R.id.star_food_pivo_2, R.id.star_food_pivo_3), mgr.getValue("food_pivo_count"), 5, "food_pivo_unlocked")
        setFoodProgress(R.id.progress_food_kameni, listOf(R.id.star_food_kameni_1, R.id.star_food_kameni_2, R.id.star_food_kameni_3), mgr.getValue("food_kameni_count"), 5, "food_kameni_unlocked")

        findViewById<LinearLayout>(R.id.backButton)?.setOnClickListener { finish() }
    }
}
