package cz.autokolk.ui.components.animation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

@Composable
fun AnimatedCounter(
    targetValue: Int,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.titleMedium,
    color: Color = MaterialTheme.colorScheme.onSurface,
) {
    var oldValue by remember { mutableIntStateOf(targetValue) }
    SideEffect { oldValue = targetValue }

    val targetStr = targetValue.toString()
    val oldStr = oldValue.toString()

    Row(modifier = modifier) {
        targetStr.forEachIndexed { index, char ->
            val oldChar = oldStr.getOrNull(index)
            val goingUp = targetValue > oldValue

            AnimatedContent(
                targetState = char,
                transitionSpec = {
                    if (oldChar == char) {
                        slideInVertically { 0 } togetherWith slideOutVertically { 0 }
                    } else {
                        slideInVertically { h -> if (goingUp) h else -h } togetherWith
                            slideOutVertically { h -> if (goingUp) -h else h }
                    }
                },
                label = "digit_$index",
            ) { digit ->
                Text(
                    text = digit.toString(),
                    style = style,
                    color = color,
                    softWrap = false,
                )
            }
        }
    }
}
