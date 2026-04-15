package cz.autokolk.ui.screens.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.ui.Alignment
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cz.autokolk.BuildConfig
import cz.autokolk.HungerManager
import cz.autokolk.LessonProgress
import cz.autokolk.AchievementsManager
import cz.autokolk.ui.components.animation.AnimatedBackground
import cz.autokolk.ui.components.glass.GlassCard
import cz.autokolk.ui.theme.AccentCyan
import cz.autokolk.ui.components.settings.ClickableSetting
import cz.autokolk.ui.components.settings.SettingsGroup
import cz.autokolk.ui.components.settings.SettingsProfileCard
import cz.autokolk.ui.components.settings.SwitchSetting
import cz.autokolk.ui.components.settings.ThemeModeSegmentedRow
import cz.autokolk.ui.navigation.Route
import cz.autokolk.ui.screens.onboarding.OnboardingPreferences
import cz.autokolk.ui.settings.AppSettingsStore
import cz.autokolk.ui.theme.LocalThemeController
import cz.autokolk.ui.theme.readThemeMode
import cz.autokolk.ui.theme.writeThemeMode

private val dailyGoalChoices = listOf(
    1 to "1 lekce (Pohoda)",
    3 to "3 lekce (Normální)",
    5 to "5 lekcí (Intenzivní)",
    10 to "10 lekcí (Šílený)",
)

@Composable
fun SettingsScreen(navController: NavHostController) {
    val context = LocalContext.current
    val themeController = LocalThemeController.current
    val themeMode = themeController?.mode ?: readThemeMode(context)
    val onboardingPrefs = remember { OnboardingPreferences(context) }

    var soundOn by remember { mutableStateOf(AppSettingsStore.isSoundEnabled(context)) }
    var hapticOn by remember { mutableStateOf(AppSettingsStore.isHapticEnabled(context)) }
    var biometricOn by remember { mutableStateOf(AppSettingsStore.isBiometricLockEnabled(context)) }

    var dailyGoal by remember { mutableIntStateOf(onboardingPrefs.dailyGoal) }
    var showDailyGoalDialog by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }

    val dailyGoalText = remember(dailyGoal) {
        dailyGoalChoices.find { it.first == dailyGoal }?.second ?: "denní cíl"
    }

    AnimatedBackground(Modifier.fillMaxSize()) {
        LazyColumn(
            Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp),
        ) {
            item {
                Text(
                    "Nastavení",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp),
                )
            }

            item {
                SettingsProfileCard(
                    lionName = onboardingPrefs.lionName,
                    dailyGoalLabel = dailyGoalText,
                )
            }

            item {
                SettingsGroup("Vzhled") {
                    GlassCardWithPadding {
                        ThemeModeSegmentedRow(
                            mode = themeMode,
                            onSelect = { m ->
                                themeController?.setMode(m) ?: writeThemeMode(context, m)
                            },
                        )
                    }
                    SwitchSetting(
                        title = "Zvuky",
                        checked = soundOn,
                        onToggle = {
                            soundOn = it
                            AppSettingsStore.setSoundEnabled(context, it)
                        },
                    )
                    SwitchSetting(
                        title = "Vibrace",
                        checked = hapticOn,
                        onToggle = {
                            hapticOn = it
                            AppSettingsStore.setHapticEnabled(context, it)
                        },
                    )
                }
            }

            item {
                SettingsGroup("Učení") {
                    SwitchSetting(
                        title = "Biometrický zámek",
                        checked = biometricOn,
                        onToggle = {
                            biometricOn = it
                            AppSettingsStore.setBiometricLockEnabled(context, it)
                        },
                    )
                    ClickableSetting(
                        title = "Denní cíl",
                        valueLabel = dailyGoal.toString(),
                        onClick = { showDailyGoalDialog = true },
                    )
                }
            }

            item {
                SettingsGroup("O aplikaci") {
                    ClickableSetting(
                        title = "Úspěchy",
                        onClick = { navController.navigate(Route.Achievements.route) },
                    )
                    ClickableSetting(
                        title = "Historie změn",
                        onClick = { navController.navigate(Route.Changelog.route) },
                    )
                    GlassCard(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(
                            Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("Verze", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                            Text(
                                BuildConfig.VERSION_NAME,
                                style = MaterialTheme.typography.bodyMedium,
                                color = AccentCyan,
                            )
                        }
                    }
                    ClickableSetting(
                        title = "Zásady ochrany soukromí",
                        subtitle = "Otevřít v prohlížeči",
                        onClick = {
                            try {
                                context.startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://sites.google.com/view/dos-pachos-studio/zásady-ochrany-soukromí"),
                                    ),
                                )
                            } catch (_: Throwable) {
                            }
                        },
                    )
                }
            }

            item {
                SettingsGroup("Nebezpečná zóna", isDanger = true) {
                    ClickableSetting(
                        title = "Smazat veškerý postup",
                        valueColor = MaterialTheme.colorScheme.error,
                        onClick = { showClearDialog = true },
                    )
                }
            }
        }
    }

    if (showDailyGoalDialog) {
        AlertDialog(
            onDismissRequest = { showDailyGoalDialog = false },
            title = { Text("Denní cíl") },
            text = {
                Column {
                    dailyGoalChoices.forEach { (value, label) ->
                        TextButton(
                            onClick = {
                                dailyGoal = value
                                onboardingPrefs.dailyGoal = value
                                showDailyGoalDialog = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(if (dailyGoal == value) "✓ $label" else label)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showDailyGoalDialog = false }) {
                    Text("Zrušit")
                }
            },
        )
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Smazat postup?") },
            text = {
                Text("Opravdu chcete smazat veškerý postup? Tuto akci nelze vrátit.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showClearDialog = false
                        LessonProgress(context).clearProgress()
                        HungerManager(context).freezeDecayUntil(0L)
                        AchievementsManager(context).clearAll()
                        context.getSharedPreferences("topic_intros", android.content.Context.MODE_PRIVATE)
                            .edit().clear().apply()
                        context.getSharedPreferences("tutorial_overlays", android.content.Context.MODE_PRIVATE)
                            .edit().clear().apply()
                        android.widget.Toast.makeText(context, "Vše vymazáno", android.widget.Toast.LENGTH_SHORT).show()
                    },
                ) {
                    Text("Smazat", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Zrušit")
                }
            },
        )
    }
}

@Composable
private fun GlassCardWithPadding(content: @Composable () -> Unit) {
    GlassCard(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(Modifier.padding(16.dp)) {
            content()
        }
    }
}
