package cz.autokolk.ui.screens.quiz

import android.app.Activity
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import cz.autokolk.LessonProgress
import cz.autokolk.ui.components.buttons.PrimaryGradientButton
import cz.autokolk.ui.components.glass.GlassCard
import cz.autokolk.ui.components.sheets.RewardedAdHelper
import cz.autokolk.ui.theme.TextPrimary
import cz.autokolk.ui.theme.TextSecondary
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun QuizNoLivesOverlay(
    visible: Boolean,
    lessonProgress: LessonProgress,
    onDismissAfterReward: () -> Unit,
    onGoHome: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (!visible) return
    val context = LocalContext.current
    val activity = context as? Activity
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("lottie/broken_heart.json"))
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.72f)),
        contentAlignment = Alignment.Center,
    ) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
        ) {
            Column(
                Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                LottieAnimation(
                    composition = composition,
                    iterations = 1,
                    modifier = Modifier.size(120.dp),
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "Došly ti životy! 💔",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(8.dp))
                val waitMs = lessonProgress.millisUntilNextHeart().coerceAtLeast(0L)
                val waitText = if (waitMs <= 0L) {
                    "Brzy dostaneš život zpět."
                } else {
                    val m = TimeUnit.MILLISECONDS.toMinutes(waitMs)
                    val s = TimeUnit.MILLISECONDS.toSeconds(waitMs) % 60
                    String.format(Locale.getDefault(), "Další život za %d:%02d", m, s)
                }
                Text(
                    waitText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(20.dp))
                PrimaryGradientButton(
                    text = "Podívat se na reklamu (+1 život)",
                    onClick = {
                        if (activity != null) {
                            RewardedAdHelper.showForHeart(activity, lessonProgress) { ok ->
                                if (ok) onDismissAfterReward()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(10.dp))
                TextButton(
                    onClick = onGoHome,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Zpět na cestu", color = TextSecondary)
                }
            }
        }
    }
}
