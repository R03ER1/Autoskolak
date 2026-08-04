package cz.autokolk.ui.screens.gamification

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Switch
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
import cz.autokolk.ui.components.glass.GlassCard
import cz.autokolk.ui.components.sheets.RewardedAdHelper
import cz.autokolk.ui.theme.GameVisualStyle

private const val PRICE_SUNGLASSES = 1000
private const val PRICE_HAT = 650
private const val PRICE_SCARF = 550
private const val PRICE_PARTY_BG = 450

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
    val coins = remember(shopRefresh, lp) { lp.getTotalPoints() }
    val activeVisual = remember(shopRefresh, lp) { lp.getActiveVisualStyle() }

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

    fun bump() {
        shopRefresh++
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
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zpět", tint = MaterialTheme.colorScheme.onSurface)
            }
            Text("Obchod a bonusy", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(4.dp))
            Text(
                "Tvoje mince: $coins",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.height(8.dp))
            snack?.let {
                Text(it, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(8.dp))
            }

            Text(
                "Motivy aplikace",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
            )
            Text(
                "Barvy, typografie a tvary karet v celé aplikaci (Compose). Jeden motiv je zdarma.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(8.dp))
            GameVisualStyle.entries.forEach { style ->
                VisualThemeShopCard(
                    style = style,
                    owned = lp.isVisualStyleOwned(style),
                    active = activeVisual == style,
                    coins = coins,
                    onBuy = {
                        if (lp.buyVisualStyleIfAffordable(style)) {
                            snack = "Motiv zakoupen — klepni na „Aktivovat“."
                            bump()
                        } else {
                            snack = "Nedostatek mincí."
                        }
                    },
                    onActivate = {
                        if (lp.setActiveVisualStyleIfOwned(style)) {
                            snack = "Motiv aktivní."
                            bump()
                        }
                    },
                )
                Spacer(Modifier.height(8.dp))
            }

            Text(
                "Doplňky pro Alexe",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 12.dp, bottom = 4.dp),
            )
            Text(
                "Ikony jsou zatím placeholdery — později nahradíš obrázky.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(8.dp))

            AlexAccessoryShopCard(
                title = "Sluneční brýle",
                detail = "Stávající vzhled Alexe.",
                price = PRICE_SUNGLASSES,
                owned = lp.hasSunglasses(),
                enabledVisual = lp.isSunglassesEnabled(),
                coins = coins,
                onBuy = {
                    if (lp.buySunglassesIfAffordable(PRICE_SUNGLASSES)) {
                        snack = "Brýle tvoje!"
                        bump()
                    } else snack = "Nedostatek mincí."
                },
                onToggle = { lp.setSunglassesEnabled(it); bump() },
            )
            Spacer(Modifier.height(8.dp))
            AlexAccessoryShopCard(
                title = "Čepice",
                detail = "Slot na čepici (placeholder 🧢).",
                price = PRICE_HAT,
                owned = lp.hasHat(),
                enabledVisual = lp.isHatEnabled(),
                coins = coins,
                onBuy = {
                    if (lp.buyHatIfAffordable(PRICE_HAT)) {
                        snack = "Čepice zakoupena."
                        bump()
                    } else snack = "Nedostatek mincí."
                },
                onToggle = { lp.setHatEnabled(it); bump() },
            )
            Spacer(Modifier.height(8.dp))
            AlexAccessoryShopCard(
                title = "Šála",
                detail = "Slot na šálu (placeholder 🧣).",
                price = PRICE_SCARF,
                owned = lp.hasScarf(),
                enabledVisual = lp.isScarfEnabled(),
                coins = coins,
                onBuy = {
                    if (lp.buyScarfIfAffordable(PRICE_SCARF)) {
                        snack = "Šála zakoupena."
                        bump()
                    } else snack = "Nedostatek mincí."
                },
                onToggle = { lp.setScarfEnabled(it); bump() },
            )
            Spacer(Modifier.height(8.dp))
            AlexAccessoryShopCard(
                title = "Párty pozadí",
                detail = "Jemný kruh za Alexem na jeho stránce.",
                price = PRICE_PARTY_BG,
                owned = lp.hasPartyBackground(),
                enabledVisual = lp.isPartyBackgroundEnabled(),
                coins = coins,
                onBuy = {
                    if (lp.buyPartyBackgroundIfAffordable(PRICE_PARTY_BG)) {
                        snack = "Pozadí zakoupeno."
                        bump()
                    } else snack = "Nedostatek mincí."
                },
                onToggle = { lp.setPartyBackgroundEnabled(it); bump() },
            )

            Spacer(Modifier.height(16.dp))
            Text(
                "Bonusy",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(8.dp))
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
                "Bonusové kolo — zbývá $wheelLeft točení",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun VisualThemeShopCard(
    style: GameVisualStyle,
    owned: Boolean,
    active: Boolean,
    coins: Int,
    onBuy: () -> Unit,
    onActivate: () -> Unit,
) {
    GlassCard(Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium) {
        Column(Modifier.padding(16.dp)) {
            Text(style.titleCs, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(4.dp))
            Text(style.subtitleCs, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(10.dp))
            when {
                active -> Text("Právě aktivní", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
                !owned -> {
                    val price = style.priceCoins
                    if (price == null) {
                        PrimaryGradientButton("Aktivovat (zdarma)", onClick = onActivate, modifier = Modifier.fillMaxWidth())
                    } else {
                        Text("$price mincí", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(6.dp))
                        PrimaryGradientButton(
                            text = "Koupit",
                            onClick = onBuy,
                            enabled = coins >= price,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
                else -> PrimaryGradientButton("Aktivovat", onClick = onActivate, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun AlexAccessoryShopCard(
    title: String,
    detail: String,
    price: Int,
    owned: Boolean,
    enabledVisual: Boolean,
    coins: Int,
    onBuy: () -> Unit,
    onToggle: (Boolean) -> Unit,
) {
    GlassCard(Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(4.dp))
            Text(detail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(10.dp))
            if (owned) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Zobrazení", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
                    Switch(checked = enabledVisual, onCheckedChange = onToggle)
                }
            } else {
                Text("$price mincí", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(8.dp))
                PrimaryGradientButton(
                    text = "Koupit",
                    onClick = onBuy,
                    enabled = coins >= price,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
