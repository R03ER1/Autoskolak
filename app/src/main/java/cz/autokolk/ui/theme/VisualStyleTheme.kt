package cz.autokolk.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Material color scheme + typografie + tvary podle [GameVisualStyle].
 * Světlý/tmavý režim řeší volající ([AutokolkTheme]); zde jen paleta motivu.
 */
fun colorSchemeForVisualStyle(style: GameVisualStyle, isDark: Boolean): ColorScheme {
    val baseDark = darkColorScheme(
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
    val baseLight = lightColorScheme(
        primary = LightAccentCyan,
        secondary = LightAccentTeal,
        tertiary = LightAccentBlue,
        background = LightBackground,
        surface = LightSurface,
        surfaceVariant = LightSurfaceVariant,
        // Krok 159 (WCAG AA audit): bílý text (LightSurface) na #00B8D4/#00BFA5 měl jen
        // 2.3–2.4:1 kontrastu (hluboko pod 4.5:1). Tmavý text (LightTextPrimary) na stejných
        // barvách dává 6.8–7.0:1 — barvy primary/secondary zůstávají nezměněné.
        onPrimary = LightTextPrimary,
        onSecondary = LightTextPrimary,
        onTertiary = LightSurface,
        onBackground = LightTextPrimary,
        onSurface = LightTextPrimary,
        onSurfaceVariant = LightTextSecondary,
        error = ErrorRed,
        onError = LightSurface,
    )
    if (style == GameVisualStyle.CLASSIC) {
        return if (isDark) baseDark else baseLight
    }
    if (!isDark) {
        return when (style) {
            GameVisualStyle.NEON_GRID -> lightColorScheme(
                primary = Color(0xFF7C3AED),
                // Krok 159 (WCAG AA audit): #0891B2 s bílým textem měla jen 3.68:1. Lehce
                // ztemněno na #07819E (stejný odstín tyrkysové) — s bílým textem 4.52:1.
                secondary = Color(0xFF07819E),
                tertiary = Color(0xFF4F46E5),
                background = Color(0xFFF5F3FF),
                surface = Color(0xFFFFFFFF),
                surfaceVariant = Color(0xFFE9E3FF),
                onPrimary = Color.White,
                onSecondary = Color.White,
                onTertiary = Color.White,
                onBackground = LightTextPrimary,
                onSurface = LightTextPrimary,
                onSurfaceVariant = LightTextSecondary,
                error = ErrorRed,
                onError = LightSurface,
            )
            GameVisualStyle.SUNSET_WARM -> lightColorScheme(
                // Krok 159 (WCAG AA audit): #E65100 s bílým textem měla jen 3.79:1. Lehce
                // ztemněno na #CC4800 (stejný odstín pálené oranžové) — s bílým textem 4.67:1.
                primary = Color(0xFFCC4800),
                secondary = Color(0xFFFF8F00),
                tertiary = Color(0xFFFF6D00),
                background = Color(0xFFFFF8F0),
                surface = Color(0xFFFFFFFF),
                surfaceVariant = Color(0xFFFFE8D6),
                onPrimary = Color.White,
                onSecondary = Color(0xFF3E2723),
                // Krok 159: bílý text na #FF6D00 měl jen 2.82:1. Tmavý text (stejný odstín
                // jako onPrimary v tmavém režimu) dává 5.65:1 — barva tertiary je nezměněná.
                onTertiary = Color(0xFF3E1508),
                onBackground = LightTextPrimary,
                onSurface = LightTextPrimary,
                onSurfaceVariant = LightTextSecondary,
                error = ErrorRed,
                onError = LightSurface,
            )
            else -> baseLight
        }
    }
    return when (style) {
        GameVisualStyle.NEON_GRID -> darkColorScheme(
            primary = Color(0xFFD946EF),
            secondary = Color(0xFF22D3EE),
            tertiary = Color(0xFFA78BFA),
            background = Color(0xFF0B0618),
            surface = Color(0xFF151028),
            surfaceVariant = Color(0xFF221C3A),
            onPrimary = Color(0xFF120018),
            onSecondary = Color(0xFF001018),
            onTertiary = Color(0xFF0D0820),
            onBackground = TextPrimary,
            onSurface = TextPrimary,
            onSurfaceVariant = TextSecondary,
            error = ErrorRed,
            onError = TextPrimary,
        )
        GameVisualStyle.SUNSET_WARM -> darkColorScheme(
            primary = Color(0xFFFFAB91),
            secondary = Color(0xFFFFCC80),
            tertiary = Color(0xFFFF8A65),
            background = Color(0xFF140E0C),
            surface = Color(0xFF241A16),
            surfaceVariant = Color(0xFF33251F),
            onPrimary = Color(0xFF3E1508),
            onSecondary = Color(0xFF3E2723),
            onTertiary = Color(0xFF2D140A),
            onBackground = TextPrimary,
            onSurface = TextPrimary,
            onSurfaceVariant = TextSecondary,
            error = ErrorRed,
            onError = TextPrimary,
        )
        else -> baseDark
    }
}

private fun TextStyle.withLetterSpacing(extraSp: Float): TextStyle {
    val baseVal = if (letterSpacing.type == TextUnitType.Sp) letterSpacing.value else 0f
    return copy(letterSpacing = (baseVal + extraSp).sp)
}

private fun TextStyle.withFontSizeDelta(deltaSp: Float): TextStyle {
    val baseVal = if (fontSize.type == TextUnitType.Sp) fontSize.value else 0f
    return copy(fontSize = (baseVal + deltaSp).sp)
}

fun typographyForVisualStyle(style: GameVisualStyle): Typography {
    val t = AutokolkTypography
    return when (style) {
        GameVisualStyle.CLASSIC -> t
        GameVisualStyle.NEON_GRID -> t.copy(
            displayLarge = t.displayLarge.withLetterSpacing(0.4f),
            displayMedium = t.displayMedium.withLetterSpacing(0.35f),
            displaySmall = t.displaySmall.withLetterSpacing(0.3f),
            headlineLarge = t.headlineLarge.withLetterSpacing(0.35f),
            headlineMedium = t.headlineMedium.withLetterSpacing(0.3f),
            headlineSmall = t.headlineSmall.withLetterSpacing(0.25f),
            titleLarge = t.titleLarge.withLetterSpacing(0.2f),
            bodyLarge = t.bodyLarge.withLetterSpacing(0.12f),
            bodyMedium = t.bodyMedium.withLetterSpacing(0.1f),
        )
        GameVisualStyle.SUNSET_WARM -> t.copy(
            headlineLarge = t.headlineLarge.withFontSizeDelta(0.5f),
            headlineMedium = t.headlineMedium.withFontSizeDelta(0.5f),
            titleLarge = t.titleLarge.withFontSizeDelta(0.5f),
            bodyLarge = t.bodyLarge.withFontSizeDelta(0.5f),
            bodyMedium = t.bodyMedium.withFontSizeDelta(0.5f),
            bodySmall = t.bodySmall.withFontSizeDelta(0.5f),
        )
    }
}

fun shapesForVisualStyle(style: GameVisualStyle): Shapes {
    return when (style) {
        GameVisualStyle.CLASSIC -> AutokolkShapes
        GameVisualStyle.NEON_GRID -> Shapes(
            extraSmall = RoundedCornerShape(6.dp),
            small = RoundedCornerShape(10.dp),
            medium = RoundedCornerShape(12.dp),
            large = RoundedCornerShape(18.dp),
            extraLarge = RoundedCornerShape(22.dp),
        )
        GameVisualStyle.SUNSET_WARM -> Shapes(
            extraSmall = RoundedCornerShape(12.dp),
            small = RoundedCornerShape(16.dp),
            medium = RoundedCornerShape(22.dp),
            large = RoundedCornerShape(28.dp),
            extraLarge = RoundedCornerShape(40.dp),
        )
    }
}
