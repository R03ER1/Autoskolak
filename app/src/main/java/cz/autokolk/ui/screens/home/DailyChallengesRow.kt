package cz.autokolk.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.autokolk.DailyChallengeUi
import cz.autokolk.ui.components.glass.GlassCard
import cz.autokolk.ui.components.progress.AnimatedProgressBar
import cz.autokolk.ui.theme.AccentCyan
import cz.autokolk.ui.theme.WarningAmber

@Composable
fun DailyChallengesRow(
    challenges: List<DailyChallengeUi>,
    modifier: Modifier = Modifier,
) {
    if (challenges.isEmpty()) return
    Column(modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
        Text(
            text = "Denní výzvy",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp),
        )
        LazyRow(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(challenges, key = { it.id }) { c ->
                DailyChallengeCard(challenge = c)
            }
        }
    }
}

@Composable
private fun DailyChallengeCard(challenge: DailyChallengeUi) {
    GlassCard(
        modifier = Modifier.width(200.dp),
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(
                text = challenge.title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(6.dp))
            AnimatedProgressBar(
                progress = if (challenge.done) 1f else challenge.progress,
                accent = if (challenge.done) WarningAmber else AccentCyan,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = when {
                    challenge.done -> "Hotovo · +${challenge.rewardXp} XP"
                    else -> "Odměna +${challenge.rewardXp} XP"
                },
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
