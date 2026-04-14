package cz.autokolk.ui.components.feedback

import androidx.compose.runtime.Composable

/**
 * Placeholder pro budoucí náhodné události (např. bonus z [cz.autokolk.RandomEventManager]).
 */
data class RandomEventStub(
    val id: String,
    val title: String,
    val message: String,
)

@Composable
fun EventOverlay(
    event: RandomEventStub? = null,
) {
    if (event == null) return
    // Budoucí: dialog / bottom sheet s [event]
}
