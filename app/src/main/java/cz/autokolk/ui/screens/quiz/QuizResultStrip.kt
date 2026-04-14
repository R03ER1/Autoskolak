package cz.autokolk.ui.screens.quiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import cz.autokolk.ui.theme.ErrorRed
import cz.autokolk.ui.theme.SuccessGreen
import cz.autokolk.ui.theme.TextPrimary

@Composable
fun QuizResultStrip(
    visible: Boolean,
    correct: Boolean,
    combo: Int,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically { it },
        exit = slideOutVertically { it },
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = if (correct) {
                            listOf(SuccessGreen, SuccessGreen.copy(alpha = 0.85f))
                        } else {
                            listOf(ErrorRed, ErrorRed.copy(alpha = 0.85f))
                        },
                    ),
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = if (correct) "Správně! 🔥×$combo" else "Špatně",
                color = TextPrimary,
            )
            Button(onClick = onContinue) {
                Text("Dál")
            }
        }
    }
}
