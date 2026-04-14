package cz.autokolk.ui.screens.test

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cz.autokolk.LessonProgress
import cz.autokolk.ui.components.buttons.PrimaryGradientButton
import cz.autokolk.ui.navigation.Route
import cz.autokolk.ui.theme.TextPrimary
import cz.autokolk.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun TestHubScreen(navController: NavHostController) {
    val context = LocalContext.current
    val avg = remember { LessonProgress(context).getAverageTestScore(50) }
    var countdown by remember { mutableIntStateOf(0) }
    var armed by remember { mutableStateOf(false) }

    LaunchedEffect(armed) {
        if (!armed) return@LaunchedEffect
        for (i in 3 downTo 1) {
            countdown = i
            delay(1000)
        }
        navController.navigate(Route.Quiz(-1, true, -1, false).buildPath())
        armed = false
        countdown = 0
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Zkouška", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
        Spacer(Modifier.padding(8.dp))
        Text(
            "Průměr z pokusů: ${"%.1f".format(avg)} / 50",
            color = TextSecondary,
        )
        Spacer(Modifier.padding(24.dp))
        if (countdown > 0) {
            Text(
                "Start za $countdown…",
                style = MaterialTheme.typography.displaySmall,
                color = TextPrimary,
            )
        } else {
            PrimaryGradientButton(
                text = "Spustit test (45 min)",
                onClick = { armed = true },
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Spacer(Modifier.padding(16.dp))
        TextButton(onClick = { navController.navigate(Route.TestStats.route) }) {
            Text("Statistiky pokusů")
        }
    }
}
