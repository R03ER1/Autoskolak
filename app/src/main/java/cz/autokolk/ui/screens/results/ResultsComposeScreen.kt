package cz.autokolk.ui.screens.results

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import cz.autokolk.ui.components.animation.ConfettiOverlay
import cz.autokolk.ui.components.buttons.PrimaryGradientButton
import cz.autokolk.ui.components.progress.RingProgress
import cz.autokolk.ui.navigation.Route
import cz.autokolk.ui.theme.AccentCyan
import cz.autokolk.ui.theme.GlassFill
import cz.autokolk.ui.theme.SuccessGreen
import cz.autokolk.ui.theme.TextPrimary
import cz.autokolk.ui.theme.TextSecondary
import cz.autokolk.ui.theme.WarningAmber
import kotlinx.coroutines.delay

@Composable
fun ResultsComposeScreen(
    navController: NavHostController,
    lessonId: Int,
    score: Int,
    total: Int,
    firstOfDay: Boolean,
    pointsAwarded: Int,
) {
    val isTest = lessonId < 0
    val percentage = if (total > 0) (score * 100 / total) else 0
    val passed = percentage >= 80
    val lottieAsset = if (passed) "lottie/correct_answer.json" else "lottie/wrong_answer.json"
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset(lottieAsset))

    val ringProgress = remember { Animatable(0f) }
    var displayScore by remember { mutableIntStateOf(0) }
    var displayPoints by remember { mutableIntStateOf(0) }

    LaunchedEffect(percentage, score, pointsAwarded) {
        ringProgress.snapTo(0f)
        displayScore = 0
        displayPoints = 0
        delay(200)
        ringProgress.animateTo((percentage / 100f).coerceIn(0f, 1f), tween(1500))
        delay(200)
        countUp(score) { displayScore = it }
        delay(200)
        countUp(pointsAwarded) { displayPoints = it }
    }

    Box(Modifier.fillMaxSize().systemBarsPadding()) {
        ConfettiOverlay(isActive = percentage == 100 && total > 0, modifier = Modifier.fillMaxSize())
        Column(
            Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = if (isTest) "Výsledek testu" else if (passed) "Výborně!" else "Zkus to znovu!",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
            )
            Spacer(Modifier.height(16.dp))
            LottieAnimation(
                composition = composition,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier.size(140.dp),
            )
            Spacer(Modifier.height(20.dp))
            RingProgress(
                progress = ringProgress.value,
                size = 132.dp,
                strokeWidth = 10.dp,
                trackColor = GlassFill.copy(alpha = 0.35f),
                gradient = listOf(if (passed) SuccessGreen else AccentCyan),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${(ringProgress.value * 100f).toInt().coerceIn(0, 100)}",
                        style = MaterialTheme.typography.displaySmall,
                        color = TextPrimary,
                    )
                    Text("%", style = MaterialTheme.typography.titleMedium, color = TextSecondary)
                }
            }
            Spacer(Modifier.height(28.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                StatChip(label = "Správně", value = "$displayScore/$total", color = SuccessGreen)
                StatChip(label = "Body", value = "+$displayPoints", color = WarningAmber)
            }
            Spacer(Modifier.height(28.dp))
            if (firstOfDay) {
                PrimaryGradientButton(
                    text = "Pokračovat",
                    onClick = { navController.navigate(Route.Streak.route) },
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
                PrimaryGradientButton(
                    text = "Zpět na cestu",
                    onClick = {
                        navController.navigate(Route.Home.route) {
                            popUpTo(Route.Home.route) { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Spacer(Modifier.height(12.dp))
            if (!isTest && lessonId > 0) {
                TextButton(
                    onClick = {
                        navController.navigate(
                            Route.Quiz(lessonId = lessonId, isTest = false, categoryId = -1, isReview = false).buildPath(),
                        ) {
                            popUpTo(Route.Home.route) { inclusive = false }
                        }
                    },
                ) {
                    Text("Zkusit znovu", color = AccentCyan)
                }
            }
        }
    }
}

@Composable
private fun StatChip(label: String, value: String, color: Color) {
    Surface(
        color = GlassFill.copy(alpha = 0.25f),
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = TextSecondary)
            Text(value, style = MaterialTheme.typography.titleMedium, color = color, fontWeight = FontWeight.Bold)
        }
    }
}

private suspend fun countUp(target: Int, set: (Int) -> Unit) {
    if (target <= 0) {
        set(0)
        return
    }
    val steps = 16
    val stepMs = 900 / steps
    for (i in 1..steps) {
        set((target * i / steps).coerceAtLeast(0))
        delay(stepMs.toLong())
    }
    set(target)
}
