package cz.autokolk.ui.screens.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import cz.autokolk.ui.theme.AccentCyan
import cz.autokolk.ui.theme.AccentTeal
import cz.autokolk.ui.theme.GlassWhite
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

/**
 * Pozadí s křivkou (kubické Bezierovy segmenty), čárkovaná trať a volitelný gradient „progresu“ podél cesty.
 */
@Composable
fun LessonPathBackground(
    nodeCount: Int,
    progressFraction: Float,
    modifier: Modifier = Modifier,
    trackColor: Color = GlassWhite.copy(alpha = 0.28f),
    dashLength: Float = 18f,
    gapLength: Float = 12f,
) {
    val pf = progressFraction.coerceIn(0f, 1f)
    Canvas(modifier.fillMaxSize()) {
        if (nodeCount < 2) return@Canvas
        val w = size.width
        val h = size.height
        val n = max(nodeCount, 2)
        val path = Path()
        val amp = w * 0.22f
        val points = List(n) { i ->
            val t = i.toFloat() / (n - 1).coerceAtLeast(1)
            val y = t * h * 0.92f + h * 0.04f
            val x = w * 0.5f + amp * sin(i / 4.5).toFloat()
            Offset(x, y)
        }
        path.moveTo(points.first().x, points.first().y)
        for (i in 1 until points.size) {
            val p0 = points[i - 1]
            val p3 = points[i]
            val dx = (p3.x - p0.x) * 0.45f
            val dy = (p3.y - p0.y) * 0.45f
            val c1 = Offset(p0.x + dx, p0.y + dy * 0.35f)
            val c2 = Offset(p3.x - dx * 0.35f, p3.y - dy * 0.35f)
            path.cubicTo(c1.x, c1.y, c2.x, c2.y, p3.x, p3.y)
        }
        drawPath(
            path = path,
            color = trackColor,
            style = Stroke(
                width = 5f,
                cap = StrokeCap.Round,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashLength, gapLength), 0f),
            ),
        )
        val gradEnd = min(1f, pf * 1.05f)
        drawPath(
            path = path,
            brush = Brush.linearGradient(
                colors = listOf(
                    AccentCyan.copy(alpha = 0.15f + 0.55f * gradEnd),
                    AccentTeal.copy(alpha = 0.1f + 0.45f * gradEnd),
                ),
                start = Offset(0f, h * (1f - gradEnd)),
                end = Offset(w, h * gradEnd),
            ),
            style = Stroke(width = 10f, cap = StrokeCap.Round),
        )
    }
}
