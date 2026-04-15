package cz.autokolk.ui.theme

import android.content.Context
import android.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

// ---------------------------------------------------------------------------
// Material 3 color schemes
// ---------------------------------------------------------------------------

private val DarkColors = darkColorScheme(
    primary = AccentCyan,
    secondary = AccentTeal,
    tertiary = AccentBlue,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onPrimary = DarkBackground,
    onSecondary = DarkBackground,
    onTertiary = DarkBackground,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    error = ErrorRed,
    onError = TextPrimary,
)

private val LightColors = lightColorScheme(
    primary = LightAccentCyan,
    secondary = LightAccentTeal,
    tertiary = LightAccentBlue,
    background = LightBackground,
    surface = LightSurface,
    surfaceVariant = LightSurfaceVariant,
    onPrimary = LightSurface,
    onSecondary = LightSurface,
    onTertiary = LightSurface,
    onBackground = LightTextPrimary,
    onSurface = LightTextPrimary,
    onSurfaceVariant = LightTextSecondary,
    error = ErrorRed,
    onError = LightSurface,
)

// ---------------------------------------------------------------------------
// Dark-mode preference (persisted in SharedPreferences)
// ---------------------------------------------------------------------------

enum class ThemeMode { SYSTEM, DARK, LIGHT }

private const val PREFS_NAME = "autokolk_theme"
private const val KEY_THEME_MODE = "theme_mode"

/**
 * Reads the stored [ThemeMode] from SharedPreferences.
 * Falls back to [ThemeMode.SYSTEM] when no value has been persisted yet.
 */
fun readThemeMode(context: Context): ThemeMode {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val stored = prefs.getString(KEY_THEME_MODE, null)
    return stored?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() } ?: ThemeMode.SYSTEM
}

fun writeThemeMode(context: Context, mode: ThemeMode) {
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .edit()
        .putString(KEY_THEME_MODE, mode.name)
        .apply()
}

/**
 * Composition-local that tells any composable whether dark theme is currently active.
 * Provided by [AutokolkTheme]; defaults to `true` (dark) when accessed outside a theme.
 */
val LocalIsDarkTheme = compositionLocalOf { true }

// ---------------------------------------------------------------------------
// Root theme composable
// ---------------------------------------------------------------------------

@Composable
fun AutokolkTheme(
    themeMode: ThemeMode? = null,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val resolvedMode by remember(themeMode) {
        mutableStateOf(themeMode ?: readThemeMode(context))
    }

    val systemDark = isSystemInDarkTheme()
    val isDark = when (resolvedMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> systemDark
    }

    val colorScheme = if (isDark) DarkColors else LightColors

    val activity = LocalContext.current as? ComponentActivity
    SideEffect {
        activity?.enableEdgeToEdge(
            statusBarStyle = if (isDark)
                SystemBarStyle.dark(Color.TRANSPARENT)
            else
                SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = if (isDark)
                SystemBarStyle.dark(Color.TRANSPARENT)
            else
                SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )
    }

    CompositionLocalProvider(LocalIsDarkTheme provides isDark) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AutokolkTypography,
            shapes = AutokolkShapes,
            content = content,
        )
    }
}
