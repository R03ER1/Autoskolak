package cz.autokolk.ui.components.feedback

import android.app.Activity
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import cz.autokolk.audio.SoundManager
import cz.autokolk.ui.theme.AccentCyan
import cz.autokolk.ui.theme.AutokolkTheme
import cz.autokolk.ui.theme.TextPrimary
import cz.autokolk.ui.theme.TextSecondary
import cz.autokolk.ui.theme.readThemeMode

/**
 * Celoobrazovkový efekt při odemčení úspěchu (Lottie + volitelně konfety).
 */
object AchievementUnlockOverlay {

    fun show(activity: Activity, achievementName: String, valueLine: String) {
        if (activity.isFinishing) return
        if (Build.VERSION.SDK_INT >= 17 && activity.isDestroyed) return

        vibrateSuccess(activity)
        SoundManager.play(SoundManager.Sound.LEVELUP)

        val root = activity.findViewById<ViewGroup>(android.R.id.content) ?: return
        val composeView = ComposeView(activity).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
            setContent {
                AutokolkTheme(themeMode = readThemeMode(activity)) {
                    AchievementUnlockContent(
                        achievementName = achievementName,
                        valueLine = valueLine,
                        onDismiss = {
                            try {
                                root.removeView(this@apply)
                            } catch (_: Throwable) {
                            }
                        },
                    )
                }
            }
        }
        root.addView(
            composeView,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            ),
        )
    }

    private fun vibrateSuccess(activity: Activity) {
        if (!cz.autokolk.ui.settings.AppSettingsStore.isHapticEnabled(activity)) return
        val v = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = activity.getSystemService(android.content.Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vm?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            activity.getSystemService(android.content.Context.VIBRATOR_SERVICE) as? Vibrator
        } ?: return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                v.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK))
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(60, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(60)
            }
        } catch (_: Throwable) {
        }
    }
}

@Composable
private fun AchievementUnlockContent(
    achievementName: String,
    valueLine: String,
    onDismiss: () -> Unit,
) {
    val main by rememberLottieComposition(LottieCompositionSpec.Asset("lottie/achievement_unlock.json"))
    val confetti by rememberLottieComposition(LottieCompositionSpec.Asset("lottie/confetti.json"))

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.72f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center,
    ) {
        LottieAnimation(
            composition = confetti,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier.fillMaxSize(),
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
            LottieAnimation(
                composition = main,
                iterations = 1,
                modifier = Modifier.size(200.dp),
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "Úspěch!",
                style = MaterialTheme.typography.headlineMedium,
                color = AccentCyan,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                achievementName,
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                valueLine,
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(24.dp))
            Text(
                "Klepnutím zavřít",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary,
            )
        }
    }
}
