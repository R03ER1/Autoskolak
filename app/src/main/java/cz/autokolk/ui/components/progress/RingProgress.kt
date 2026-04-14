package cz.autokolk.ui.components.progress

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cz.autokolk.ui.theme.AccentCyan
import cz.autokolk.ui.theme.AccentTeal
import cz.autokolk.ui.theme.GlassWhite

@Composable
fun RingProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 4.dp,
    gradient: List<Color> = listOf(AccentCyan, AccentTeal),
    trackColor: Color = GlassWhite.copy(alpha = 0.35f),
    size: Dp = 64.dp,
    content: @Composable () -> Unit = {},
) {
    val p = progress.coerceIn(0f, 1f)
    val sweep by animateFloatAsState(
        targetValue = p * 360f,
        animationSpec = spring(dampingRatio = 0.8f),
        label = "ringSweep",
    )
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val sw = strokeWidth.toPx()
            val arcSize = Size(this.size.minDimension - sw, this.size.minDimension - sw)
            val topLeft = Offset(sw / 2f, sw / 2f)
            val stroke = Stroke(width = sw, cap = StrokeCap.Round)
            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = stroke,
            )
            val c = Offset(this.size.width / 2f, this.size.height / 2f)
            drawArc(
                brush = Brush.sweepGradient(colors = gradient, center = c),
                startAngle = -90f,
                sweepAngle = sweep,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = stroke,
            )
        }
        content()
    }
}
