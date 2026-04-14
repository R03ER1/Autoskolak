package cz.autokolk.ui.components.sheets

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import cz.autokolk.ui.components.buttons.PrimaryGradientButton
import cz.autokolk.ui.theme.BottomSheetShape
import cz.autokolk.ui.theme.DarkSurfaceVariant
import cz.autokolk.ui.theme.ErrorRed
import cz.autokolk.ui.theme.TextSecondary
import cz.autokolk.ui.theme.TextTertiary
import androidx.compose.material.icons.filled.PlayCircle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeartsSheet(
    isVisible: Boolean,
    lives: Int,
    maxLives: Int,
    nextHeartIn: kotlin.time.Duration?,
    onDismiss: () -> Unit,
    onWatchAd: () -> Unit,
) {
    if (!isVisible) return
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkSurfaceVariant,
        shape = BottomSheetShape,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Životy",
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(maxLives) { index ->
                    val isFull = index < lives
                    val scale by animateFloatAsState(
                        targetValue = if (isFull) 1f else 0.7f,
                        animationSpec = spring(dampingRatio = 0.6f),
                        label = "heartScale",
                    )
                    Icon(
                        imageVector = if (isFull) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = null,
                        tint = if (isFull) ErrorRed else TextTertiary,
                        modifier = Modifier
                            .size(32.dp)
                            .scale(scale),
                    )
                }
            }
            nextHeartIn?.let { d ->
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Další ❤️ za ${formatDuration(d)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )
            }
            if (lives < maxLives) {
                Spacer(Modifier.height(24.dp))
                PrimaryGradientButton(
                    text = "Získat život",
                    onClick = onWatchAd,
                    icon = Icons.Filled.PlayCircle,
                )
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

private fun formatDuration(d: kotlin.time.Duration): String {
    val totalSeconds = d.inWholeSeconds
    val m = totalSeconds / 60
    val s = totalSeconds % 60
    return "${m}m ${s.toString().padStart(2, '0')}s"
}
