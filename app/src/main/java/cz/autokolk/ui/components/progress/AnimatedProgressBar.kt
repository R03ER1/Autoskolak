package cz.autokolk.ui.components.progress

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cz.autokolk.ui.util.rememberLowPerformanceModeEnabled

/**
 * Tenký progress bar s animovaným gradientem (lehký „shimmer“ posun).
 *
 * Krok 157: tato komponenta se používá na mnoha místech současně (Home sekce, Alex
 * hunger bar, Practice karty) a každá instance dřív běžela s vlastní nekonečnou
 * [rememberInfiniteTransition] animací i mimo obrazovku. V nízkovýkonném/reduced-motion
 * režimu ([rememberLowPerformanceModeEnabled]) se shimmer efekt vypíná a vykreslí se
 * jako statický gradient — funkční část (vyplnění podle `progress`) zůstává beze změny.
 */
@Composable
fun AnimatedProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    accent: Color,
    // Theme-aware výchozí track — fixní 10% bílá by ve světlém režimu na světlém pozadí zmizela.
    trackColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
    height: Dp = 8.dp,
) {
    val p = progress.coerceIn(0f, 1f)
    val lowPerformanceMode = rememberLowPerformanceModeEnabled()
    val shift = if (lowPerformanceMode) {
        0.5f
    } else {
        val transition = rememberInfiniteTransition(label = "progressShimmer")
        val animatedShift by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1400, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
            label = "shimmerShift",
        )
        animatedShift
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
    ) {
        val w = size.width
        val h = size.height
        val r = CornerRadius(h / 2f, h / 2f)
        drawRoundRect(color = trackColor, cornerRadius = r, size = size)
        if (p > 0.001f) {
            val fillW = w * p
            clipRect(0f, 0f, fillW, h) {
                val band = w * 0.45f
                val x0 = -band + shift * (w + band * 2f)
                drawRoundRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            accent.copy(alpha = 0.35f),
                            accent.copy(alpha = 0.95f),
                            accent.copy(alpha = 0.4f),
                        ),
                        start = Offset(x0, 0f),
                        end = Offset(x0 + band, h),
                    ),
                    cornerRadius = r,
                    size = Size(fillW, h),
                )
            }
        }
    }
}
