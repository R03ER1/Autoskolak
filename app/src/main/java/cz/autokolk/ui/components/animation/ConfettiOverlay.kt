package cz.autokolk.ui.components.animation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Konfety po správné odpovědi. Plná implementace zůstává v plánu (Canvas / particles);
 * zatím neblokuje layout ani výkon.
 */
@Composable
fun ConfettiOverlay(
    isActive: Boolean,
    modifier: Modifier = Modifier,
) {
    if (!isActive) return
    // Intentionally empty — visual polish can be added later without API changes.
}
