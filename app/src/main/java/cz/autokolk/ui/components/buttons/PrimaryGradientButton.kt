package cz.autokolk.ui.components.buttons

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cz.autokolk.ui.theme.PillShape
import cz.autokolk.ui.util.rememberLowPerformanceModeEnabled

/**
 * Hlavní CTA s gradientem, barevným stínem (glow) a volitelným jemným shimmerem přes gradient.
 */
@Composable
fun PrimaryGradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    shimmerEnabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.5f),
        label = "primaryGradientScale",
    )
    val elevation by animateDpAsState(
        targetValue = if (isPressed) 4.dp else 12.dp,
        animationSpec = spring(dampingRatio = 0.5f),
        label = "primaryGradientElevation",
    )

    // Krok 160: v reduced-motion / low-power režimu shimmer neběží (čistě dekorativní).
    // Disabled tlačítko shimmer nepotřebuje vůbec — je to jasný signál "nedostupné".
    val lowPerformanceMode = rememberLowPerformanceModeEnabled()
    val shimmerActive = shimmerEnabled && !lowPerformanceMode && enabled
    val shimmerPhase = if (shimmerActive) {
        val shimmer = rememberInfiniteTransition(label = "primaryGradientShimmer")
        val animatedPhase by shimmer.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2200, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
            label = "primaryGradientShimmerPhase",
        )
        animatedPhase
    } else {
        0f
    }

    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val onGradient = MaterialTheme.colorScheme.onPrimary

    // Standardní Material3 vzhled pro disabled stav (stejné alpha hodnoty jako výchozí
    // ButtonDefaults.disabledContainerColor/disabledContentColor) — bez barevného gradientu
    // a bez glow stínu, ať je na první pohled jasné, že tlačítko není momentálně dostupné.
    val disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    val disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    val backgroundBrush = if (enabled) {
        Brush.horizontalGradient(listOf(primary, secondary))
    } else {
        SolidColor(disabledContainerColor)
    }
    val contentColor = if (enabled) onGradient else disabledContentColor

    Box(
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = if (enabled) elevation else 0.dp,
                shape = PillShape,
                ambientColor = primary.copy(alpha = if (enabled) 0.3f else 0f),
                spotColor = secondary.copy(alpha = if (enabled) 0.3f else 0f),
            )
            .clip(PillShape)
            .background(backgroundBrush)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(),
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            )
            .padding(horizontal = 32.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        // Shimmer jen jako kresba přes obsah — žádný druhý child s fillMaxSize, který by při puštění prstu
        // znovu roztáhl tlačítko na celou šířku rodiče (fillMaxWidth z onboardingu).
        Box(
            Modifier
                .wrapContentWidth()
                .clip(PillShape)
                .drawWithContent {
                    drawContent()
                    if (shimmerActive && enabled && !isPressed) {
                        drawRect(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.White.copy(alpha = 0.16f),
                                    Color.Transparent,
                                ),
                                start = Offset(
                                    x = (shimmerPhase - 0.4f) * 400f,
                                    y = 0f,
                                ),
                                end = Offset(
                                    x = (shimmerPhase + 0.4f) * 400f,
                                    y = size.height,
                                ),
                            ),
                            size = size,
                        )
                    }
                },
            contentAlignment = Alignment.Center,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = contentColor,
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    color = contentColor,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}
