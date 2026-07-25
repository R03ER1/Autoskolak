package cz.autokolk.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import cz.autokolk.ui.components.glass.GlassCard
import cz.autokolk.ui.theme.WarningAmber

/**
 * Krok 142 — připomínka spaced-repetition revize chybných otázek na Home obrazovce.
 * Zobrazí se pouze pokud je dnes k dispozici alespoň jedna otázka k opakování
 * (žádný prázdný stav — pokud [dueCount] je 0, volající karty vůbec nevykresluje).
 */
@Composable
fun ReviewReminderCard(
    dueCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (dueCount <= 0) return
    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable(onClick = onClick)
            .semantics {
                contentDescription = "Dnes máš $dueCount ${questionsWord(dueCount)} k opakování. Otevřít revizi chyb."
            },
        borderGradient = listOf(WarningAmber.copy(alpha = 0.55f), WarningAmber.copy(alpha = 0.1f)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.History,
                contentDescription = null,
                tint = WarningAmber,
                modifier = Modifier
                    .clip(CircleShape)
                    .padding(2.dp),
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = "Čas na revizi!",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "Dnes máš $dueCount ${questionsWord(dueCount)} k opakování",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

private fun questionsWord(count: Int): String {
    val n = kotlin.math.abs(count)
    return when {
        n == 1 -> "otázku"
        n in 2..4 -> "otázky"
        else -> "otázek"
    }
}
