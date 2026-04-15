package cz.autokolk.ui.components.animation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import cz.autokolk.ui.theme.AccentCyan
import cz.autokolk.ui.theme.AccentTeal
import cz.autokolk.ui.theme.DarkBackground
import cz.autokolk.ui.theme.LightAccentTeal
import cz.autokolk.ui.theme.LightBackground
import cz.autokolk.ui.theme.LocalIsDarkTheme

/**
 * Jemný animovaný radial gradient na pozadí (Fáze 2 / onboarding).
 */
@Composable
fun AnimatedBackground(
    modifier: Modifier = Modifier,
    accentColor: Color = AccentCyan,
    content: @Composable () -> Unit,
) {
    val isDark = LocalIsDarkTheme.current
    val baseBg = if (isDark) DarkBackground else LightBackground
    val secondaryAccent = if (isDark) AccentTeal else LightAccentTeal
    val primaryAlpha = if (isDark) 0.05f else 0.08f
    val secondaryAlpha = if (isDark) 0.03f else 0.06f

    val infiniteTransition = rememberInfiniteTransition(label = "animatedBg")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10_000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "animatedBgOffset",
    )

    Box(
        modifier = modifier.background(
            Brush.radialGradient(
                colors = listOf(
                    accentColor.copy(alpha = primaryAlpha),
                    baseBg,
                    secondaryAccent.copy(alpha = secondaryAlpha),
                ),
                center = Offset(offset * 1000f, offset * 1500f),
                radius = 800f,
            ),
        ),
    ) {
        content()
    }
}
