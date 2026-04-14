package cz.autokolk.ui.screens.test

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cz.autokolk.ui.navigation.Route
import cz.autokolk.ui.theme.TextPrimary
import cz.autokolk.ui.theme.TextSecondary

@Composable
fun TestResultsDetailScreen(navController: NavHostController, testId: Int) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
    ) {
        Text("Detail pokusu", style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
        Text(
            "ID záznamu: $testId (podrobnosti z XML aktivity brzy).",
            color = TextSecondary,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 12.dp),
        )
        TextButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.padding(top = 16.dp),
        ) {
            Text("Zpět")
        }
        TextButton(onClick = { navController.navigate(Route.Test.route) }) {
            Text("Na hub testu")
        }
    }
}
