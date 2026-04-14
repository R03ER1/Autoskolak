package cz.autokolk.ui.screens.alex

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import cz.autokolk.LessonProgress
import cz.autokolk.ui.components.buttons.PrimaryGradientButton
import cz.autokolk.ui.components.media.AssetImageFromPath
import cz.autokolk.ui.components.progress.AnimatedProgressBar
import cz.autokolk.ui.navigation.Route
import cz.autokolk.ui.theme.TextPrimary
import cz.autokolk.ui.theme.TextSecondary

private fun alexImageAsset(hunger: Int, sunglasses: Boolean): String {
    val base = when {
        hunger <= 20 -> "AlexHungry.png"
        hunger <= 40 -> "AlexSad.png"
        hunger <= 60 -> "Alex.png"
        hunger <= 80 -> "AlexHappy.png"
        else -> "AlexCool.png"
    }
    val name = if (sunglasses) "C$base" else base
    return "images/alex/$name"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlexScreen(navController: NavHostController) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val vm: AlexViewModel = viewModel(factory = AlexViewModelFactory(application))
    val state by vm.state.collectAsStateWithLifecycle()
    val lessonProgress = remember { LessonProgress(context) }

    LaunchedEffect(state.hunger) {
        if (state.hunger <= 0) {
            navController.navigate(Route.AlexDeath.route) {
                launchSingleTop = true
            }
        }
    }

    var foodOpen by remember { mutableStateOf(false) }
    var shopOpen by remember { mutableStateOf(false) }
    var renameOpen by remember { mutableStateOf(false) }
    var renameText by remember { mutableStateOf(state.lionName) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        Text(
            text = hungerTitle(state.lionName, state.hunger),
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
        )
        state.frozenLabel?.let {
            Text(it, color = TextSecondary, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(Modifier.height(12.dp))
        val progress = state.hunger / 100f
        AnimatedProgressBar(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp),
        )
        Spacer(Modifier.height(16.dp))
        AssetImageFromPath(
            assetPath = alexImageAsset(state.hunger, lessonProgress.isSunglassesEnabled()),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
        )
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            PrimaryGradientButton(
                text = "Jídlo",
                onClick = { foodOpen = true },
                modifier = Modifier.weight(1f).padding(4.dp),
            )
            PrimaryGradientButton(
                text = "Obchod",
                onClick = { shopOpen = true },
                modifier = Modifier.weight(1f).padding(4.dp),
            )
        }
        Spacer(Modifier.height(8.dp))
        TextButton(
            onClick = {
                renameText = state.lionName
                renameOpen = true
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Přejmenovat lva")
        }
        state.snackMessage?.let { msg ->
            Column {
                Text(msg, color = TextSecondary, modifier = Modifier.padding(top = 8.dp))
                LaunchedEffect(msg) {
                    kotlinx.coroutines.delay(2500)
                    vm.clearSnack()
                }
            }
        }
    }

    if (foodOpen) {
        ModalBottomSheet(
            onDismissRequest = { foodOpen = false },
            sheetState = sheetState,
        ) {
            Column(Modifier.padding(16.dp)) {
                FoodRow("Klobása +1", "4 bodů") { vm.purchaseFood(1, 4, "klobaska", "Snězeno") }
                FoodRow("Kuře +10", "30 bodů") { vm.purchaseFood(10, 30, "kure", "Snězeno") }
                FoodRow("Zmrzlina +3", "10 bodů") { vm.purchaseFood(3, 10, "zmrzlina", "Snězeno") }
                FoodRow("Mrkev +5", "16 bodů") { vm.purchaseFood(5, 16, "mrkev", "Snězeno") }
                PrimaryGradientButton(
                    text = "Pivo — max hlad (150)",
                    onClick = { vm.purchaseMaxBeer() },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                )
                PrimaryGradientButton(
                    text = "Kamení — 48 h bez hladu (80)",
                    onClick = { vm.purchaseFreeze() },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }

    if (shopOpen) {
        ModalBottomSheet(onDismissRequest = { shopOpen = false }) {
            Column(Modifier.padding(16.dp)) {
                Text("Obchod", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                val cost = 400
                PrimaryGradientButton(
                    text = "Sluneční brýle ($cost)",
                    onClick = {
                        if (lessonProgress.buySunglassesIfAffordable(cost)) {
                            vm.refresh()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
                if (lessonProgress.hasSunglasses()) {
                    Text(
                        if (lessonProgress.isSunglassesEnabled()) "Brýle zapnuté" else "Brýle vypnuté",
                        color = TextSecondary,
                    )
                    TextButton(
                        onClick = {
                            lessonProgress.setSunglassesEnabled(!lessonProgress.isSunglassesEnabled())
                            vm.refresh()
                        },
                    ) {
                        Text("Přepnout brýle")
                    }
                }
            }
        }
    }

    if (renameOpen) {
        AlertDialog(
            onDismissRequest = { renameOpen = false },
            title = { Text("Jméno lva") },
            text = {
                OutlinedTextField(
                    value = renameText,
                    onValueChange = { renameText = it },
                    singleLine = true,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val cost = 50
                        if (lessonProgress.hasRenameUnlocked() || lessonProgress.buyRenameIfAffordable(cost)) {
                            vm.setLionName(renameText)
                            renameOpen = false
                        }
                    },
                ) {
                    Text("Uložit")
                }
            },
            dismissButton = {
                TextButton(onClick = { renameOpen = false }) {
                    Text("Zrušit")
                }
            },
        )
    }
}

@Composable
private fun FoodRow(label: String, price: String, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(label, color = TextPrimary)
            Text(price, color = TextSecondary, style = MaterialTheme.typography.bodySmall)
        }
        TextButton(onClick = onClick) {
            Text("Koupit")
        }
    }
}

private fun hungerTitle(lion: String, hunger: Int): String {
    val hungerLevel = (hunger / 10) * 10
    return when (hungerLevel) {
        0 -> "$lion je úplně vyhladovělý!"
        10 -> "$lion má obrovský hlad!"
        20 -> "$lion je velmi hladový!"
        30 -> "$lion má velký hlad!"
        40 -> "$lion má hlad!"
        50 -> "$lion je trochu hladový!"
        60 -> "$lion je v pořádku!"
        70 -> "$lion se cítí dobře!"
        80 -> "$lion je spokojený!"
        90 -> "$lion je velmi spokojený!"
        100 -> "$lion se velmi dobře napapal!"
        else -> "$lion je v pořádku!"
    }
}

private class AlexViewModelFactory(
    private val application: Application,
) : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        return AlexViewModel(application) as T
    }
}
