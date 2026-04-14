package cz.autokolk.ui.components.animation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import cz.autokolk.ui.theme.GlassFill
import cz.autokolk.ui.theme.GlassHighlight

fun Modifier.shimmer(
    isLoading: Boolean = true,
    highlightColor: Color = GlassHighlight,
    baseColor: Color = GlassFill,
): Modifier = composed {
    if (!isLoading) return@composed this
    val transition = rememberInfiniteTransition(label = "shimmer")
    val offset by transition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing)),
        label = "shimmerOffset",
    )
    drawWithContent {
        drawContent()
        val brush = Brush.linearGradient(
            colors = listOf(baseColor, highlightColor, baseColor),
            start = Offset(size.width * offset, 0f),
            end = Offset(size.width * (offset + 1f), size.height),
        )
        drawRect(brush = brush)
    }
}
