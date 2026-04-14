package cz.autokolk.ui.components.progress

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cz.autokolk.ui.theme.AccentCyan
import cz.autokolk.ui.theme.AccentTeal
import cz.autokolk.ui.theme.GlassWhite
import cz.autokolk.ui.theme.PillShape

@Composable
fun AnimatedProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    gradient: List<Color> = listOf(AccentCyan, AccentTeal),
    height: Dp = 8.dp,
    animated: Boolean = true,
) {
    val p = progress.coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = p,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f),
        label = "progressBar",
    )
    val fill = if (animated) animatedProgress else p

    Canvas(
        modifier = modifier
            .height(height)
            .fillMaxWidth()
            .clip(PillShape),
    ) {
        val r = CornerRadius(size.height / 2f, size.height / 2f)
        drawRoundRect(
            color = GlassWhite.copy(alpha = 0.35f),
            size = size,
            cornerRadius = r,
            style = Fill,
        )
        val w = size.width * fill
        if (w > 0f) {
            drawRoundRect(
                brush = Brush.horizontalGradient(gradient),
                size = Size(w, size.height),
                cornerRadius = r,
            )
            val dotX = w.coerceIn(0f, size.width)
            drawCircle(
                color = gradient.last(),
                radius = size.height / 2f,
                center = Offset(dotX, size.height / 2f),
            )
        }
    }
}

/** Quiz / běžný progress — cyan → teal. */
@Composable
fun QuizProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 8.dp,
) {
    AnimatedProgressBar(
        progress = progress,
        modifier = modifier,
        gradient = listOf(AccentCyan, AccentTeal),
        height = height,
    )
}

/** XP — zlatý gradient. */
@Composable
fun XpProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 8.dp,
) {
    val goldA = Color(0xFFFFD54F)
    val goldB = Color(0xFFFFA000)
    AnimatedProgressBar(
        progress = progress,
        modifier = modifier,
        gradient = listOf(goldA, goldB),
        height = height,
    )
}

/** Hunger — červená → žlutá → zelená podle [hunger01] (0 = hlad, 1 = sytý). */
@Composable
fun HungerProgressBar(
    hunger01: Float,
    modifier: Modifier = Modifier,
    height: Dp = 8.dp,
) {
    val h = hunger01.coerceIn(0f, 1f)
    val c0 = lerp(Color(0xFFFF1744), Color(0xFFFFD600), h.coerceIn(0f, 0.5f) * 2f)
    val c1 = lerp(Color(0xFFFFD600), Color(0xFF00E676), (h - 0.5f).coerceIn(0f, 0.5f) * 2f)
    val a = if (h < 0.5f) c0 else c1
    val b = lerp(a, Color(0xFF00E676), 0.4f)
    AnimatedProgressBar(
        progress = h,
        modifier = modifier,
        gradient = listOf(a, b),
        height = height,
    )
}
