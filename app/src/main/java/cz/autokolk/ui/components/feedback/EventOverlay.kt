package cz.autokolk.ui.components.feedback

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cz.autokolk.RandomEventManager
import cz.autokolk.ui.components.animation.ConfettiOverlay
import cz.autokolk.ui.components.buttons.PrimaryGradientButton
import cz.autokolk.ui.components.glass.GlassCard
import cz.autokolk.ui.components.media.AssetImageFromPath
import cz.autokolk.ui.theme.GlassTone
import cz.autokolk.ui.theme.TextPrimary
import cz.autokolk.ui.theme.TextSecondary
import cz.autokolk.ui.theme.WarningAmber

@Composable
fun RandomEventOverlay(
    event: RandomEventManager.RandomEventPresentation?,
    onDismiss: () -> Unit,
) {
    AnimatedVisibility(
        visible = event != null,
        enter = fadeIn() + scaleIn(initialScale = 0.92f),
        exit = fadeOut() + scaleOut(targetScale = 0.92f),
    ) {
        val e = event ?: return@AnimatedVisibility
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.58f))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                ) { onDismiss() },
            contentAlignment = Alignment.Center,
        ) {
            GlassCard(
                // Pevný tmavý scrim za kartou — glass tón musí zůstat tmavý v obou režimech.
                tone = GlassTone.Dark,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth(0.92f)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                    ) { /* consume */ },
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    ConfettiOverlay(
                        isActive = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                    )
                    AssetImageFromPath(
                        assetPath = "images/alex/AlexCool.png",
                        contentDescription = null,
                        modifier = Modifier.size(96.dp),
                        contentScale = ContentScale.Fit,
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "Událost!",
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextPrimary,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = e.message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = e.valueLine,
                        style = MaterialTheme.typography.titleLarge,
                        color = WarningAmber,
                    )
                    Spacer(Modifier.height(20.dp))
                    PrimaryGradientButton(
                        text = "Super!",
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}
