package cz.autokolk.ui.components.progress

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import cz.autokolk.ui.theme.AccentCyan
import cz.autokolk.ui.theme.PillShape

@Composable
fun QuizProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    LinearProgressIndicator(
        progress = { progress.coerceIn(0f, 1f) },
        modifier = modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(PillShape),
        color = AccentCyan,
        // Theme-aware track — fixní 10% bílá by ve světlém režimu na světlém pozadí zmizela.
        trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f),
    )
}
