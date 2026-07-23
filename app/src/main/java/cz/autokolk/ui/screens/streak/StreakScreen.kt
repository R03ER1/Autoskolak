package cz.autokolk.ui.screens.streak

import android.app.Application
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import cz.autokolk.LessonProgress
import cz.autokolk.audio.SoundManager
import cz.autokolk.ui.components.animation.AnimatedBackground
import cz.autokolk.ui.components.animation.ConfettiOverlay
import cz.autokolk.ui.components.animation.AnimatedCounter
import cz.autokolk.ui.components.buttons.PrimaryGradientButton
import cz.autokolk.ui.navigation.Route
import cz.autokolk.ui.theme.TextSecondary
import cz.autokolk.ui.theme.WarningAmber
import cz.autokolk.ui.util.HapticFeedback

@Composable
fun StreakScreen(navController: NavHostController) {
    val context = LocalContext.current
    val app = context.applicationContext as Application
    val lessonProgress = remember { LessonProgress(app) }
    val streak = lessonProgress.getCurrentStreak()
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("lottie/streak_fire.json"))

    LaunchedEffect(Unit) {
        HapticFeedback.onMilestone(context)
        SoundManager.play(SoundManager.Sound.STREAK)
    }

    AnimatedBackground(modifier = Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize().systemBarsPadding()) {
            ConfettiOverlay(isActive = true, modifier = Modifier.fillMaxSize())
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(32.dp))
                LottieAnimation(
                    composition = composition,
                    iterations = LottieConstants.IterateForever,
                    modifier = Modifier.size(200.dp),
                )
                Spacer(Modifier.height(16.dp))
                AnimatedCounter(
                    targetValue = streak,
                    style = MaterialTheme.typography.displayLarge,
                    color = WarningAmber,
                )
                Text(
                    text = "dní v řadě! 🔥",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextSecondary,
                )
                Spacer(Modifier.height(40.dp))
                PrimaryGradientButton(
                    text = "Pokračovat",
                    onClick = {
                        val ok = navController.popBackStack(Route.Home.route, inclusive = false)
                        if (!ok) {
                            navController.navigate(Route.Home.route) {
                                popUpTo(Route.Home.route) { inclusive = true }
                            }
                        }
                    },
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        }
    }
}

