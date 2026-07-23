package cz.autokolk.ui.screens.alex

import android.graphics.BitmapFactory
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import cz.autokolk.R
import cz.autokolk.audio.SoundManager
import cz.autokolk.ui.components.animation.FloatingReward
import cz.autokolk.ui.components.progress.AnimatedProgressBar
import cz.autokolk.ui.navigation.Route
import cz.autokolk.ui.theme.AccentCyan
import cz.autokolk.ui.theme.AccentTeal
import cz.autokolk.ui.theme.ErrorRed
import cz.autokolk.ui.theme.SuccessGreen
import cz.autokolk.ui.theme.WarningAmber
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AlexScreen(
    navController: NavHostController,
    viewModel: AlexViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    DisposableEffect(lifecycleOwner) {
        val obs = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshState()
            }
        }
        lifecycleOwner.lifecycle.addObserver(obs)
        onDispose { lifecycleOwner.lifecycle.removeObserver(obs) }
    }

    LaunchedEffect(state.hungerPercent) {
        if (state.hungerPercent <= 0) {
            navController.navigate(Route.AlexDeath.route) {
                launchSingleTop = true
            }
        }
    }

    LaunchedEffect(state.snackMessage) {
        val msg = state.snackMessage ?: return@LaunchedEffect
        android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_SHORT).show()
        viewModel.clearSnack()
    }

    LaunchedEffect(state.feedPhase) {
        if (state.feedPhase == AlexFeedAnimationPhase.Bouncing) {
            SoundManager.play(SoundManager.Sound.ALEX_FEED)
            SoundManager.play(SoundManager.Sound.COIN)
        }
    }

    var renameOpen by remember { mutableStateOf(false) }
    var renameDraft by remember { mutableStateOf(state.lionName) }

    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val moodTint = remember(state.mood, onSurfaceColor) {
        when (state.mood) {
            AlexMood.Happy -> Color.Transparent
            AlexMood.Neutral -> onSurfaceColor.copy(alpha = 0.08f)
            AlexMood.Hungry -> WarningAmber.copy(alpha = 0.08f)
            AlexMood.Starving -> ErrorRed.copy(alpha = 0.10f)
        }
    }
    val screenBg = MaterialTheme.colorScheme.background

    Box(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(screenBg, moodTint, screenBg),
                ),
            ),
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(16.dp))
            Text(
                state.lionName,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = {},
                        onLongClick = {
                            renameDraft = state.lionName
                            renameOpen = true
                        },
                    )
                    .padding(8.dp),
            )
            Text(
                state.title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(24.dp))
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(260.dp)) {
                if (state.partyBackgroundEnabled) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.22f),
                                        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.12f),
                                        Color.Transparent,
                                    ),
                                ),
                                shape = CircleShape,
                            ),
                    )
                }
                AlexCharacter(
                    mood = state.mood,
                    hasSunglassesVisual = state.hasSunglassesVisual,
                    showStreakCrown = state.showStreakCrown,
                    showHatVisual = state.showHatVisual,
                    showScarfVisual = state.showScarfVisual,
                    modifier = Modifier.size(250.dp),
                    bounceTrigger = state.bounceTrigger,
                    heartParticlesTrigger = state.heartParticlesTrigger,
                )
                FlyingFoodOverlay(
                    phase = state.feedPhase,
                    assetPath = state.feedFoodAssetPath,
                )
            }

            Spacer(Modifier.height(24.dp))
            HungerBar(percent = state.hungerPercent, isFrozen = state.isFrozen)

            Spacer(Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                AlexActionChip(
                    label = "Nakrmit",
                    iconRes = R.drawable.ic_food,
                    tint = AccentTeal,
                    onClick = { viewModel.openFoodMenu() },
                )
                AlexActionChip(
                    label = "Obchod",
                    iconRes = R.drawable.ic_shop,
                    tint = AccentCyan,
                    onClick = { viewModel.openShop() },
                )
            }
            Spacer(Modifier.height(32.dp))
        }

        FloatingReward(
            visible = state.lastSpendRewardCoins != null,
            amount = state.lastSpendRewardCoins ?: 0,
            textOverride = state.lastSpendRewardCoins?.let { "-$it bodů" },
            modifier = Modifier.align(Alignment.TopCenter),
            onDismiss = { viewModel.dismissFeedSpendPopup() },
        )

        if (state.showFoodMenu) {
            FoodMenuSheet(
                state = state,
                viewModel = viewModel,
                onDismiss = { viewModel.closeFoodMenu() },
            )
        }
        if (state.showShop) {
            ShopSheet(
                state = state,
                viewModel = viewModel,
                onDismiss = { viewModel.closeShop() },
                onOpenCentralShop = {
                    viewModel.closeShop()
                    navController.navigate(Route.CoinShop.route)
                },
            )
        }
    }

    if (renameOpen) {
        AlertDialog(
            onDismissRequest = { renameOpen = false },
            title = { Text("Přejmenovat lva", color = MaterialTheme.colorScheme.onSurface) },
            text = {
                Column {
                    Text(
                        "Tvůj lev se bude jmenovat: $renameDraft",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = renameDraft,
                        onValueChange = { renameDraft = it.take(24) },
                        singleLine = true,
                        label = { Text("Jméno (1–20 znaků, bez emoji)") },
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (AlexViewModel.isValidLionName(renameDraft)) {
                            viewModel.renameLion(renameDraft)
                            renameOpen = false
                        } else {
                            android.widget.Toast.makeText(
                                context,
                                "Neplatné jméno",
                                android.widget.Toast.LENGTH_SHORT,
                            ).show()
                        }
                    },
                ) { Text("Uložit") }
            },
            dismissButton = {
                TextButton(onClick = { renameOpen = false }) { Text("Zrušit") }
            },
        )
    }
}

@Composable
private fun FlyingFoodOverlay(
    phase: AlexFeedAnimationPhase,
    assetPath: String?,
) {
    val ctx = LocalContext.current
    val bmp = remember(assetPath, phase) {
        if (assetPath == null) return@remember null
        runCatching {
            ctx.assets.open(assetPath).use { BitmapFactory.decodeStream(it)?.asImageBitmap() }
        }.getOrNull()
    }
    val offsetY = remember { Animatable(120f) }
    LaunchedEffect(phase, assetPath) {
        if (phase == AlexFeedAnimationPhase.Flying && assetPath != null) {
            offsetY.snapTo(120f)
            offsetY.animateTo(0f, tween(320))
        } else {
            offsetY.snapTo(0f)
        }
    }
    if (phase == AlexFeedAnimationPhase.Flying && bmp != null) {
        androidx.compose.foundation.Image(
            bitmap = bmp,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(56.dp)
                .offset { IntOffset(0, offsetY.value.roundToInt()) },
        )
    }
}

@Composable
private fun AlexActionChip(
    label: String,
    iconRes: Int,
    tint: Color,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(22.dp),
        )
        Text(label, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun HungerBar(percent: Int, isFrozen: Boolean) {
    val gradientLead = when {
        percent > 60 -> SuccessGreen
        percent > 30 -> WarningAmber
        else -> ErrorRed
    }
    Column(Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("Sytost", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface)
            Text(
                "$percent%",
                style = MaterialTheme.typography.labelLarge,
                color = gradientLead,
            )
        }
        Spacer(Modifier.height(4.dp))
        AnimatedProgressBar(
            progress = percent / 100f,
            accent = gradientLead,
            height = 12.dp,
            modifier = Modifier.fillMaxWidth(),
        )
        if (isFrozen) {
            Text(
                "Hlad zmrazen",
                style = MaterialTheme.typography.labelMedium,
                color = AccentCyan,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}
