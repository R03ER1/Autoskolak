package cz.autokolk.ui.components.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cz.autokolk.ui.screens.gamification.MysteryBoxGraphic
import cz.autokolk.ui.theme.AccentTeal
import cz.autokolk.ui.theme.DarkBackground
import cz.autokolk.ui.theme.DarkSurface
import cz.autokolk.ui.theme.DarkSurfaceVariant
import cz.autokolk.ui.theme.GlassWhite

/**
 * Plovoucí tlačítko rychlého přístupu k mystery boxu — zobrazuje se v levém dolním rohu
 * Home obrazovky (viz [cz.autokolk.ui.screens.home.HomeScreen], kontejner `HomeCornerFabColumn`),
 * nad [BonusWheelFab]. Vizuálně: mini verze [MysteryBoxGraphic] (stejná grafika dárkové krabičky
 * jako v dialogu, jen menší), badge s počtem zbývajících otevření dnes, a stín/elevation pro
 * vizuální "vyvýšení" nad pozadím — analogicky k [BonusWheelFab].
 */
@Composable
fun MysteryBoxFab(
    opensRemaining: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary

    Box(
        modifier = modifier.size(60.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .size(56.dp)
                .shadow(
                    elevation = 10.dp,
                    shape = CircleShape,
                    ambientColor = primary.copy(alpha = 0.45f),
                    spotColor = secondary.copy(alpha = 0.45f),
                )
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(DarkSurfaceVariant, DarkSurface)))
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(listOf(GlassWhite, Color.Transparent)),
                    shape = CircleShape,
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = false, radius = 28.dp),
                    role = Role.Button,
                    onClick = onClick,
                ),
            contentAlignment = Alignment.Center,
        ) {
            MysteryBoxGraphic(
                sizeDp = 38.dp,
                opened = false,
            )
        }
        if (opensRemaining > 0) {
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 2.dp, y = (-2).dp)
                    .size(20.dp)
                    .shadow(elevation = 3.dp, shape = CircleShape)
                    .clip(CircleShape)
                    .background(Brush.horizontalGradient(listOf(AccentTeal, AccentTeal.copy(alpha = 0.85f))))
                    .border(width = 1.5.dp, color = DarkBackground, shape = CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = opensRemaining.coerceAtMost(9).toString(),
                    color = Color.Black,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}
