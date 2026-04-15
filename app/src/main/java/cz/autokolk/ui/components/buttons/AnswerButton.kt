package cz.autokolk.ui.components.buttons

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cz.autokolk.ui.theme.AccentCyan
import cz.autokolk.ui.theme.AutokolkShapes
import cz.autokolk.ui.theme.AutokolkTokens
import cz.autokolk.ui.theme.ErrorRed
import cz.autokolk.ui.theme.GlassFill
import cz.autokolk.ui.theme.GlassWhite
import cz.autokolk.ui.theme.SuccessGreen
import cz.autokolk.ui.theme.TextPrimary

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
    val shake = remember { Animatable(0f) }
    LaunchedEffect(state) {
        if (state == AnswerState.WRONG) {
            repeat(3) {
                shake.animateTo(10f, tween(50))
                shake.animateTo(-10f, tween(50))
            }
            shake.animateTo(0f, spring())
        }
    }

    val borderColor by animateColorAsState(
        when (state) {
            AnswerState.CORRECT -> SuccessGreen
            AnswerState.WRONG -> ErrorRed
            AnswerState.SELECTED -> AccentCyan
            AnswerState.DEFAULT -> GlassWhite
        },
        label = "answerBorder",
    )

    val scale by animateFloatAsState(
        targetValue = if (state == AnswerState.SELECTED) 1.02f else 1f,
        animationSpec = spring(dampingRatio = 0.65f),
        label = "answerScale",
    )

    val bgBrush = when (state) {
        AnswerState.CORRECT -> Brush.horizontalGradient(listOf(SuccessGreen.copy(0.85f), SuccessGreen))
        AnswerState.WRONG -> Brush.horizontalGradient(listOf(ErrorRed.copy(0.85f), ErrorRed))
        else -> Brush.linearGradient(listOf(GlassFill, GlassFill.copy(alpha = 0.02f)))
    }

    Box(
        modifier = modifier
            .graphicsLayer { translationX = shake.value }
            .scale(scale)
            .clip(AutokolkShapes.medium)
            .background(bgBrush)
            .border(AutokolkTokens.GlassBorderWidth, borderColor, AutokolkShapes.medium)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            )
            .padding(horizontal = 14.dp, vertical = 12.dp),
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        when (state) {
                            AnswerState.CORRECT -> Color.White.copy(alpha = 0.25f)
                            AnswerState.WRONG -> Color.White.copy(alpha = 0.25f)
                            AnswerState.SELECTED -> AccentCyan.copy(alpha = 0.2f)
                            AnswerState.DEFAULT -> GlassWhite.copy(alpha = 0.15f)
                        },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                when (state) {
                    AnswerState.CORRECT -> Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(22.dp))
                    AnswerState.WRONG -> Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.size(22.dp))
                    else -> Text(
                        label,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary,
                modifier = Modifier.weight(1f),
            )
        }
    }
}
