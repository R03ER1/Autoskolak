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
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext

// region Color schemes

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
    outline = GlassWhite,
)

private val LightColors = lightColorScheme(
    primary = LightAccentCyan,
    secondary = LightAccentTeal,
    tertiary = LightAccentBlue,
    background = LightBackground,
    surface = LightSurface,
    surfaceVariant = LightSurfaceVariant,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    onTertiary = androidx.compose.ui.graphics.Color.White,
    onBackground = LightTextPrimary,
    onSurface = LightTextPrimary,
    onSurfaceVariant = LightTextSecondary,
    error = ErrorRed,
    onError = androidx.compose.ui.graphics.Color.White,
    outline = LightGlassBorder,
)

// endregion

// region Theme mode

enum class ThemeMode { LIGHT, DARK, SYSTEM }

val LocalThemeMode = staticCompositionLocalOf { ThemeMode.SYSTEM }

class ThemePreferences(context: Context) {
    private val prefs = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)

    var themeMode: ThemeMode
        get() = ThemeMode.entries.getOrElse(prefs.getInt("theme_mode", 2)) { ThemeMode.SYSTEM }
        set(value) = prefs.edit().putInt("theme_mode", value.ordinal).apply()
}

// endregion

@Composable
fun AutokolkTheme(
    themeMode: ThemeMode = LocalThemeMode.current,
    content: @Composable () -> Unit,
) {
    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    val colorScheme = if (darkTheme) DarkColors else LightColors

    val activity = LocalContext.current as? ComponentActivity
    SideEffect {
        activity?.enableEdgeToEdge(
            statusBarStyle = if (darkTheme)
                SystemBarStyle.dark(Color.TRANSPARENT)
            else
                SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = if (darkTheme)
                SystemBarStyle.dark(Color.TRANSPARENT)
            else
                SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )
    }

    CompositionLocalProvider(LocalThemeMode provides themeMode) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AutokolkTypography,
            shapes = AutokolkShapes,
            content = content,
        )
    }
}
