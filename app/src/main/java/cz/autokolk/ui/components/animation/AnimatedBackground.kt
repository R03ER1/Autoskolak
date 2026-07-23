package cz.autokolk.ui.components.animation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Jemný animovaný radial gradient na pozadí (Fáze 2 / onboarding).
 *
 * Pozadí je vždy nejdřív vyplněné PLNOU (opakní) barvou z aktuálního
 * `MaterialTheme.colorScheme.background` — díky tomu obrazovka vždy odpovídá
 * zvolenému světlému/tmavému režimu (i vizuálnímu stylu), bez ohledu na to,
 * co je "pod" ní (legacy Activity `windowBackground` apod. se tak nikdy
 * neprosvítí). Přes tuto plnou barvu se pak vykreslí jemný, pohyblivý glow
 * tak, aby uprostřed i na okrajích zůstal průhledný (žádný "tvrdý kruh").
 */
@Composable
fun AnimatedBackground(
    modifier: Modifier = Modifier,
    accentColor: Color = MaterialTheme.colorScheme.primary,
    content: @Composable () -> Unit,
) {
    val baseBg = MaterialTheme.colorScheme.background
    val secondaryAccent = MaterialTheme.colorScheme.secondary
    val glowAlpha = 0.10f
    val secondaryGlowAlpha = 0.07f

    val infiniteTransition = rememberInfiniteTransition(label = "animatedBg")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10_000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "animatedBgOffset",
    )

    Box(
        modifier = modifier
            // 1) Plná, vždy theme-správná základní barva pozadí.
            .background(baseBg)
            // 2) Jemný pohyblivý glow nad ní — začíná i končí průhledně,
            //    takže nikdy nevznikne viditelný "prsten" jiné barvy.
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        accentColor.copy(alpha = glowAlpha),
                        Color.Transparent,
                        secondaryAccent.copy(alpha = secondaryGlowAlpha),
                    ),
                    center = Offset(offset * 1000f, offset * 1500f),
                    radius = 900f,
                ),
            ),
    ) {
        content()
    }
}
