package cz.autokolk.ui.screens.quiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cz.autokolk.ui.theme.ErrorRed
import cz.autokolk.ui.theme.SuccessGreen

@Composable
fun QuizResultStrip(
    visible: Boolean,
    correct: Boolean,
    combo: Int,
    funFact: String?,
    wrongDetail: String?,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var toneCorrect by remember { mutableStateOf(false) }
    LaunchedEffect(visible, correct) {
        if (visible) toneCorrect = correct
    }
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically { it } + fadeIn(),
        exit = slideOutVertically { it } + fadeOut(),
    ) {
        val bg = if (toneCorrect) {
            Brush.horizontalGradient(listOf(SuccessGreen.copy(0.92f), SuccessGreen.copy(0.75f)))
        } else {
            Brush.horizontalGradient(listOf(ErrorRed.copy(0.92f), ErrorRed.copy(0.75f)))
        }
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(bg)
                .navigationBarsPadding()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = if (toneCorrect) "Správně! 🎉" else "Špatně 😬",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
            if (toneCorrect && !funFact.isNullOrBlank()) {
                Text(
                    text = "Věděl jsi, že… $funFact",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.92f),
                )
            }
            if (!toneCorrect && !wrongDetail.isNullOrBlank()) {
                Text(
                    text = wrongDetail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.92f),
                )
            }
            if (toneCorrect && combo >= 2) {
                Text(
                    text = when {
                        combo >= 10 -> "💯 ${combo}× v řadě!"
                        combo >= 5 -> "🔥 ${combo}× combo!"
                        else -> "🔥 ${combo}× combo!"
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White.copy(alpha = 0.95f),
                )
            }
            Spacer(Modifier.height(4.dp))
            Button(
                onClick = onContinue,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.22f)),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Pokračovat", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
