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

@Composable
fun AnimatedBackground(
    modifier: Modifier = Modifier,
    accentColor: Color? = null,
    content: @Composable () -> Unit,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "bg")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10_000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "bgOffset",
    )

    val accent = accentColor ?: AccentCyan
    val accent2 = accentColor ?: AccentTeal
    val brush = Brush.radialGradient(
        colors = listOf(
            accent.copy(alpha = if (accentColor != null) 0.12f else 0.05f),
            DarkBackground,
            accent2.copy(alpha = if (accentColor != null) 0.08f else 0.03f),
        ),
        center = Offset(offset * 1000f, offset * 1500f),
        radius = 800f,
    )

    Box(modifier = modifier.background(brush = brush)) {
        content()
    }
}
