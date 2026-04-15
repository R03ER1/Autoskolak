package cz.autokolk.ui.screens.gamification

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import cz.autokolk.ui.components.animation.AnimatedBackground
import cz.autokolk.ui.components.buttons.PrimaryGradientButton
import cz.autokolk.ui.components.sheets.RewardedAdHelper
import cz.autokolk.ui.theme.TextPrimary
import cz.autokolk.ui.theme.TextSecondary

@Composable
fun CoinShopScreen(navController: NavHostController) {
    val context = LocalContext.current
    val activity = context as? Activity
    val lp = remember { LessonProgress(context) }
    var snack by remember { mutableStateOf<String?>(null) }
    var showWheelDialog by remember { mutableStateOf(false) }
    var showBoxDialog by remember { mutableStateOf(false) }
    var shopRefresh by remember { mutableIntStateOf(0) }
    val wheelLeft = remember(shopRefresh, lp) { lp.getBonusWheelRollsRemainingToday() }
    val boxLeft = remember(shopRefresh, lp) { lp.getMysteryBoxOpensRemainingToday() }

    if (showWheelDialog) {
        BonusWheelDialog(
            lessonProgress = lp,
            onDismiss = {
                showWheelDialog = false
                shopRefresh++
            },
        )
    }
    if (showBoxDialog) {
        MysteryBoxDialog(
            lessonProgress = lp,
            onDismiss = {
                showBoxDialog = false
                shopRefresh++
            },
        )
    }

    AnimatedBackground(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zpět", tint = TextPrimary)
            }
            Text("Obchod a bonusy", style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
            Spacer(Modifier.height(8.dp))
            snack?.let {
                Text(it, color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(8.dp))
            }
            PrimaryGradientButton(
                text = "2× XP na 30 min (reklama)",
                onClick = {
                    if (activity == null) return@PrimaryGradientButton
                    RewardedAdHelper.showForDoubleXp(activity, lp) { ok ->
                        snack = if (ok) "2× XP aktivní!" else "Reklamu se nepodařilo přehrát."
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(10.dp))
            Text(
                "Bonusové kolo — zbývá $wheelLeft/3",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                modifier = Modifier.align(Alignment.Start),
            )
            Spacer(Modifier.height(4.dp))
            PrimaryGradientButton(
                text = "Zatočit bonusovým kolem",
                onClick = { showWheelDialog = true },
                enabled = wheelLeft > 0,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(10.dp))
            Text(
                "Mystery box — zbývá $boxLeft/2",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                modifier = Modifier.align(Alignment.Start),
            )
            Spacer(Modifier.height(4.dp))
            PrimaryGradientButton(
                text = "Otevřít mystery box",
                onClick = { showBoxDialog = true },
                enabled = boxLeft > 0,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(10.dp))
            PrimaryGradientButton(
                text = "Sdílet streak",
                onClick = {
                    val send = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "Učím se v Autoškolákovi — streak ${lp.getCurrentStreak()} dní! 🚗",
                        )
                    }
                    context.startActivity(Intent.createChooser(send, "Sdílet"))
                },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(24.dp))
            Text(
                "Combo násobič: při 5+ správných v řadě dostaneš o 5 % více XP z lekce, při 10+ o 10 %.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
            )
        }
    }
}
