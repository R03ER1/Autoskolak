package cz.autokolk.ui.screens.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cz.autokolk.ui.theme.GlassFill
import cz.autokolk.ui.theme.PillShape
import cz.autokolk.ui.theme.TextSecondary

@Composable
fun QuizPowerUpRow(
    enabled: Boolean,
    onEliminate: () -> Unit,
    onSkip: () -> Unit,
    onHint: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PowerUpPill(
            icon = Icons.Outlined.Delete,
            label = "5",
            onClick = onEliminate,
            enabled = enabled,
        )
        PowerUpPill(
            icon = Icons.Outlined.SkipNext,
            label = "10",
            onClick = onSkip,
            enabled = enabled,
        )
        PowerUpPill(
            icon = Icons.Outlined.Lightbulb,
            label = "3",
            onClick = onHint,
            enabled = enabled,
        )
        Text(
            text = "1 akce / otázku",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            modifier = Modifier.padding(start = 4.dp),
        )
    }
}

@Composable
private fun PowerUpPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    enabled: Boolean,
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = PillShape,
        color = GlassFill.copy(alpha = 0.35f),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Color.White.copy(alpha = 0.12f),
        ),
    ) {
        Row(
            Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(icon, contentDescription = null, tint = TextSecondary, modifier = Modifier.padding(2.dp))
            Text(label, style = MaterialTheme.typography.labelMedium, color = TextSecondary)
        }
    }
}
