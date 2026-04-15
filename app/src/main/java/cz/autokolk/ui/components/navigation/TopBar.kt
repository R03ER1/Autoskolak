package cz.autokolk.ui.components.navigation

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cz.autokolk.R
import cz.autokolk.ui.components.animation.AnimatedCounter
import cz.autokolk.ui.components.buttons.GlassButton
import cz.autokolk.ui.theme.ErrorRed
import cz.autokolk.ui.theme.TextPrimary
import cz.autokolk.ui.theme.WarningAmber

@Composable
fun AutokolkTopBar(
    streak: Int,
    coins: Int,
    lives: Int,
    onStreakClick: () -> Unit,
    onCoinsClick: () -> Unit,
    onLivesClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        StatBadge(
            iconRes = R.drawable.ic_streak,
            value = streak,
            iconColor = WarningAmber,
            onClick = onStreakClick,
        )
        StatBadge(
            iconRes = R.drawable.ic_coin,
            value = coins,
            iconColor = WarningAmber,
            onClick = onCoinsClick,
        )
        StatBadge(
            iconRes = R.drawable.ic_heart,
            value = lives,
            iconColor = ErrorRed,
            onClick = onLivesClick,
            pulse = lives <= 1,
        )
    }
}

@Composable
private fun StatBadge(
    iconRes: Int,
    value: Int,
    iconColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    pulse: Boolean = false,
) {
    val pulseScale = if (pulse) {
        val transition = rememberInfiniteTransition(label = "pulse")
        val scale by transition.animateFloat(
            initialValue = 1f,
            targetValue = 1.1f,
            animationSpec = infiniteRepeatable(
                animation = tween(800),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "pulseScale",
        )
        scale
    } else {
        1f
    }

    GlassButton(onClick = onClick) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier
                .size(20.dp)
                .scale(pulseScale),
            tint = iconColor,
        )
        Spacer(Modifier.width(6.dp))
        AnimatedCounter(
            targetValue = value,
            color = TextPrimary,
        )
    }
}
