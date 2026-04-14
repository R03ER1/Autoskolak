package cz.autokolk.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cz.autokolk.BuildConfig
import cz.autokolk.ui.navigation.Route
import cz.autokolk.ui.screens.onboarding.OnboardingPreferences
import cz.autokolk.ui.theme.TextPrimary
import cz.autokolk.ui.theme.TextSecondary
import cz.autokolk.ui.theme.ThemeMode
import cz.autokolk.ui.theme.readThemeMode
import cz.autokolk.ui.theme.writeThemeMode

@Composable
fun SettingsComposeScreen(navController: NavHostController) {
    val context = LocalContext.current
    var themeMode by remember { mutableStateOf(readThemeMode(context)) }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        Text("Nastavení", style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
        Spacer(Modifier.padding(8.dp))
        Text("Vzhled", color = TextSecondary, style = MaterialTheme.typography.labelMedium)
        RowSetting(
            title = "Tmavý režim",
            subtitle = "Aktuálně: ${themeMode.name}",
            checked = themeMode == ThemeMode.DARK,
            onCheckedChange = { dark ->
                themeMode = if (dark) ThemeMode.DARK else ThemeMode.LIGHT
                writeThemeMode(context, themeMode)
            },
        )
        Text(
            "Pro režim Systém použij přepínač v systému Android (plná podpora brzy).",
            color = TextSecondary,
            style = MaterialTheme.typography.bodySmall,
        )
        Spacer(Modifier.padding(12.dp))
        TextButton(onClick = { navController.navigate(Route.Achievements.route) }) {
            Text("Úspěchy")
        }
        TextButton(onClick = { navController.navigate(Route.Changelog.route) }) {
            Text("Historie změn")
        }
        TextButton(
            onClick = {
                OnboardingPreferences.clearCompletedFlagForReplay(context)
                navController.navigate(Route.Onboarding.route) {
                    launchSingleTop = true
                }
            },
        ) {
            Text("Znovu zobrazit onboarding")
        }
        Spacer(Modifier.padding(24.dp))
        Text(
            "Verze ${BuildConfig.VERSION_NAME}",
            color = TextSecondary,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun RowSetting(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Column(Modifier.padding(vertical = 8.dp)) {
        Text(title, color = TextPrimary, style = MaterialTheme.typography.titleMedium)
        Text(subtitle, color = TextSecondary, style = MaterialTheme.typography.bodySmall)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
