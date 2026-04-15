package cz.autokolk.ui.settings

import android.content.Context

/**
 * Uživatelské předvolby pro zvuk, vibrace a biometrický zámek (SharedPreferences).
 */
object AppSettingsStore {
    private const val PREFS = "autokolk_app_settings"
    private const val KEY_SOUND = "sound_enabled"
    private const val KEY_HAPTIC = "haptic_enabled"
    private const val KEY_BIOMETRIC = "biometric_lock_enabled"

    fun isSoundEnabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_SOUND, true)

    fun setSoundEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(KEY_SOUND, enabled).apply()
    }

    fun isHapticEnabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_HAPTIC, true)

    fun setHapticEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(KEY_HAPTIC, enabled).apply()
    }

    fun isBiometricLockEnabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_BIOMETRIC, false)

    fun setBiometricLockEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(KEY_BIOMETRIC, enabled).apply()
    }
}
