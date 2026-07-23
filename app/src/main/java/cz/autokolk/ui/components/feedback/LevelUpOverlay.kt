package cz.autokolk.ui.components.feedback

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.view.HapticFeedbackConstantsCompat
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import cz.autokolk.LevelUpPending
import cz.autokolk.audio.SoundManager
import cz.autokolk.ui.components.animation.ConfettiOverlay
import cz.autokolk.ui.components.buttons.PrimaryGradientButton
import cz.autokolk.ui.theme.TextPrimary
import cz.autokolk.ui.theme.TextSecondary
import cz.autokolk.ui.theme.WarningAmber
import cz.autokolk.ui.util.HapticFeedback

@Composable
fun LevelUpOverlay(
    pending: LevelUpPending,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val view = LocalView.current
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("lottie/level_up.json"))

    LaunchedEffect(Unit) {
        HapticFeedback.onMilestone(view.context)
        SoundManager.play(SoundManager.Sound.LEVELUP)
    }

    Box(
        modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.88f)),
        contentAlignment = Alignment.Center,
    ) {
        ConfettiOverlay(isActive = true, modifier = Modifier.fillMaxSize())
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp),
        ) {
            LottieAnimation(
                composition = composition,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier.size(160.dp),
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Level ${pending.level}!",
                style = MaterialTheme.typography.displaySmall,
                color = WarningAmber,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = pending.title,
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                textAlign = TextAlign.Center,
            )
            if (pending.bonusCoins > 0) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "+${pending.bonusCoins} mincí bonus",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextSecondary,
                )
            }
            Spacer(Modifier.height(28.dp))
            PrimaryGradientButton(
                text = "Paráda!",
                onClick = {
                    try {
                        view.performHapticFeedback(HapticFeedbackConstantsCompat.CONTEXT_CLICK)
                    } catch (_: Throwable) {
                    }
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
