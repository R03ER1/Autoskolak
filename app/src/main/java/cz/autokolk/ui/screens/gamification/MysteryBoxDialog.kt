package cz.autokolk.ui.screens.gamification

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.view.HapticFeedbackConstantsCompat
import cz.autokolk.LessonProgress
import cz.autokolk.audio.SoundManager
import cz.autokolk.ui.components.buttons.PrimaryGradientButton
import cz.autokolk.ui.util.rememberHaptic
import cz.autokolk.ui.theme.AccentCyan
import cz.autokolk.ui.theme.DarkSurface
import cz.autokolk.ui.theme.TextPrimary
import cz.autokolk.ui.theme.TextSecondary
import cz.autokolk.ui.theme.WarningAmber
import kotlinx.coroutines.launch

@Composable
fun MysteryBoxDialog(
    lessonProgress: LessonProgress,
    onDismiss: () -> Unit,
) {
    val view = LocalView.current
    val haptic = rememberHaptic()
    val scope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }
    var phase by remember { mutableStateOf(BoxPhase.Idle) }
    var resultCoins by remember { mutableStateOf<Int?>(null) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var remaining by remember { mutableIntStateOf(lessonProgress.getMysteryBoxOpensRemainingToday()) }
    val bonusXp = remember { lessonProgress.getMysteryBoxBonusXp() }

    LaunchedEffect(Unit) {
        remaining = lessonProgress.getMysteryBoxOpensRemainingToday()
    }

    Dialog(onDismissRequest = {
        if (phase != BoxPhase.Opening) onDismiss()
    }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.large)
                .background(DarkSurface)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                "Mystery box",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Dnes zbývá $remaining/2 otevření",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
            )
            Spacer(Modifier.height(20.dp))
            Box(
                Modifier
                    .size(140.dp)
                    .scale(scale.value)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(WarningAmber.copy(alpha = 0.9f), AccentCyan.copy(alpha = 0.5f)),
                        ),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    when {
                        resultCoins != null -> "✓"
                        errorMsg != null -> "…"
                        else -> "?"
                    },
                    style = MaterialTheme.typography.displayLarge,
                    color = TextPrimary,
                )
            }
            Spacer(Modifier.height(16.dp))
            errorMsg?.let {
                Text(
                    it,
                    color = AccentCyan,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(8.dp))
            }
            resultCoins?.let { coins ->
                Text(
                    "+$coins mincí",
                    style = MaterialTheme.typography.headlineSmall,
                    color = WarningAmber,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    "+$bonusXp XP",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextSecondary,
                )
                Spacer(Modifier.height(12.dp))
            }
            when {
                resultCoins != null || (errorMsg != null && phase != BoxPhase.Opening) -> {
                    PrimaryGradientButton(
                        text = "Zavřít",
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                else -> {
                    PrimaryGradientButton(
                        text = when (phase) {
                            BoxPhase.Idle -> "Otevřít"
                            BoxPhase.Opening -> "Otevírám…"
                            BoxPhase.Done -> "Hotovo"
                        },
                        onClick = {
                            when (phase) {
                                BoxPhase.Opening -> return@PrimaryGradientButton
                                BoxPhase.Done -> return@PrimaryGradientButton
                                BoxPhase.Idle -> {
                                    if (lessonProgress.getMysteryBoxOpensRemainingToday() <= 0) {
                                        errorMsg = "Dnes už nemáš žádný box."
                                        return@PrimaryGradientButton
                                    }
                                    errorMsg = null
                                    phase = BoxPhase.Opening
                                    scope.launch {
                                        scale.animateTo(1.12f, tween(180, easing = FastOutSlowInEasing))
                                        scale.animateTo(0.95f, tween(120, easing = FastOutSlowInEasing))
                                        scale.animateTo(1.08f, tween(140, easing = FastOutSlowInEasing))
                                        scale.animateTo(1f, tween(160, easing = FastOutSlowInEasing))
                                        val coins = lessonProgress.openMysteryBox()
                                        phase = BoxPhase.Done
                                        remaining = lessonProgress.getMysteryBoxOpensRemainingToday()
                                        if (coins <= 0) {
                                            errorMsg = "Dnes už nemáš žádný box."
                                        } else {
                                            resultCoins = coins
                                            haptic.onAchievement()
                                            try {
                                                view.performHapticFeedback(HapticFeedbackConstantsCompat.CONTEXT_CLICK)
                                            } catch (_: Throwable) {
                                            }
                                            SoundManager.play(SoundManager.Sound.WHEEL_WIN)
                                            SoundManager.play(SoundManager.Sound.COIN, volume = 0.7f)
                                        }
                                    }
                                }
                            }
                        },
                        enabled = phase == BoxPhase.Idle && remaining > 0,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

private enum class BoxPhase {
    Idle,
    Opening,
    Done,
}
