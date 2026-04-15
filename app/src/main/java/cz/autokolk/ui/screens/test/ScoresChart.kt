package cz.autokolk.ui.screens.test

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import cz.autokolk.ui.theme.AccentCyan
import cz.autokolk.ui.theme.ErrorRed
private val AxisColor = Color(0x66FFFFFF)
private val GridColor = Color(0x33FFFFFF)
private val LineColor = Color(0xFFFFC107)
private val PointColor = Color.White

/**
 * Graf skóre (0–[maxPoints]); [threshold] čárkovaná čára (např. 43 u max 50).
 * [scores] chronologicky zleva doprava (nejstarší první).
 */
@Composable
fun ScoresChart(
    scores: List<Int>,
    threshold: Int = 43,
    maxPoints: Int = 50,
    modifier: Modifier = Modifier,
) {
    val reveal = remember { Animatable(0f) }
    LaunchedEffect(scores) {
        reveal.snapTo(0f)
        reveal.animateTo(1f, tween(durationMillis = 900))
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
    ) {
        val left = 8.dp.toPx()
        val top = 12.dp.toPx()
        val right = size.width - 8.dp.toPx()
        val bottom = size.height - 12.dp.toPx()
        if (right <= left || bottom <= top) return@Canvas

        drawLine(AxisColor, Offset(left, bottom), Offset(right, bottom), strokeWidth = 2f)
        drawLine(AxisColor, Offset(left, top), Offset(left, bottom), strokeWidth = 2f)

        for (i in 1..3) {
            val y = bottom - (bottom - top) * (i / 4f)
            drawLine(GridColor, Offset(left, y), Offset(right, y), strokeWidth = 1f)
        }

        if (scores.isEmpty()) return@Canvas

        val thRatio = (threshold.coerceIn(0, maxPoints)).toFloat() / maxPoints.toFloat()
        val thY = bottom - thRatio * (bottom - top)
        val dash = 12f
        var xDash = left
        while (xDash < right) {
            drawLine(
                ErrorRed,
                Offset(xDash, thY),
                Offset((xDash + dash).coerceAtMost(right), thY),
                strokeWidth = 2f,
            )
            xDash += dash * 2f
        }

        val count = scores.size
        val dx = if (count <= 1) 0f else (right - left) / (count - 1)
        val clipRight = left + (right - left) * reveal.value

        val path = Path()
        val fillPath = Path()
        for ((index, value) in scores.withIndex()) {
            val x = left + dx * index
            if (x > clipRight) break
            val clamped = value.coerceIn(0, maxPoints)
            val ratio = clamped.toFloat() / maxPoints.toFloat()
            val y = bottom - ratio * (bottom - top)
            if (index == 0) {
                path.moveTo(x, y)
                fillPath.moveTo(x, bottom)
                fillPath.lineTo(x, y)
            } else {
                path.lineTo(x, y)
                fillPath.lineTo(x, y)
            }
        }
        val lastX = (left + dx * (scores.size - 1)).coerceAtMost(clipRight)
        fillPath.lineTo(lastX, bottom)
        fillPath.close()

        drawPath(
            fillPath,
            color = AccentCyan.copy(alpha = 0.18f),
        )
        drawPath(
            path,
            color = LineColor,
            style = Stroke(width = 4f, cap = StrokeCap.Round),
        )

        for ((index, value) in scores.withIndex()) {
            val x = left + dx * index
            if (x > clipRight) break
            val clamped = value.coerceIn(0, maxPoints)
            val ratio = clamped.toFloat() / maxPoints.toFloat()
            val y = bottom - ratio * (bottom - top)
            drawCircle(PointColor, radius = 5f, center = Offset(x, y))
            drawCircle(LineColor, radius = 3f, center = Offset(x, y))
        }
    }
}
