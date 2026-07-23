package cz.autokolk.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

/**
 * Určuje, jestli má "glass" komponenta (GlassCard, GlassButton, ...) sledovat
 * aktuální světlý/tmavý režim ([Auto]), nebo zůstat vždy ve stejném tónu.
 *
 * [Dark] a [Light] se používají tam, kde komponenta sedí na ZÁMĚRNĚ fixním pozadí
 * (např. tmavý scrim celoobrazovkových overlayů typu achievement/level-up/tutorial),
 * a musí si tedy udržet konzistentní kontrast bez ohledu na zvolený app theme.
 */
enum class GlassTone { Auto, Dark, Light }

/** Barvy pro „fake glass“ efekt (fill gradient + border) podle [GlassTone]. */
data class GlassPalette(
    val fillStart: Color,
    val fillEnd: Color,
    val borderStart: Color,
    val borderEnd: Color,
    val highlight: Color,
)

@Composable
@ReadOnlyComposable
fun glassPalette(tone: GlassTone = GlassTone.Auto): GlassPalette {
    val isDark = when (tone) {
        GlassTone.Auto -> LocalIsDarkTheme.current
        GlassTone.Dark -> true
        GlassTone.Light -> false
    }
    return if (isDark) {
        GlassPalette(
            fillStart = GlassFill,
            fillEnd = GlassFill.copy(alpha = 0.02f),
            borderStart = GlassWhite,
            borderEnd = Color.Transparent,
            highlight = GlassHighlight,
        )
    } else {
        GlassPalette(
            fillStart = LightGlassFill,
            fillEnd = LightGlassFill.copy(alpha = 0.25f),
            borderStart = LightGlassBorder,
            borderEnd = Color.Transparent,
            highlight = LightGlassHighlight,
        )
    }
}
