package cz.autokolk.ui.screens.alex

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cz.autokolk.HungerManager
import cz.autokolk.ui.components.buttons.PrimaryGradientButton
import cz.autokolk.ui.components.media.AssetImageFromPath
import cz.autokolk.ui.navigation.Route
import cz.autokolk.ui.theme.TextPrimary
import cz.autokolk.ui.theme.TextSecondary

@Composable
fun AlexDeathScreen(navController: NavHostController) {
    val context = LocalContext.current
    val lionName = context.getSharedPreferences("lesson_progress", android.content.Context.MODE_PRIVATE)
        .getString("lion_name", "Alex") ?: "Alex"

    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            "⚠️ ${lionName.uppercase()} VYHLADOVĚL",
            style = MaterialTheme.typography.headlineSmall,
            color = TextPrimary,
        )
        Spacer(Modifier.padding(16.dp))
        AssetImageFromPath(
            assetPath = "images/alex/AlexDead.png",
            contentDescription = null,
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth(),
        )
        Spacer(Modifier.padding(16.dp))
        Text(
            "Podrž tlačítko v původní verzi nebo oživ rychle zde.",
            color = TextSecondary,
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(Modifier.padding(24.dp))
        PrimaryGradientButton(
            text = "Oživit (50 % hladu)",
            onClick = {
                HungerManager(context).setCurrentHunger(50)
                navController.navigate(Route.Alex.route) {
                    popUpTo(Route.Alex.route) { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
