package cz.autokolk.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration

/**
 * Krok 161 — lehká, vlastní náhrada za `androidx.compose.material3.windowsizeclass`
 * (ta by vyžadovala novou Gradle závislost, které jsme se dle zadání vyhnuli). Práh
 * 600dp odpovídá standardní hranici Compact/Medium z Material `WindowSizeClass` a
 * běžně se používá jako "je to tablet" heuristika.
 */
const val EXPANDED_WIDTH_THRESHOLD_DP = 600

/** True, pokud je šířka obrazovky >= 600dp (tablet, nebo telefon v landscape na větším displeji). */
@Composable
fun rememberIsExpandedWidth(): Boolean {
    val configuration = LocalConfiguration.current
    return remember(configuration.screenWidthDp) {
        configuration.screenWidthDp >= EXPANDED_WIDTH_THRESHOLD_DP
    }
}

/** True, pokud je obrazovka širší než vyšší (landscape orientace) — nezávisle na šířce. */
@Composable
fun rememberIsLandscape(): Boolean {
    val configuration = LocalConfiguration.current
    return remember(configuration.screenWidthDp, configuration.screenHeightDp) {
        configuration.screenWidthDp > configuration.screenHeightDp
    }
}

/**
 * True pouze pro "tablet-like" landscape (široký ZÁROVEŇ landscape) — používá se pro
 * side-by-side layouty (např. Quiz: média vlevo, otázka vpravo), aby telefon v běžném
 * landscape (typicky < 600dp šířky) nedostal stejný layout jako tablet.
 */
@Composable
fun rememberIsExpandedLandscape(): Boolean {
    val expanded = rememberIsExpandedWidth()
    val landscape = rememberIsLandscape()
    return expanded && landscape
}
