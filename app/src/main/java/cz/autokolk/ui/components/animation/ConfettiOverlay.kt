package cz.autokolk.ui.components.animation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import cz.autokolk.ui.theme.AccentCyan
import cz.autokolk.ui.theme.AccentTeal
import cz.autokolk.ui.theme.ErrorRed
import cz.autokolk.ui.theme.SuccessGreen
import cz.autokolk.ui.theme.WarningAmber
import kotlin.random.Random

enum class ConfettiShape { RECT, CIRCLE }

private data class Particle(
    val startX: Float,
    val startY: Float,
    val velocityX: Float,
    val velocityY: Float,
    val gravity: Float,
    val rotationSpeed: Float,
    val color: Color,
    val width: Float,
    val height: Float,
    val shape: ConfettiShape,
)

private fun generateParticles(count: Int, colors: List<Color>, seed: Int): List<Particle> {
    val r = Random(seed)
    return List(count) {
        Particle(
            startX = r.nextFloat(),
            startY = r.nextFloat() * 0.25f,
            velocityX = (r.nextFloat() - 0.5f) * 900f,
            velocityY = -r.nextFloat() * 1100f,
            gravity = 2200f,
            rotationSpeed = (r.nextFloat() - 0.5f) * 720f,
            color = colors[r.nextInt(colors.size)],
            width = 6f + r.nextFloat() * 8f,
            height = 5f + r.nextFloat() * 7f,
            shape = if (r.nextBoolean()) ConfettiShape.RECT else ConfettiShape.CIRCLE,
        )
    }
}

@Composable
fun ConfettiOverlay(
    isActive: Boolean,
    modifier: Modifier = Modifier,
    particleCount: Int = 100,
    colors: List<Color> = listOf(AccentCyan, AccentTeal, WarningAmber, ErrorRed, SuccessGreen),
    durationMs: Int = 3000,
) {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(isActive) {
        if (isActive) {
            progress.snapTo(0f)
            progress.animateTo(1f, tween(durationMs, easing = LinearEasing))
        } else {
            progress.snapTo(0f)
        }
    }
    if (!isActive) return
    val t = progress.value
    val particles = remember(colors, particleCount) {
        generateParticles(particleCount, colors, seed = 42)
    }

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val gScale = h / 900f
        particles.forEach { p ->
            val time = t
            val x = p.startX * w + p.velocityX * time
            val y = p.startY * h + p.velocityY * time + 0.5f * p.gravity * time * time * gScale
            val rot = p.rotationSpeed * time
            rotate(rot, Offset(x, y)) {
                when (p.shape) {
                    ConfettiShape.RECT -> drawRect(
                        color = p.color,
                        topLeft = Offset(-p.width / 2f, -p.height / 2f),
                        size = Size(p.width, p.height),
                    )
                    ConfettiShape.CIRCLE -> drawCircle(
                        color = p.color,
                        radius = p.width / 2f,
                        center = Offset.Zero,
                    )
                }
            }
        }
    }
}
