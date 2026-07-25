package cz.autokolk.ui.screens.results

import android.app.Activity
import android.net.Uri
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import cz.autokolk.InterstitialAdController
import cz.autokolk.LessonProgress
import cz.autokolk.LevelUpPending
import cz.autokolk.ui.components.feedback.LevelUpOverlay
import cz.autokolk.ui.components.feedback.StreakMilestoneOverlay
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
import cz.autokolk.ui.navigation.navigateToTab
import cz.autokolk.ui.theme.AccentCyan
import cz.autokolk.ui.theme.SuccessGreen
import cz.autokolk.ui.theme.WarningAmber
import cz.autokolk.ui.theme.accentCyanText
import cz.autokolk.ui.theme.glassPalette
import cz.autokolk.ui.theme.warningAmberText
import kotlinx.coroutines.delay

@Composable
fun ResultsComposeScreen(
    navController: NavHostController,
    lessonId: Int,
    score: Int,
    total: Int,
    firstOfDay: Boolean,
    pointsAwarded: Int,
    fromPractice: Boolean = false,
    replayCategoryEncoded: String = Route.Results.NO_REPLAY,
    replayPracticeMode: Int = 0,
    replaySubEncoded: String = Route.Results.NO_REPLAY,
    replayFocusEncoded: String = Route.Results.NO_REPLAY,
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val lessonProgress = remember { LessonProgress(context) }
    var levelUpPending by remember { mutableStateOf<LevelUpPending?>(null) }
    var streakMilestonePending by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    LaunchedEffect(Unit) {
        levelUpPending = lessonProgress.consumePendingLevelUp()
        if (levelUpPending == null) {
            streakMilestonePending = lessonProgress.consumePendingStreakCelebration()
        }
    }

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

    val headline = when {
        isTest -> "Výsledek testu"
        fromPractice -> "Procvičování dokončeno"
        passed -> "Výborně!"
        else -> "Zkus to znovu!"
    }

    val replayCategory = Uri.decode(replayCategoryEncoded)
    val replaySub = when (replaySubEncoded) {
        Route.Results.NO_REPLAY -> Route.PracticeQuiz.ALL_SUB
        else -> Uri.decode(replaySubEncoded)
    }
    val replayFocus = when (replayFocusEncoded) {
        Route.Results.NO_REPLAY -> Route.PracticeQuiz.FOCUS_NONE
        else -> Uri.decode(replayFocusEncoded)
    }
    val hasReplay = fromPractice && replayCategory != Route.Results.NO_REPLAY

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
                text = headline,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
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
                trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                gradient = listOf(if (passed) SuccessGreen else AccentCyan),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${(ringProgress.value * 100f).toInt().coerceIn(0, 100)}",
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text("%", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(Modifier.height(28.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                StatChip(label = "Správně", value = "$displayScore/$total", color = SuccessGreen)
                StatChip(label = "Body", value = "+$displayPoints", color = warningAmberText())
            }
            Spacer(Modifier.height(28.dp))
            if (firstOfDay) {
                PrimaryGradientButton(
                    text = "Pokračovat",
                    onClick = { navController.navigate(Route.Streak.route) },
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
                val primaryLabel = if (fromPractice) "Zpět na procvičování" else "Zpět na cestu"
                // Interstitial ad se pokusí zobrazit jen při běžném ukončení lekce
                // (ne test / procvičování / firstOfDay-streak flow). Controller sám
                // rozhodne podle počítadla; když se reklama nezobrazí, navigace proběhne
                // synchronně.
                val gateAdOnCta = !fromPractice && !isTest && lessonId > 0
                PrimaryGradientButton(
                    text = primaryLabel,
                    onClick = {
                        val proceed: () -> Unit = {
                            if (fromPractice) {
                                navController.navigateToTab(Route.Practice)
                            } else {
                                navController.navigate(Route.Home.route) {
                                    popUpTo(Route.Home.route) { inclusive = true }
                                }
                            }
                        }
                        if (gateAdOnCta) {
                            InterstitialAdController.maybeShowInterstitial(activity) { proceed() }
                        } else {
                            proceed()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Spacer(Modifier.height(12.dp))
            if (hasReplay) {
                TextButton(
                    onClick = {
                        navController.navigate(
                            Route.PracticeQuiz(
                                categoryKey = replayCategory,
                                practiceMode = replayPracticeMode,
                                subcategoryKey = replaySub,
                                focusQuestionId = replayFocus,
                            ).buildPath(),
                        ) {
                            popUpTo(Route.Practice.route) { inclusive = false }
                        }
                    },
                ) {
                    Text("Zkusit znovu", color = accentCyanText())
                }
            } else if (!isTest && lessonId > 0) {
                TextButton(
                    onClick = {
                        navController.navigate(
                            Route.Quiz(lessonId = lessonId, isTest = false, categoryId = -1, isReview = false).buildPath(),
                        ) {
                            popUpTo(Route.Home.route) { inclusive = false }
                        }
                    },
                ) {
                    Text("Zkusit znovu", color = accentCyanText())
                }
            }
        }
        levelUpPending?.let { pending ->
            LevelUpOverlay(
                pending = pending,
                onDismiss = {
                    levelUpPending = null
                    streakMilestonePending = lessonProgress.consumePendingStreakCelebration()
                },
                modifier = Modifier.fillMaxSize(),
            )
        }
        streakMilestonePending?.let { (days, bonusCoins) ->
            StreakMilestoneOverlay(
                days = days,
                bonusCoins = bonusCoins,
                onDismiss = { streakMilestonePending = null },
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun StatChip(label: String, value: String, color: Color) {
    Surface(
        color = glassPalette().fillStart,
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
