package cz.autokolk.ui.screens.home

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cz.autokolk.ui.theme.AccentCyan
import cz.autokolk.ui.theme.AccentTeal
import cz.autokolk.ui.util.rememberLowPerformanceModeEnabled

@Composable
fun Modifier.pulsingGlow(
    enabled: Boolean,
    cornerRadius: Dp = 999.dp,
    colors: List<Color> = listOf(AccentCyan.copy(alpha = 0.55f), AccentTeal.copy(alpha = 0.35f)),
): Modifier {
    if (!enabled) return this
    // Krok 160: v reduced-motion / low-power režimu zůstává glow statický (bez nekonečné
    // animace), aby neběžel kontinuální redraw jen kvůli čistě dekorativnímu pulzování.
    val lowPerformanceMode = rememberLowPerformanceModeEnabled()
    val phase = if (lowPerformanceMode) {
        1f
    } else {
        val transition = rememberInfiniteTransition(label = "pulsingGlow")
        val animatedPhase by transition.animateFloat(
            initialValue = 0.65f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(900, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "pulsingGlowPhase",
        )
        animatedPhase
    }
    return this.drawBehind {
        val r = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx())
        val stroke = 3.dp.toPx() * phase
        drawRoundRect(
            brush = Brush.linearGradient(colors),
            cornerRadius = r,
            style = Stroke(width = stroke),
        )
    }
}
