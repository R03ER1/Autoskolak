package cz.autokolk.ui.components.progress

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun RingProgress(
    progress: Float,
    size: Dp,
    strokeWidth: Dp,
    trackColor: Color,
    gradient: List<Color>,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val p = progress.coerceIn(0f, 1f)
    val arcColor = gradient.firstOrNull() ?: Color.White
    Box(modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val stroke = strokeWidth.toPx()
            val diameter = this.size.minDimension
            val inner = diameter - stroke
            val topLeft = Offset(
                (this.size.width - diameter) / 2f + stroke / 2f,
                (this.size.height - diameter) / 2f + stroke / 2f,
            )
            val arcSize = Size(inner, inner)
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
            drawArc(
                color = arcColor,
                startAngle = -90f,
                sweepAngle = 360f * p,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
        }
        content()
    }
}
