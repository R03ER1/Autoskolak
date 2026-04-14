package cz.autokolk.ui.components.animation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import cz.autokolk.ui.theme.TextPrimary

@Composable
fun AnimatedCounter(
    targetValue: Int,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.titleMedium,
    color: Color = TextPrimary,
) {
    val animatedValue by animateIntAsState(
        targetValue = targetValue,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "counter",
    )
    val s = animatedValue.toString()
    Row(modifier = modifier) {
        s.forEachIndexed { index, char ->
            key("$index-$char") {
                AnimatedContent(
                    targetState = char,
                    transitionSpec = {
                        slideInVertically { -it } + fadeIn() togetherWith
                            slideOutVertically { it } + fadeOut()
                    },
                    label = "digit",
                ) { digit ->
                    Text(
                        text = digit.toString(),
                        style = style,
                        color = color,
                    )
                }
            }
        }
    }
}
