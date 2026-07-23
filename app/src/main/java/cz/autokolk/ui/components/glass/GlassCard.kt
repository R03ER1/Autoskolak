package cz.autokolk.ui.components.glass

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import cz.autokolk.ui.theme.AutokolkShapes
import cz.autokolk.ui.theme.AutokolkTokens
import cz.autokolk.ui.theme.GlassTone
import cz.autokolk.ui.theme.glassPalette

/**
 * Jednoduchý „fake glass“ bez rozostření pozadí — levnější na GPU.
 *
 * [tone] řídí, jestli se barva skla přizpůsobí aktuálnímu světlému/tmavému
 * režimu ([GlassTone.Auto], výchozí — vhodné pro běžné obrazovky/karty), nebo
 * zůstane vždy stejná ([GlassTone.Dark]/[GlassTone.Light]) — to se používá u
 * komponent na záměrně fixním pozadí (např. celoobrazovkové overlaye s tmavým
 * scrimem), kde by přizpůsobení tématu naopak rozbilo kontrast.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = AutokolkShapes.medium,
    tone: GlassTone = GlassTone.Auto,
    borderGradient: List<Color>? = null,
    content: @Composable () -> Unit,
) {
    val palette = glassPalette(tone)
    val border = borderGradient ?: listOf(palette.borderStart, palette.borderEnd)
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(palette.fillStart, palette.fillEnd),
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
                ),
            )
            .border(
                width = AutokolkTokens.GlassBorderWidth,
                brush = Brush.linearGradient(colors = border),
                shape = shape,
            ),
    ) {
        content()
    }
}

/**
 * Skutečný blur přes Haze — vyžaduje sourozenecký obsah s [dev.chrisbanes.haze.hazeSource].
 * Dražší na render než [GlassCard].
 */
@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun GlassCardBlur(
    hazeState: HazeState,
    modifier: Modifier = Modifier,
    shape: Shape = AutokolkShapes.medium,
    tone: GlassTone = GlassTone.Auto,
    borderGradient: List<Color>? = null,
    style: HazeStyle = HazeMaterials.thin(MaterialTheme.colorScheme.surface),
    content: @Composable () -> Unit,
) {
    val palette = glassPalette(tone)
    val border = borderGradient ?: listOf(palette.borderStart, palette.borderEnd)
    Box(
        modifier = modifier
            .clip(shape)
            .hazeEffect(state = hazeState, style = style)
            .border(
                width = AutokolkTokens.GlassBorderWidth,
                brush = Brush.linearGradient(colors = border),
                shape = shape,
            ),
    ) {
        content()
    }
}
