package cz.autokolk.ui.screens.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import cz.autokolk.ui.theme.AccentCyan
import cz.autokolk.ui.theme.AccentTeal
import kotlin.math.max
import kotlin.math.min

/**
 * Pozadí s křivkou (kubické Bezierovy segmenty) jen přes **měřené** středy uzlů
 * (LazyColumn položky mimo obraz se nekomponují — žádná syntetická sinusoida přes celou výšku).
 * Výkres je oříznut na viewport, aby úseky mimo obrazovka netáhly čáru přes celou plochu.
 * Plný tah = část cesty podle [progressFraction] (PathMeasure).
 */
@Composable
fun LessonPathBackground(
    lessonOrder: List<Int>,
    measuredCenters: Map<Int, Offset>,
    progressFraction: Float,
    modifier: Modifier = Modifier,
    // Theme-aware: onSurface (bílá v tmavém, tmavá v světlém režimu) s nízkou alfou,
    // aby stopa cesty byla vždy viditelná na aktuálním pozadí.
    trackColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.28f),
    dashLength: Float = 18f,
    gapLength: Float = 12f,
) {
    val pathMeasure = remember { PathMeasure() }
    val pf = progressFraction.coerceIn(0f, 1f)
    Canvas(modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val n = lessonOrder.size
        if (n < 2) return@Canvas

        val points = ArrayList<Offset>(n)
        for (id in lessonOrder) {
            measuredCenters[id]?.let { points.add(it) }
        }
        if (points.size < 2) return@Canvas

        val path = buildSmoothPathThrough(points)

        clipRect(0f, 0f, w, h) {
            drawPath(
                path = path,
                color = trackColor,
                style = Stroke(
                    width = 5f,
                    cap = StrokeCap.Round,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashLength, gapLength), 0f),
                ),
            )

            pathMeasure.setPath(path, false)
            val len = pathMeasure.length
            if (len > 0f && pf > 0f) {
                val progressPath = Path()
                val endDist = max(0.001f, len * min(1f, pf * 1.02f))
                pathMeasure.getSegment(0f, endDist, progressPath, true)
                val gradEnd = min(1f, pf * 1.05f)
                drawPath(
                    path = progressPath,
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
    }
}

internal fun buildSmoothPathThrough(points: List<Offset>): Path {
    val path = Path()
    if (points.isEmpty()) return path
    path.moveTo(points.first().x, points.first().y)
    if (points.size == 1) return path
    for (i in 1 until points.size) {
        val p0 = points[i - 1]
        val p3 = points[i]
        val dx = (p3.x - p0.x) * 0.45f
        val dy = (p3.y - p0.y) * 0.45f
        val c1 = Offset(p0.x + dx, p0.y + dy * 0.35f)
        val c2 = Offset(p3.x - dx * 0.35f, p3.y - dy * 0.35f)
        path.cubicTo(c1.x, c1.y, c2.x, c2.y, p3.x, p3.y)
    }
    return path
}
