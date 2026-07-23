package cz.autokolk.ui.components.sheets

import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cz.autokolk.LessonProgress
import cz.autokolk.R
import cz.autokolk.audio.SoundManager
import cz.autokolk.ui.components.buttons.PrimaryGradientButton
import cz.autokolk.ui.components.animation.AnimatedCounter
import cz.autokolk.ui.theme.AccentCyan
import cz.autokolk.ui.theme.BottomSheetShape
import cz.autokolk.ui.theme.DarkSurfaceVariant
import cz.autokolk.ui.theme.ErrorRed
import cz.autokolk.ui.theme.SuccessGreen
import cz.autokolk.ui.theme.TextSecondary
import cz.autokolk.ui.theme.WarningAmber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinsSheet(
    isVisible: Boolean,
    totalCoins: Int,
    onDismiss: () -> Unit,
    lessonProgress: LessonProgress? = null,
    onStatsRefresh: () -> Unit = {},
) {
    if (!isVisible) return

    val context = LocalContext.current
    val activity = context as? Activity
    var bonusMsg by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        SoundManager.play(SoundManager.Sound.WHOOSH, volume = 0.6f)
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
            Icon(
                painter = painterResource(R.drawable.ic_coin),
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = WarningAmber,
            )
            Spacer(Modifier.height(8.dp))
            AnimatedCounter(
                targetValue = totalCoins,
                style = MaterialTheme.typography.displayMedium,
                color = WarningAmber,
            )
            Text(
                text = "bodů",
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondary,
            )

            Spacer(Modifier.height(24.dp))
            HorizontalDivider(color = TextSecondary.copy(alpha = 0.2f))
            Spacer(Modifier.height(16.dp))

            Text(
                text = "Jak získat body",
                style = MaterialTheme.typography.titleSmall,
                color = TextSecondary,
            )
            Spacer(Modifier.height(12.dp))

            CoinInfoRow("+10", "Dokončit lekci", SuccessGreen)
            CoinInfoRow("+20", "Bezchybná lekce", AccentCyan)
            CoinInfoRow("+5", "Denní streak", WarningAmber)
            CoinInfoRow("-5", "Nakrmit Alexe", ErrorRed)

            Spacer(Modifier.height(16.dp))

            if (lessonProgress != null && activity != null) {
                PrimaryGradientButton(
                    text = "2× XP na 30 min (reklama)",
                    onClick = {
                        bonusMsg = null
                        RewardedAdHelper.showForDoubleXp(activity, lessonProgress) { ok ->
                            bonusMsg = if (ok) "2× XP je aktivní!" else "Reklamu se nepodařilo přehrát."
                            if (ok) onStatsRefresh()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
                bonusMsg?.let {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun CoinInfoRow(
    points: String,
    description: String,
    color: androidx.compose.ui.graphics.Color,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = points,
            style = MaterialTheme.typography.titleMedium,
            color = color,
            modifier = Modifier.width(48.dp),
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
        )
    }
}
