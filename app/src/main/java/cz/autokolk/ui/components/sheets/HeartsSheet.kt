package cz.autokolk.ui.components.sheets

import android.app.Activity
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cz.autokolk.LessonProgress
import cz.autokolk.audio.SoundManager
import cz.autokolk.ui.components.buttons.PrimaryGradientButton
import cz.autokolk.ui.theme.BottomSheetShape
import cz.autokolk.ui.theme.DarkSurfaceVariant
import cz.autokolk.ui.theme.ErrorRed
import cz.autokolk.ui.theme.TextSecondary
import cz.autokolk.ui.theme.TextTertiary
import kotlinx.coroutines.delay

private const val MAX_HEARTS = 15

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeartsSheet(
    isVisible: Boolean,
    lives: Int,
    lessonProgress: LessonProgress,
    onDismiss: () -> Unit,
    onLivesUpdated: () -> Unit,
) {
    if (!isVisible) return

    val activity = LocalContext.current as? Activity
    var adLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        SoundManager.play(SoundManager.Sound.WHOOSH, volume = 0.6f)
    }

    var nextHeartMs by remember { mutableLongStateOf(lessonProgress.millisUntilNextHeart()) }
    LaunchedEffect(lives) {
        while (true) {
            nextHeartMs = lessonProgress.millisUntilNextHeart()
            delay(1000)
        }
    }

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
            Text(
                text = "Životy",
                style = MaterialTheme.typography.titleLarge,
                color = ErrorRed,
            )
            Spacer(Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(),
            ) {
                repeat(MAX_HEARTS.coerceAtMost(5)) { index ->
                    val isFull = index < lives.coerceAtMost(5)
                    val scale by animateFloatAsState(
                        targetValue = if (isFull) 1f else 0.7f,
                        animationSpec = spring(dampingRatio = 0.5f),
                        label = "heartScale_$index",
                    )
                    Icon(
                        imageVector = if (isFull) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = null,
                        tint = if (isFull) ErrorRed else TextTertiary,
                        modifier = Modifier
                            .size(32.dp)
                            .scale(scale)
                            .padding(horizontal = 2.dp),
                    )
                }
            }

            if (lives > 5) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "$lives / $MAX_HEARTS",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary,
                )
            }

            Spacer(Modifier.height(16.dp))

            if (nextHeartMs > 0) {
                val minutes = (nextHeartMs / 60_000).toInt()
                val seconds = ((nextHeartMs % 60_000) / 1000).toInt()
                Text(
                    text = "Další ❤\uFE0F za ${minutes}:${"%02d".format(seconds)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )
                Spacer(Modifier.height(16.dp))
            }

            if (lives < MAX_HEARTS && activity != null) {
                PrimaryGradientButton(
                    text = if (adLoading) "Načítání…" else "Získat život",
                    onClick = {
                        if (adLoading) return@PrimaryGradientButton
                        adLoading = true
                        RewardedAdHelper.showForHeart(activity, lessonProgress) {
                            adLoading = false
                            onLivesUpdated()
                        }
                    },
                    enabled = !adLoading,
                )
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
