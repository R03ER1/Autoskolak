package cz.autokolk.ui.components.animation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cz.autokolk.ui.theme.AutokolkTokens
import cz.autokolk.ui.theme.GlassFill
import cz.autokolk.ui.theme.GlassWhite
import cz.autokolk.ui.theme.PillShape
import cz.autokolk.ui.theme.WarningAmber
import kotlinx.coroutines.delay

@Composable
fun FloatingReward(
    visible: Boolean,
    amount: Int,
    icon: ImageVector,
    color: Color = WarningAmber,
    onDismiss: () -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn() + scaleIn(initialScale = 0.5f),
        exit = slideOutVertically(targetOffsetY = { -it * 2 }) + fadeOut(),
    ) {
        Row(
            modifier = Modifier
                .background(GlassFill, PillShape)
                .border(AutokolkTokens.GlassBorderWidth, GlassWhite, PillShape)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(4.dp))
            Text(
                text = "+$amount",
                fontWeight = FontWeight.Bold,
                color = color,
            )
        }
    }

    LaunchedEffect(visible) {
        if (visible) {
            delay(2000)
            onDismiss()
        }
    }
}
