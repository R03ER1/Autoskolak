package cz.autokolk.ui.components.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import cz.autokolk.ui.components.animation.AnimatedCounter
import cz.autokolk.ui.components.buttons.PrimaryGradientButton
import cz.autokolk.ui.theme.AutokolkTokens
import cz.autokolk.ui.theme.BottomSheetShape
import cz.autokolk.ui.theme.DarkSurfaceVariant
import cz.autokolk.ui.theme.TextSecondary
import cz.autokolk.ui.theme.WarningAmber
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Shield

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreakSheet(
    isVisible: Boolean,
    streak: Int,
    streakHistory: List<Boolean>,
    onDismiss: () -> Unit,
    onWatchAd: () -> Unit,
    shouldShowProtection: Boolean = true,
) {
    if (!isVisible) return
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val compositionResult = rememberLottieComposition(
        LottieCompositionSpec.Asset("lottie/streak_fire.json"),
    )
    val composition = compositionResult.value

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
            StreakWeekRow(streakHistory = streakHistory)
            Spacer(Modifier.height(24.dp))
            if (shouldShowProtection) {
                PrimaryGradientButton(
                    text = "Ochránit streak",
                    onClick = onWatchAd,
                    icon = Icons.Outlined.Shield,
                )
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun StreakWeekRow(
    streakHistory: List<Boolean>,
    modifier: Modifier = Modifier,
) {
    val days = 7
    val last = streakHistory.takeLast(days)
    val pad = (days - last.size).coerceAtLeast(0)
    val padded = List(pad) { false } + last
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(days) { index ->
            val done = padded.getOrElse(index) { false }
            val isToday = index == days - 1
            val base = Modifier
                .size(if (isToday) 22.dp else 18.dp)
                .background(
                    color = if (done) WarningAmber.copy(alpha = 0.35f) else DarkSurfaceVariant,
                    shape = CircleShape,
                )
            val mod = if (isToday) {
                base.border(AutokolkTokens.GlassBorderWidth * 2, WarningAmber, CircleShape)
            } else {
                base
            }
            Box(modifier = mod)
        }
    }
}
