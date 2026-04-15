package cz.autokolk.ui.screens.achievements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import cz.autokolk.AchievementsManager
import cz.autokolk.AchievementRowUi
import cz.autokolk.ui.components.animation.AnimatedBackground
import cz.autokolk.ui.components.glass.GlassCard
import cz.autokolk.ui.components.progress.AnimatedProgressBar
import cz.autokolk.ui.theme.AccentCyan
import cz.autokolk.ui.theme.GlassFill
import cz.autokolk.ui.theme.TextPrimary
import cz.autokolk.ui.theme.TextSecondary

@Composable
fun AchievementsScreen(@Suppress("UNUSED_PARAMETER") navController: NavHostController) {
    val context = LocalContext.current
    val rows = remember {
        AchievementsManager(context).getAchievementRows()
    }

    AnimatedBackground(Modifier.fillMaxSize()) {
        LazyColumn(
            Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp),
        ) {
            item {
                Text(
                    "Úspěchy",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp),
                )
            }
            items(rows, key = { it.id }) { row ->
                AchievementCard(row)
            }
        }
    }
}

@Composable
private fun AchievementCard(achievement: AchievementRowUi) {
    val alpha = if (achievement.unlocked) 1f else 0.55f
    GlassCard(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .alpha(alpha),
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier
                    .size(48.dp)
                    .background(
                        if (achievement.unlocked) AccentCyan.copy(alpha = 0.2f) else GlassFill,
                        CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    if (achievement.unlocked) Icons.Default.Star else Icons.Default.Lock,
                    contentDescription = null,
                    tint = if (achievement.unlocked) AccentCyan else TextSecondary,
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(achievement.title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                Text(achievement.description, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                if (!achievement.unlocked) {
                    AnimatedProgressBar(
                        progress = achievement.progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        accent = AccentCyan,
                        height = 4.dp,
                    )
                }
            }
            if (achievement.unlocked) {
                Text("⭐", fontSize = 22.sp)
            }
        }
    }
}
