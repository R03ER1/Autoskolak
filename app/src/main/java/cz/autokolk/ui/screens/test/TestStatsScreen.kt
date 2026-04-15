package cz.autokolk.ui.screens.test

import android.app.Application
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cz.autokolk.data.test.TestAttemptRepository
import cz.autokolk.data.test.TestStatsSnapshot
import cz.autokolk.ui.components.animation.AnimatedBackground
import cz.autokolk.ui.components.glass.GlassCard
import cz.autokolk.ui.theme.TextPrimary
import cz.autokolk.ui.theme.TextSecondary

@Composable
fun TestStatsScreen(navController: NavHostController) {
    val context = LocalContext.current
    val app = context.applicationContext as Application
    val repo = remember { TestAttemptRepository.getInstance(app) }
    var stats by remember { mutableStateOf(TestStatsSnapshot(0, 0.0, 0.0)) }
    var chartScores by remember { mutableStateOf<List<Int>>(emptyList()) }

    LaunchedEffect(Unit) {
        stats = repo.getStats()
        chartScores = repo.getChartScoresDescending(80).asReversed()
    }

    AnimatedBackground(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            Text(
                text = "Statistiky zkoušek",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
            )
            Spacer(Modifier.height(16.dp))
            GlassCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp)) {
                    StatLine("Počet pokusů", "${stats.attemptCount}")
                    Spacer(Modifier.height(12.dp))
                    StatLine("Průměrné skóre", "${"%.1f".format(stats.averageScore)} / 50")
                    Spacer(Modifier.height(12.dp))
                    StatLine("Úspěšnost (≥43 bodů)", "${"%.0f".format(stats.passRatePercent)} %")
                }
            }
            Spacer(Modifier.height(20.dp))
            Text(
                text = "Graf pokusů v čase",
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondary,
            )
            Spacer(Modifier.height(8.dp))
            if (chartScores.isEmpty()) {
                Text(
                    text = "Zatím žádná data.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )
            } else {
                ScoresChart(scores = chartScores, threshold = 43, maxPoints = 50)
            }
            Spacer(Modifier.height(24.dp))
            TextButton(onClick = { navController.popBackStack() }) {
                Text("Zpět", color = TextPrimary)
            }
        }
    }
}

@Composable
private fun StatLine(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelMedium, color = TextSecondary)
        Text(value, style = MaterialTheme.typography.titleLarge, color = TextPrimary)
    }
}
