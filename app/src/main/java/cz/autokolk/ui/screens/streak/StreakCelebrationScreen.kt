package cz.autokolk.ui.screens.streak

import androidx.compose.foundation.layout.Arrangement
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
fun StreakCelebrationScreen(
    navController: NavHostController,
    streak: Int,
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            "Streak milník!",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
        )
        Spacer(Modifier.padding(8.dp))
        Text(
            "Počet dní v řadě",
            color = TextSecondary,
            style = MaterialTheme.typography.titleMedium,
        )
        AnimatedCounter(
            targetValue = streak,
            modifier = Modifier.padding(vertical = 16.dp),
        )
        TextButton(
            onClick = {
                navController.popBackStack()
            },
        ) {
            Text("Zpět")
        }
        TextButton(
            onClick = {
                navController.navigate(Route.Home.route) {
                    popUpTo(Route.Home.route) { inclusive = true }
                }
            },
        ) {
            Text("Domů")
        }
    }
    ConfettiOverlay(
        isActive = true,
        modifier = Modifier.fillMaxSize(),
    )
}
