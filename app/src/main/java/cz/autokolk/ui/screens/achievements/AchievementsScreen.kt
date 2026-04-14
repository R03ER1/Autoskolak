package cz.autokolk.ui.screens.achievements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cz.autokolk.AchievementsManager
import cz.autokolk.ui.theme.TextPrimary
import cz.autokolk.ui.theme.TextSecondary

private data class AchievementRow(val title: String, val key: String)

@Composable
fun AchievementsScreen(navController: NavHostController) {
    val context = LocalContext.current
    val mgr = remember { AchievementsManager(context) }
    val rows = remember {
        listOf(
            AchievementRow("Streak — úroveň 1", "streak_tier_1_unlocked"),
            AchievementRow("Streak — úroveň 2", "streak_tier_2_unlocked"),
            AchievementRow("Streak — úroveň 3", "streak_tier_3_unlocked"),
            AchievementRow("Opravy chyb", "fixes_tier_1_unlocked"),
            AchievementRow("Správné odpovědi", "answers_correct_tier_1_unlocked"),
            AchievementRow("Penízky — získané", "coins_earned_tier_1_unlocked"),
        )
    }

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        item {
            Text("Úspěchy", style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
            Text(
                "Odemykají se automaticky při hraní.",
                color = TextSecondary,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        items(rows) { row ->
            val ok = mgr.isUnlocked(row.key)
            Column(Modifier.padding(vertical = 8.dp)) {
                Text(
                    row.title,
                    color = if (ok) TextPrimary else TextSecondary,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    if (ok) "Odemčeno" else "Zamčeno",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}
