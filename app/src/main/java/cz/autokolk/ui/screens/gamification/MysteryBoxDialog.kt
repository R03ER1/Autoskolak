package cz.autokolk.ui.screens.gamification

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.view.HapticFeedbackConstantsCompat
import cz.autokolk.LessonProgress
import cz.autokolk.R
import cz.autokolk.audio.SoundManager
import cz.autokolk.ui.components.buttons.PrimaryGradientButton
import cz.autokolk.ui.util.rememberHaptic
import cz.autokolk.ui.util.rememberLowPerformanceModeEnabled
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
    val lowPerformanceMode = rememberLowPerformanceModeEnabled()
    val scale = remember { Animatable(1f) }
    val shake = remember { Animatable(0f) }
    val glow = remember { Animatable(0f) }
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
            MysteryBoxGraphic(
                sizeDp = 140.dp,
                opened = resultCoins != null,
                glowAlpha = glow.value,
                modifier = Modifier
                    .scale(scale.value)
                    .rotate(shake.value),
            )
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
                                        if (lowPerformanceMode) {
                                            // Reduced motion / power save — bez "zatřesení" a spring
                                            // odskoku, jen krátké zvětšení a zpět (funkční část
                                            // animace, ne čistě dekorativní, proto zůstává).
                                            scale.animateTo(1.05f, tween(120, easing = FastOutSlowInEasing))
                                            scale.animateTo(1f, tween(120, easing = FastOutSlowInEasing))
                                        } else {
                                            // Truhla se před otevřením krátce "zatřese", jako by se
                                            // chystala vyskočit víko.
                                            shake.animateTo(-8f, tween(55, easing = FastOutSlowInEasing))
                                            shake.animateTo(8f, tween(85, easing = FastOutSlowInEasing))
                                            shake.animateTo(-6f, tween(75, easing = FastOutSlowInEasing))
                                            shake.animateTo(5f, tween(65, easing = FastOutSlowInEasing))
                                            shake.animateTo(0f, tween(60, easing = FastOutSlowInEasing))
                                        }
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
                                            if (lowPerformanceMode) {
                                                glow.snapTo(0.7f)
                                                glow.animateTo(0f, tween(200, easing = FastOutSlowInEasing))
                                            } else {
                                                // "Pop" odskok při zobrazení otevřené truhly (spring
                                                // s přestřelením) + krátký záblesk zlaté záře, obě
                                                // animace běží paralelně (samostatné korutiny).
                                                launch {
                                                    scale.snapTo(0.85f)
                                                    scale.animateTo(
                                                        1.15f,
                                                        spring(
                                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                                            stiffness = Spring.StiffnessMedium,
                                                        ),
                                                    )
                                                    scale.animateTo(
                                                        1f,
                                                        spring(
                                                            dampingRatio = Spring.DampingRatioLowBouncy,
                                                            stiffness = Spring.StiffnessLow,
                                                        ),
                                                    )
                                                }
                                                launch {
                                                    glow.snapTo(1f)
                                                    glow.animateTo(0f, tween(600, easing = FastOutSlowInEasing))
                                                }
                                            }
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

/**
 * Grafika truhly mystery boxu — sdílená mezi [MysteryBoxDialog] a [cz.autokolk.ui.components.buttons.MysteryBoxFab]
 * (analogicky k [WheelGraphic] pro bonusové kolo). Pozadí je STEJNÝ tmavý radiální gradient jako za
 * kolem v [WheelGraphic] (`Color(0xFF12162A)` → `Color(0xFF1A1F36)`) — konzistentní glassmorphism
 * "podklad" pro obě grafiky. Uprostřed se podle [opened] přepíná celá ikona mezi zavřenou
 * (`ic_chest_closed`) a otevřenou (`ic_chest_open`) truhlou — žádný malý odznak navíc, celý motiv
 * se "transformuje". [glowAlpha] (0f–1f) přidá krátký zlatý záblesk za truhlou v okamžiku otevření.
 */
@Composable
fun MysteryBoxGraphic(
    sizeDp: Dp,
    opened: Boolean,
    modifier: Modifier = Modifier,
    glowAlpha: Float = 0f,
) {
    Box(
        modifier
            .size(sizeDp)
            .clip(RoundedCornerShape(sizeDp * (16f / 140f)))
            .background(Brush.radialGradient(listOf(Color(0xFF12162A), Color(0xFF1A1F36)))),
        contentAlignment = Alignment.Center,
    ) {
        if (glowAlpha > 0f) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            listOf(WarningAmber.copy(alpha = 0.55f * glowAlpha), Color.Transparent),
                        ),
                    ),
            )
        }
        Icon(
            painter = painterResource(if (opened) R.drawable.ic_chest_open else R.drawable.ic_chest_closed),
            contentDescription = null,
            modifier = Modifier.size(sizeDp * 0.56f),
            tint = if (opened) WarningAmber else Color.White,
        )
    }
}
