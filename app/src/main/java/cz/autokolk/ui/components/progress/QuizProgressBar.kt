package cz.autokolk.ui.components.progress

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import cz.autokolk.ui.theme.AccentCyan
import cz.autokolk.ui.theme.GlassWhite
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
        trackColor = GlassWhite.copy(alpha = 0.25f),
    )
}
