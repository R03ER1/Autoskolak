package cz.autokolk.ui.screens.results

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cz.autokolk.ui.components.animation.AnimatedCounter
import cz.autokolk.ui.components.animation.ConfettiOverlay
import cz.autokolk.ui.navigation.Route
import cz.autokolk.ui.theme.TextPrimary
import cz.autokolk.ui.theme.TextSecondary

@Composable
fun ResultsComposeScreen(
    navController: NavHostController,
    lessonId: Int,
    score: Int,
    total: Int,
) {
    val perfect = total > 0 && score == total

    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = if (lessonId < 0) "Výsledek testu" else "Lekce dokončena",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
            )
            Spacer(Modifier.padding(8.dp))
            Text(
                text = "Správně",
                color = TextSecondary,
                style = MaterialTheme.typography.titleMedium,
            )
            AnimatedCounter(
                targetValue = score,
                modifier = Modifier.padding(vertical = 8.dp),
            )
            Text(
                text = "z $total",
                color = TextSecondary,
            )
            Spacer(Modifier.padding(24.dp))
            TextButton(
                onClick = {
                    navController.navigate(Route.Home.route) {
                        popUpTo(Route.Home.route) { inclusive = true }
                    }
                },
            ) {
                Text("Zpět na cestu")
            }
        }
        ConfettiOverlay(
            isActive = perfect,
            modifier = Modifier.fillMaxSize(),
        )
    }
}
