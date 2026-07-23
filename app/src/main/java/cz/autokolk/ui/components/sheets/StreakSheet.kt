package cz.autokolk.ui.components.sheets

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import cz.autokolk.LessonProgress
import cz.autokolk.audio.SoundManager
import cz.autokolk.ui.components.animation.AnimatedCounter
import cz.autokolk.ui.components.buttons.PrimaryGradientButton
import cz.autokolk.ui.theme.AccentCyan
import cz.autokolk.ui.theme.BottomSheetShape
import cz.autokolk.ui.theme.DarkSurfaceVariant
import cz.autokolk.ui.theme.GlassWhite
import cz.autokolk.ui.theme.TextSecondary
import cz.autokolk.ui.theme.TextTertiary
import cz.autokolk.ui.theme.WarningAmber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreakSheet(
    isVisible: Boolean,
    streak: Int,
    streakHistory: List<Boolean>,
    lessonProgress: LessonProgress,
    onDismiss: () -> Unit,
    onStreakUpdated: () -> Unit,
    onRefreshStats: () -> Unit = {},
) {
    if (!isVisible) return

    LaunchedEffect(isVisible) {
        if (isVisible) {
            lessonProgress.consumeFrozenLabelForYesterday()
            SoundManager.play(SoundManager.Sound.WHOOSH, volume = 0.6f)
        }
    }

    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("lottie/streak_fire.json"),
    )
    val activity = LocalContext.current as? Activity
    var adLoading by remember { mutableStateOf(false) }
    var freezeMsg by remember { mutableStateOf<String?>(null) }
    val pendingFreeze = lessonProgress.hasPendingStreakFreeze()
    val coins = lessonProgress.getTotalPoints()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = DarkSurfaceVariant,
        shape = BottomSheetShape,
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LottieAnimation(
                composition = composition,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier.size(80.dp),
            )
            Spacer(Modifier.height(8.dp))
            AnimatedCounter(
                targetValue = streak,
                style = MaterialTheme.typography.displayLarge,
                color = WarningAmber,
            )
            Text(
                text = "dní v řadě!",
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondary,
            )
            Spacer(Modifier.height(24.dp))

            StreakWeekRow(
                history = streakHistory,
                frozenDayIndices = computeFrozenIndices(lessonProgress),
            )

            freezeMsg?.let {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(Modifier.height(24.dp))

            PrimaryGradientButton(
                text = when {
                    pendingFreeze -> "Zmrazení aktivní (příští vynechaný den)"
                    coins < 20 -> "Zmrazit streak (20 mincí) — nemáš dost mincí"
                    else -> "Zmrazit streak na zítřek (20 mincí)"
                },
                onClick = {
                    freezeMsg = null
                    if (pendingFreeze) return@PrimaryGradientButton
                    if (coins < 20) {
                        freezeMsg = "Potřebuješ 20 mincí."
                        return@PrimaryGradientButton
                    }
                    if (lessonProgress.buyStreakFreezeWithCoins()) {
                        freezeMsg = "Streak je chráněný při jednom vynechaném dni."
                        onRefreshStats()
                    } else {
                        freezeMsg = "Nepodařilo se zaplatit."
                    }
                },
                enabled = !pendingFreeze && coins >= 20,
            )

            Spacer(Modifier.height(16.dp))

            val shouldShowProtection = streak > 0
                && streakHistory.isNotEmpty()
                && !streakHistory[0]
            if (shouldShowProtection && activity != null) {
                PrimaryGradientButton(
                    text = if (adLoading) "Načítání…" else "Ochránit streak",
                    onClick = {
                        if (adLoading) return@PrimaryGradientButton
                        adLoading = true
                        RewardedAdHelper.showForStreakProtect(activity, lessonProgress) {
                            adLoading = false
                            onStreakUpdated()
                        }
                    },
                    enabled = !adLoading,
                )
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

private fun computeFrozenIndices(lessonProgress: LessonProgress): Set<Int> {
    if (!lessonProgress.wasYesterdayStreakFrozen()) return emptySet()
    // Historie: index 0 = dnes, 1 = včera
    return setOf(1)
}

@Composable
private fun StreakWeekRow(
    history: List<Boolean>,
    frozenDayIndices: Set<Int> = emptySet(),
) {
    val dayLabels = listOf("Dnes", "1", "2", "3", "4", "5", "6")

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        history.take(7).forEachIndexed { index, completed ->
            val isToday = index == 0
            val frozen = index in frozenDayIndices
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .then(
                            if (isToday) Modifier.border(2.dp, AccentCyan, CircleShape)
                            else Modifier
                        )
                        .background(
                            color = when {
                                frozen -> WarningAmber.copy(alpha = 0.85f)
                                completed -> AccentCyan
                                else -> TextTertiary.copy(alpha = 0.2f)
                            },
                            shape = CircleShape,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    when {
                        frozen -> Text("❄", color = DarkSurfaceVariant, style = MaterialTheme.typography.labelSmall)
                        completed -> Text("✓", color = DarkSurfaceVariant, style = MaterialTheme.typography.labelSmall)
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = dayLabels[index],
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isToday) AccentCyan else GlassWhite,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
