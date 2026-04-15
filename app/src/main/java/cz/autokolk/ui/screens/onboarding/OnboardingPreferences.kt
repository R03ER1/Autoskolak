package cz.autokolk.ui.screens.onboarding

import android.content.Context
import android.content.SharedPreferences

/**
 * Stav onboardingu a personalizace (sdílené klíče s [cz.autokolk.LessonProgress] pro daily goal).
 */
class OnboardingPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var isCompleted: Boolean
        get() = prefs.getBoolean(KEY_COMPLETED, false)
        set(value) {
            prefs.edit().putBoolean(KEY_COMPLETED, value).apply()
        }

    /** Cíl lekcí za den: 1, 3, 5 nebo 10. */
    var dailyGoal: Int
        get() = prefs.getInt(KEY_DAILY_GOAL, DEFAULT_DAILY_GOAL).coerceIn(1, 10)
        set(value) {
            prefs.edit().putInt(KEY_DAILY_GOAL, value.coerceIn(1, 10)).apply()
        }

    var lionName: String
        get() = prefs.getString(KEY_LION_NAME, DEFAULT_LION_NAME) ?: DEFAULT_LION_NAME
        set(value) {
            val trimmed = value.trim().take(MAX_LION_NAME_LENGTH)
            val toStore = trimmed.ifEmpty { DEFAULT_LION_NAME }
            prefs.edit().putString(KEY_LION_NAME, toStore).apply()
        }

    companion object {
        const val PREFS_NAME = "onboarding_prefs"
        const val KEY_COMPLETED = "onboarding_completed"
        const val KEY_DAILY_GOAL = "daily_goal"
        const val KEY_LION_NAME = "lion_name"

        const val DEFAULT_LION_NAME = "Alex"
        const val DEFAULT_DAILY_GOAL = 3
        private const val MAX_LION_NAME_LENGTH = 20
    }
}
