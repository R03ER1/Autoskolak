package com.example.autokolk

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AchievementsManager(private val context: Context) {
    private val prefs = context.getSharedPreferences("achievements", Context.MODE_PRIVATE)

    // Thresholds
    private val streakTiers = intArrayOf(5, 25, 100)
    private val fixesTiers = intArrayOf(20, 50, 200)
    private val correctTiers = intArrayOf(100, 500, 1140)
    private val coinsEarnedTiers = intArrayOf(50, 250, 1000)
    private val coinsSpentTiers = intArrayOf(150, 400, 1500)
    private val feedStreakTiers = intArrayOf(10, 20, 40)

    // Food counters target
    private val foodTargets = mapOf(
        // Keys aligned with Alex UI ids
        "mrkev" to 60,
        "zmrzlina" to 90,
        "kure" to 30,
        "klobaska" to 250,
        "pivo" to 5,
        "kameni" to 5
    )

    fun onPointsEarned(amount: Int) {
        if (amount <= 0) return
        val total = prefs.getInt("coins_earned_total", 0) + amount
        prefs.edit().putInt("coins_earned_total", total).apply()
        checkTiers("coins_earned", total, coinsEarnedTiers)
    }

    fun onPointsSpent(amount: Int) {
        if (amount <= 0) return
        val total = prefs.getInt("coins_spent_total", 0) + amount
        prefs.edit().putInt("coins_spent_total", total).apply()
        checkTiers("coins_spent", total, coinsSpentTiers)
    }

    fun onAnswer(isCorrect: Boolean) {
        if (!isCorrect) return
        val total = prefs.getInt("answers_correct_total", 0) + 1
        prefs.edit().putInt("answers_correct_total", total).apply()
        checkTiers("answers_correct", total, correctTiers)
    }

    fun onPracticeFix(wasPreviouslyWrong: Boolean, nowCorrect: Boolean) {
        if (wasPreviouslyWrong && nowCorrect) {
            val total = prefs.getInt("fixes_total", 0) + 1
            prefs.edit().putInt("fixes_total", total).apply()
            checkTiers("fixes", total, fixesTiers)
        }
    }

    fun onStreakUpdated(currentStreak: Int) {
        checkTiers("streak", currentStreak, streakTiers)
    }

    fun onTestCorrectAdded(count: Int) {
        if (count <= 0) return
        val total = prefs.getInt("answers_correct_total", 0) + count
        prefs.edit().putInt("answers_correct_total", total).apply()
        checkTiers("answers_correct", total, correctTiers)
    }

    fun onFed(foodKey: String) {
        val key = "food_${foodKey}_count"
        val newCount = prefs.getInt(key, 0) + 1
        prefs.edit().putInt(key, newCount).apply()
        val target = foodTargets[foodKey]
        if (target != null) {
            checkSingleTarget("food_${foodKey}", newCount, target)
        }
        updateDailyFeedStreak()
    }

    private fun updateDailyFeedStreak() {
        val today = todayString()
        val last = prefs.getString("last_fed_date", null)
        var streak = prefs.getInt("feed_streak", 0)
        if (last == today) {
            // already counted today
        } else if (last == yesterdayString()) {
            streak += 1
            prefs.edit().putString("last_fed_date", today).putInt("feed_streak", streak).apply()
        } else {
            streak = 1
            prefs.edit().putString("last_fed_date", today).putInt("feed_streak", streak).apply()
        }
        checkTiers("feed_streak", streak, feedStreakTiers)
    }

    private fun checkTiers(prefix: String, value: Int, tiers: IntArray) {
        tiers.forEachIndexed { idx, threshold ->
            val unlockedKey = "${prefix}_tier_${idx+1}_unlocked"
            if (!prefs.getBoolean(unlockedKey, false) && value >= threshold) {
                // Unlock and reward
                prefs.edit().putBoolean(unlockedKey, true).apply()
                reward()
            }
        }
    }

    private fun checkSingleTarget(keyPrefix: String, value: Int, target: Int) {
        val unlockedKey = "${keyPrefix}_unlocked"
        if (!prefs.getBoolean(unlockedKey, false) && value >= target) {
            prefs.edit().putBoolean(unlockedKey, true).apply()
            reward()
        }
    }

    private fun reward() {
        // Award 150 points on each achievement completion
        try {
            LessonProgress(context).addPoints(150)
            onPointsEarned(150) // Count towards earned total
        } catch (_: Throwable) { }
    }

    private fun todayString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun yesterdayString(): String {
        val cal = java.util.Calendar.getInstance()
        cal.add(java.util.Calendar.DATE, -1)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(cal.time)
    }

    // Expose simple getters for UI
    fun getValue(key: String, def: Int = 0): Int = prefs.getInt(key, def)
    fun isUnlocked(key: String): Boolean = prefs.getBoolean(key, false)

    fun clearAll() {
        prefs.edit().clear().apply()
    }
}


