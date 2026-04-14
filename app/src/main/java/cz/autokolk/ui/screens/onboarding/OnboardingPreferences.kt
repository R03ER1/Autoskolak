package cz.autokolk.ui.screens.onboarding

import android.content.Context
import androidx.core.content.edit

/**
 * Ukládání stavu onboardingu a souvisejících voleb.
 * `lion_name` sdílíme se starým kódem (`lesson_progress` prefs, viz [cz.autokolk.autokolk.SettingsActivity]).
 */
object OnboardingPreferences {

    private const val PREFS_NAME = "lesson_progress"

    private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
    private const val KEY_LICENSE_GROUP = "license_group"
    private const val KEY_DAILY_GOAL = "daily_lesson_goal"
    private const val KEY_LION_NAME = "lion_name"
    private const val KEY_NOTIFICATIONS_PROMPT = "onboarding_notifications_prompt_shown"

    fun isCompleted(context: Context): Boolean =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_ONBOARDING_COMPLETED, false)

    fun clearCompletedFlagForReplay(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
            putBoolean(KEY_ONBOARDING_COMPLETED, false)
        }
    }

    fun saveOnboardingResult(
        context: Context,
        draft: OnboardingDraft,
        notificationsOptInShown: Boolean,
    ) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
            putBoolean(KEY_ONBOARDING_COMPLETED, true)
            putString(KEY_LICENSE_GROUP, draft.selectedLicense)
            putInt(KEY_DAILY_GOAL, draft.dailyGoalLessons)
            val name = draft.lionName.trim().ifEmpty { "Alex" }
            putString(KEY_LION_NAME, name)
            putBoolean(KEY_NOTIFICATIONS_PROMPT, notificationsOptInShown)
        }
    }

    fun readLicenseGroup(context: Context): String =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_LICENSE_GROUP, "B") ?: "B"

    fun readDailyGoal(context: Context): Int =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getInt(KEY_DAILY_GOAL, 3)
}
