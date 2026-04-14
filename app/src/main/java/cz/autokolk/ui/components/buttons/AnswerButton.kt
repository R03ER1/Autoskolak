package cz.autokolk.ui.components.buttons

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.matchParentSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.autokolk.ui.theme.AccentCyan
import cz.autokolk.ui.theme.AutokolkTokens
import cz.autokolk.ui.theme.ErrorRed
import cz.autokolk.ui.theme.GlassFill
import cz.autokolk.ui.theme.GlassWhite
import cz.autokolk.ui.theme.PillShape
import cz.autokolk.ui.theme.SuccessGreen
import cz.autokolk.ui.theme.TextPrimary
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

enum class AnswerState {
    DEFAULT,
    SELECTED,
    CORRECT,
    WRONG,
}

@Composable
fun AnswerButton(
    text: String,
    state: AnswerState,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val shake = remember { Animatable(0f) }
    LaunchedEffect(state) {
        if (state == AnswerState.WRONG) {
            for (i in 0 until 3) {
                shake.animateTo(10f, tween(50))
                shake.animateTo(-10f, tween(50))
            }
            shake.animateTo(0f, spring())
        } else {
            shake.snapTo(0f)
        }
    }

    val borderColor by animateColorAsState(
        targetValue = when (state) {
            AnswerState.CORRECT -> SuccessGreen
            AnswerState.WRONG -> ErrorRed
            AnswerState.SELECTED -> AccentCyan
            AnswerState.DEFAULT -> GlassWhite
        },
        label = "answerBorder",
    )

    val scale by animateFloatAsState(
        targetValue = when (state) {
            AnswerState.SELECTED -> 1.03f
            else -> 1f
        },
        animationSpec = spring(dampingRatio = 0.65f),
        label = "answerScale",
    )

    val pulse = rememberInfiniteTransition(label = "answerPulse")
    val pulseScale by pulse.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "answerPulseScale",
    )
    val effectiveScale = if (state == AnswerState.CORRECT) scale * pulseScale else scale

    val bgBrush: Brush? = when (state) {
        AnswerState.CORRECT -> Brush.horizontalGradient(
            listOf(SuccessGreen, SuccessGreen.copy(alpha = 0.85f)),
        )
        AnswerState.WRONG -> Brush.horizontalGradient(
            listOf(ErrorRed, ErrorRed.copy(alpha = 0.85f)),
        )
        else -> null
    }

    Box(
        modifier = modifier
            .graphicsLayer { translationX = shake.value }
            .scale(effectiveScale)
            .clip(PillShape)
            .background(
                brush = bgBrush ?: Brush.linearGradient(listOf(GlassFill, GlassFill)),
                shape = PillShape,
            )
            .border(
                width = AutokolkTokens.GlassBorderWidth * if (state == AnswerState.SELECTED) 1.5f else 1f,
                color = borderColor,
                shape = PillShape,
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(),
                enabled = enabled && state != AnswerState.CORRECT && state != AnswerState.WRONG,
                role = Role.Button,
                onClick = onClick,
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        if (state == AnswerState.CORRECT) {
            AnswerConfettiBurst(
                modifier = Modifier
                    .matchParentSize()
                    .clip(PillShape),
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(GlassWhite.copy(alpha = 0.2f))
                    .border(AutokolkTokens.GlassBorderWidth, GlassWhite.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                )
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = text,
                color = TextPrimary,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f),
            )
            when (state) {
                AnswerState.CORRECT -> Text("✓", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                AnswerState.WRONG -> Text("✗", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                else -> {}
            }
        }
    }
}

@Composable
private fun AnswerConfettiBurst(modifier: Modifier = Modifier) {
    val particles = remember {
        List(14) { i ->
            Triple(
                (i * 27.0) % 360.0,
                0.4f + (i % 3) * 0.08f,
                Color(
                    red = (i * 17 % 100) / 100f,
                    green = (i * 31 % 100) / 100f,
                    blue = (i * 47 % 100) / 100f,
                    alpha = 0.85f,
                ),
            )
        }
    }
    var started by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        started = true
    }
    val t by animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = tween(700, easing = LinearEasing),
        label = "confettiProgress",
    )

    Canvas(modifier) {
        val cx = size.width * 0.85f
        val cy = size.height * 0.5f
        val maxR = size.minDimension * 0.55f
        particles.forEach { (deg, speed, color) ->
            val rad = deg * PI / 180.0
            val r = maxR * speed * t
            val x = (cx + cos(rad) * r).toFloat()
            val y = (cy + sin(rad) * r).toFloat()
            drawCircle(color = color, radius = 3.dp.toPx(), center = Offset(x, y))
        }
    }
}
