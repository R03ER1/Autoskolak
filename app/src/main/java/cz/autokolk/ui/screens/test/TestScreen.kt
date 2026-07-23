package cz.autokolk.ui.screens.test

import android.app.Application
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import cz.autokolk.data.test.TestAttemptRepository
import cz.autokolk.ui.components.animation.AnimatedBackground
import cz.autokolk.ui.components.buttons.PrimaryGradientButton
import cz.autokolk.ui.components.glass.GlassCard
import cz.autokolk.ui.navigation.Route
import cz.autokolk.ui.screens.quiz.TEST_QUESTION_COUNT

@Composable
fun TestScreen(navController: NavHostController) {
    val context = LocalContext.current
    val app = context.applicationContext as Application
    val repo = remember { TestAttemptRepository.getInstance(app) }
    var chartScores by remember { mutableStateOf<List<Int>>(emptyList()) }
    var stats by remember {
        mutableStateOf(
            cz.autokolk.data.test.TestStatsSnapshot(0, 0.0, 0.0),
        )
    }

    LaunchedEffect(Unit) {
        val desc = repo.getChartScoresDescending(60)
        chartScores = desc.asReversed()
        stats = repo.getStats()
    }

    val lottie by rememberLottieComposition(LottieCompositionSpec.Asset("lottie/onboarding_test.json"))

    AnimatedBackground(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            GlassCard(Modifier.fillMaxWidth()) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    LottieAnimation(
                        composition = lottie,
                        iterations = LottieConstants.IterateForever,
                        modifier = Modifier.size(120.dp),
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "Zkouška z teorie",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "$TEST_QUESTION_COUNT otázek • 30 minut • min. 43 bodů",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(20.dp))
                    PrimaryGradientButton(
                        text = "Spustit zkoušku",
                        onClick = {
                            navController.navigate(
                                Route.Quiz(
                                    lessonId = -1,
                                    isTest = true,
                                    categoryId = -1,
                                    isReview = false,
                                ).buildPath(),
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
            GlassCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    RowTitleStats(stats)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "Vývoj skóre",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(8.dp))
                    if (chartScores.isEmpty()) {
                        Text(
                            text = "Zatím žádné pokusy — spusť první zkoušku.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    } else {
                        ScoresChart(scores = chartScores, threshold = 43, maxPoints = 50)
                    }
                }
            }
            TextButton(onClick = { navController.navigate(Route.TestStats.route) }) {
                Text("Statistiky zkoušek", color = MaterialTheme.colorScheme.onSurface)
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun RowTitleStats(stats: cz.autokolk.data.test.TestStatsSnapshot) {
    Text("Tvoje výsledky", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
    Spacer(Modifier.height(8.dp))
    Text(
        text = "Pokusů: ${stats.attemptCount}   Průměr: ${"%.1f".format(stats.averageScore)} bodů   Úspěšnost: ${"%.0f".format(stats.passRatePercent)} %",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}
