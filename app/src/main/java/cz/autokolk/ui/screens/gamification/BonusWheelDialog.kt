package cz.autokolk.ui.screens.gamification

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.view.HapticFeedbackConstantsCompat
import cz.autokolk.LessonProgress
import cz.autokolk.audio.SoundManager
import cz.autokolk.ui.components.buttons.PrimaryGradientButton
import cz.autokolk.ui.theme.AccentBlue
import cz.autokolk.ui.theme.AccentCyan
import cz.autokolk.ui.theme.AccentTeal
import cz.autokolk.ui.theme.DarkSurface
import cz.autokolk.ui.theme.TextPrimary
import cz.autokolk.ui.theme.TextSecondary
import cz.autokolk.ui.theme.WarningAmber
import cz.autokolk.ui.util.rememberHaptic
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun BonusWheelDialog(
    lessonProgress: LessonProgress,
    onDismiss: () -> Unit,
) {
    val view = LocalView.current
    val haptic = rememberHaptic()
    val scope = rememberCoroutineScope()
    val rotation = remember { Animatable(0f) }
    var isSpinning by remember { mutableStateOf(false) }
    var resultCoins by remember { mutableStateOf<Int?>(null) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var remaining by remember { mutableIntStateOf(lessonProgress.getBonusWheelRollsRemainingToday()) }

    LaunchedEffect(Unit) {
        remaining = lessonProgress.getBonusWheelRollsRemainingToday()
    }

    Dialog(onDismissRequest = { if (!isSpinning) onDismiss() }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.large)
                .background(DarkSurface)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                "Bonusové kolo",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Dnes zbývá $remaining/3 točení",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "▼",
                color = WarningAmber,
                style = MaterialTheme.typography.headlineSmall,
            )
            Spacer(Modifier.height(4.dp))
            WheelGraphic(rotationDegrees = rotation.value)
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
                    style = MaterialTheme.typography.headlineMedium,
                    color = WarningAmber,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(12.dp))
            }
            when {
                resultCoins != null || (errorMsg != null && !isSpinning) -> {
                    PrimaryGradientButton(
                        text = "Zavřít",
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                else -> {
                    PrimaryGradientButton(
                        text = if (isSpinning) "Točí se…" else "Zatočit!",
                        onClick = {
                            if (isSpinning) return@PrimaryGradientButton
                            if (lessonProgress.getBonusWheelRollsRemainingToday() <= 0) {
                                errorMsg = "Dnes už nemáš žádné točení."
                                return@PrimaryGradientButton
                            }
                            errorMsg = null
                            isSpinning = true
                            scope.launch {
                                val extra = Random.nextFloat() * 360f
                                val animJob = launch {
                                    rotation.animateTo(
                                        rotation.value + 360f * 5 + extra,
                                        animationSpec = tween(2800, easing = FastOutSlowInEasing),
                                    )
                                }
                                // Tikot podle segmentů — zpomalování s časem imituje realistické kolo.
                                launch {
                                    var interval = 60L
                                    while (animJob.isActive) {
                                        SoundManager.play(SoundManager.Sound.WHEEL_TICK, volume = 0.7f)
                                        delay(interval)
                                        interval = (interval + 15L).coerceAtMost(260L)
                                    }
                                }
                                animJob.join()
                                val coins = lessonProgress.rollBonusWheel()
                                isSpinning = false
                                remaining = lessonProgress.getBonusWheelRollsRemainingToday()
                                if (coins <= 0) {
                                    errorMsg = "Dnes už nemáš žádné točení."
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
                        },
                        enabled = !isSpinning && remaining > 0,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
private fun WheelGraphic(rotationDegrees: Float) {
    val segmentColors = listOf(
        AccentCyan,
        AccentTeal,
        AccentBlue,
        WarningAmber,
        AccentCyan.copy(alpha = 0.85f),
        AccentTeal.copy(alpha = 0.85f),
    )
    val n = segmentColors.size
    Box(
        Modifier
            .size(220.dp)
            .graphicsLayer { rotationZ = rotationDegrees }
            .clip(CircleShape)
            .background(Brush.radialGradient(listOf(Color(0xFF12162A), DarkSurface))),
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val sweep = 360f / n
            for (i in 0 until n) {
                drawArc(
                    color = segmentColors[i],
                    startAngle = i * sweep - 90f,
                    sweepAngle = sweep,
                    useCenter = true,
                )
            }
        }
        Box(
            Modifier
                .align(Alignment.Center)
                .size(48.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(DarkSurface, AccentCyan.copy(alpha = 0.35f)))),
        )
    }
}
