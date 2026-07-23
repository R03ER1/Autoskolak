package cz.autokolk.ui.components.badges

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cz.autokolk.ui.theme.WarningAmber
import kotlinx.coroutines.delay

/**
 * Vizuální odznak/milník na lesson path, zobrazený za zcela dokončenou sekci (krok 141).
 *
 * Pokud [justUnlocked] je true (sekce byla dokončena a odznak ještě nebyl uživateli
 * zobrazen), přehraje se malá "pop-in" animace (scale + lehký bounce). Zvuk a haptika
 * se řeší na místě volajícím ([cz.autokolk.ui.screens.home.HomeScreen]), aby se
 * nedublovala logika s existujícím achievement systémem.
 */
@Composable
fun SectionMilestoneBadge(
    sectionTitle: String,
    sectionColor: Color,
    justUnlocked: Boolean,
    modifier: Modifier = Modifier,
) {
    var appear by remember { mutableStateOf(!justUnlocked) }
    LaunchedEffect(Unit) {
        if (justUnlocked) {
            delay(90)
            appear = true
        }
    }
    val scaleAnim by animateFloatAsState(
        targetValue = if (appear) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow,
        ),
        label = "sectionBadgeScale",
    )

    Column(
        modifier = modifier.scale(scaleAnim),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            sectionColor.copy(alpha = 0.95f),
                            sectionColor.copy(alpha = 0.55f),
                        ),
                    ),
                    shape = CircleShape,
                )
                .border(3.dp, WarningAmber.copy(alpha = 0.9f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.EmojiEvents,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(42.dp),
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = "🏆 Sekce dokončena",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        Text(
            text = sectionTitle,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp),
        )
    }
}
