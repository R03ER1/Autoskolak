package cz.autokolk.ui.components.animation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush

fun Modifier.shimmerLoading(active: Boolean = true): Modifier = composed {
    if (!active) return@composed this
    // Theme-aware: v tmavém i světlém režimu musí být shimmer viditelný — fixní bílá
    // by ve světlém režimu na světlém pozadí zmizela.
    val onSurface = MaterialTheme.colorScheme.onSurface
    val glassFill = onSurface.copy(alpha = 0.06f)
    val highlight = onSurface.copy(alpha = 0.16f)
    val transition = rememberInfiniteTransition(label = "shimmer")
    val shift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmerShift",
    )
    drawWithContent {
        drawContent()
        val w = size.width
        val brush = Brush.linearGradient(
            0f to glassFill,
            0.45f to highlight,
            0.55f to highlight,
            1f to glassFill,
            start = Offset(-w + shift * 2 * w, 0f),
            end = Offset(shift * 2 * w, size.height),
        )
        drawRect(brush = brush)
    }
}
