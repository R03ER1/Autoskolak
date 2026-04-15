package cz.autokolk.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cz.autokolk.ui.components.media.AssetImageFromPath
import cz.autokolk.ui.components.progress.RingProgress
import cz.autokolk.ui.theme.AccentCyan
import cz.autokolk.ui.theme.AccentTeal
import cz.autokolk.ui.theme.DarkSurfaceVariant
import cz.autokolk.ui.theme.ErrorRed
import cz.autokolk.ui.theme.GlassWhite
import cz.autokolk.ui.theme.SuccessGreen

enum class LessonNodeState {
    LOCKED,
    CURRENT,
    COMPLETED,
    PERFECT,
}

// TODO: Shared element transition — use sharedElement() modifier to animate
//  this LessonNode icon into the Quiz header when navigating to a quiz.
@Composable
fun LessonNode(
    iconFileName: String,
    sectionColor: Color,
    state: LessonNodeState,
    ringProgress: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
) {
    val locked = state == LessonNodeState.LOCKED
    val showRing = state == LessonNodeState.COMPLETED || state == LessonNodeState.PERFECT
    val ringColors = when (state) {
        LessonNodeState.PERFECT -> listOf(SuccessGreen, SuccessGreen.copy(alpha = 0.85f))
        else -> listOf(AccentCyan, AccentTeal)
    }
    val assetPath = "images/lesson_icons/$iconFileName"

    Box(
        modifier = modifier
            .size(size + 24.dp)
            .then(
                if (state == LessonNodeState.CURRENT) {
                    Modifier.pulsingGlow(enabled = true, cornerRadius = 999.dp)
                } else {
                    Modifier
                },
            )
            .alpha(if (locked) 0.5f else 1f)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (showRing) {
            RingProgress(
                progress = ringProgress,
                size = size + 18.dp,
                strokeWidth = 5.dp,
                trackColor = ErrorRed.copy(alpha = 0.4f),
                gradient = ringColors,
            ) {
                AssetImageFromPath(
                    assetPath = assetPath,
                    contentDescription = null,
                    modifier = Modifier
                        .size(size - 6.dp)
                        .background(DarkSurfaceVariant.copy(alpha = 0.15f), CircleShape),
                    contentScale = ContentScale.Fit,
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .size(size + 8.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                sectionColor.copy(alpha = 0.95f),
                                sectionColor.copy(alpha = 0.55f),
                            ),
                        ),
                        shape = CircleShape,
                    )
                    .border(2.dp, GlassWhite.copy(alpha = 0.35f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                AssetImageFromPath(
                    assetPath = assetPath,
                    contentDescription = null,
                    modifier = Modifier.size(size - 8.dp),
                    contentScale = ContentScale.Fit,
                )
            }
        }
    }
}
