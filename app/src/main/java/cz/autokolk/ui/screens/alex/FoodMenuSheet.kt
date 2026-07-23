package cz.autokolk.ui.screens.alex

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cz.autokolk.audio.SoundManager
import cz.autokolk.ui.components.buttons.PrimaryGradientButton
import cz.autokolk.ui.components.glass.GlassCard
import cz.autokolk.ui.theme.SuccessGreen
import cz.autokolk.ui.theme.TextPrimary
import cz.autokolk.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodMenuSheet(
    state: AlexState,
    viewModel: AlexViewModel,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    LaunchedEffect(Unit) {
        SoundManager.play(SoundManager.Sound.WHOOSH, volume = 0.6f)
    }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        LazyColumn(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp),
        ) {
            item {
                Text(
                    "Nakrmit ${state.lionName}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 16.dp),
                )
            }
            items(state.foodItems, key = { it.achievementKey }) { food ->
                FoodItemRow(
                    food = food,
                    coins = state.coins,
                    onClick = { viewModel.feed(food) },
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun FoodItemRow(
    food: AlexFoodItem,
    coins: Int,
    onClick: () -> Unit,
) {
    val context = LocalContext.current
    val thumb = remember(food.assetImagePath) {
        runCatching {
            context.assets.open(food.assetImagePath).use { stream ->
                BitmapFactory.decodeStream(stream)?.asImageBitmap()
            }
        }.getOrNull()
    }
    val hungerLabel = when (food.kind) {
        AlexFeedKind.Delta -> "+${food.hungerDelta}% sytost"
        AlexFeedKind.FullMax -> "Plná sytost"
        AlexFeedKind.FreezeDecay -> "48 h bez úbytku hladu"
    }
    GlassCard(Modifier.fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (thumb != null) {
                Image(
                    bitmap = thumb,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                )
            } else {
                Spacer(Modifier.size(48.dp))
            }
            Spacer(Modifier.size(12.dp))
            Column(Modifier.weight(1f)) {
                Text(food.displayName, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                Text(
                    hungerLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = SuccessGreen,
                )
            }
            PrimaryGradientButton(
                text = "${food.priceCoins}",
                onClick = onClick,
                enabled = coins >= food.priceCoins,
                icon = Icons.Filled.MonetizationOn,
            )
        }
    }
}
