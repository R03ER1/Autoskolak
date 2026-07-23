package cz.autokolk.ui.screens.home

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.autokolk.ui.components.glass.GlassCard
import cz.autokolk.ui.theme.AccentCyan

@Composable
fun SeasonalBanner(message: String) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        borderGradient = listOf(AccentCyan.copy(alpha = 0.5f), AccentCyan.copy(alpha = 0.1f)),
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(14.dp),
        )
    }
}
