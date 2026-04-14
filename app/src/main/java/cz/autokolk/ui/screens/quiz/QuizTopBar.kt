package cz.autokolk.ui.screens.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.autokolk.ui.components.progress.QuizProgressBar
import cz.autokolk.ui.theme.TextPrimary
import cz.autokolk.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun QuizTopBar(
    progress: Float,
    current: Int,
    total: Int,
    testRemainingMs: Long?,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Zavřít", tint = TextPrimary)
            }
            Text(
                text = "$current / $total",
                color = TextSecondary,
            )
            if (testRemainingMs != null) {
                val m = (testRemainingMs / 60000).toInt()
                val s = ((testRemainingMs % 60000) / 1000).toInt()
                Text(
                    text = String.format(Locale.getDefault(), "%02d:%02d", m, s),
                    color = TextPrimary,
                )
            } else {
                Text(text = " ", color = TextPrimary)
            }
        }
        QuizProgressBar(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
        )
    }
}
