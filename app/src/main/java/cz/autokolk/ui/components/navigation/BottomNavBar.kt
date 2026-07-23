package cz.autokolk.ui.components.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cz.autokolk.R
import cz.autokolk.audio.SoundManager
import cz.autokolk.ui.components.glass.GlassCard
import cz.autokolk.ui.navigation.Route
import cz.autokolk.ui.theme.AccentCyan
import cz.autokolk.ui.theme.PillShape
import cz.autokolk.ui.util.rememberHaptic

private data class BottomNavItem(
    val route: Route,
    val iconRes: Int,
    val label: String,
)

private val bottomNavItems = listOf(
    BottomNavItem(Route.Home, R.drawable.ic_home, "Domů"),
    BottomNavItem(Route.Alex, R.drawable.ic_alex, "Alex"),
    BottomNavItem(Route.Test, R.drawable.ic_test, "Zkouška"),
    BottomNavItem(Route.Practice, R.drawable.ic_practice, "Praxe"),
    BottomNavItem(Route.Settings, R.drawable.ic_settings, "Více"),
)

@Composable
fun AutokolkBottomBar(
    currentRoute: String,
    onNavigate: (Route) -> Unit,
    modifier: Modifier = Modifier,
) {
    val haptic = rememberHaptic()
    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp)
            .padding(bottom = 8.dp),
        shape = PillShape,
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            bottomNavItems.forEach { item ->
                val isSelected = currentRoute == item.route.route
                BottomNavItemView(
                    item = item,
                    isSelected = isSelected,
                    onClick = {
                        if (!isSelected) {
                            haptic.onTap()
                            SoundManager.play(SoundManager.Sound.TAP, volume = 0.5f)
                        }
                        onNavigate(item.route)
                    },
                )
            }
        }
    }
}

@Composable
private fun BottomNavItemView(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.15f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 800f),
        label = "navScale",
    )
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) AccentCyan else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(200),
        label = "navColor",
    )

    Column(
        modifier = Modifier
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick,
            )
            .padding(horizontal = 4.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (isSelected) {
                Box(
                    Modifier
                        .size(40.dp)
                        .background(AccentCyan.copy(alpha = 0.15f), CircleShape),
                )
            }
            Icon(
                painter = painterResource(item.iconRes),
                contentDescription = item.label,
                modifier = Modifier
                    .size(24.dp)
                    .scale(scale),
                tint = iconColor,
            )
        }
        AnimatedVisibility(
            visible = isSelected,
            enter = slideInVertically { it } + fadeIn(),
        ) {
            Text(
                text = item.label,
                style = MaterialTheme.typography.labelSmall,
                color = iconColor,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}
