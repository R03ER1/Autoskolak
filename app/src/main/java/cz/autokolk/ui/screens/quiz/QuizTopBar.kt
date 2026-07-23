package cz.autokolk.ui.screens.quiz

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cz.autokolk.ui.components.progress.QuizProgressBar
import cz.autokolk.ui.navigation.LocalNavAnimatedVisibilityScope
import cz.autokolk.ui.navigation.LocalSharedTransitionScope
import cz.autokolk.ui.theme.ErrorRed
import cz.autokolk.ui.theme.GlassFill
import cz.autokolk.ui.theme.PillShape
import cz.autokolk.ui.theme.TextPrimary
import cz.autokolk.ui.theme.TextSecondary
import cz.autokolk.ui.theme.WarningAmber
import java.util.Locale

// Shared element transition (krok 41): `heroTransitionKey` napojí pilulku s číslem otázky
// na kolečko lekce (LessonNode) na Home cestě přes SharedTransitionLayout z NavGraph.kt —
// při vstupu do lekce tak kolečko plynule "doputuje" na místo této pilulky v hlavičce.
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun QuizTopBar(
    progress: Float,
    current: Int,
    total: Int,
    testRemainingMs: Long?,
    hearts: Int?,
    comboStreak: Int,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    showCombo: Boolean = true,
    belowProgress: (@Composable () -> Unit)? = null,
    heroTransitionKey: String? = null,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Box(Modifier.fillMaxWidth()) {
            if (showCombo && comboStreak >= 2) {
                val msg = when {
                    comboStreak >= 10 -> "💯 PERFEKTNÍ!"
                    comboStreak >= 5 -> "🔥🔥 ${comboStreak}× combo! Super!"
                    else -> "🔥 ${comboStreak}× combo!"
                }
                Text(
                    text = msg,
                    style = MaterialTheme.typography.labelLarge,
                    color = WarningAmber,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 2.dp),
                )
            }
        }
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
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (hearts != null) {
                    Surface(
                        shape = PillShape,
                        color = GlassFill.copy(alpha = 0.35f),
                    ) {
                        Row(
                            Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Icon(Icons.Default.Favorite, contentDescription = null, tint = ErrorRed, modifier = Modifier.padding(2.dp))
                            Text("$hearts", style = MaterialTheme.typography.labelLarge, color = TextPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                val sharedTransitionScope = LocalSharedTransitionScope.current
                val visibilityScope = LocalNavAnimatedVisibilityScope.current
                val heroModifier = if (heroTransitionKey != null && sharedTransitionScope != null && visibilityScope != null) {
                    with(sharedTransitionScope) {
                        Modifier.sharedBounds(
                            sharedContentState = rememberSharedContentState(key = heroTransitionKey),
                            animatedVisibilityScope = visibilityScope,
                        )
                    }
                } else {
                    Modifier
                }
                Surface(
                    modifier = heroModifier,
                    shape = PillShape,
                    color = GlassFill.copy(alpha = 0.35f),
                ) {
                    AnimatedContent(
                        targetState = current,
                        transitionSpec = {
                            (scaleIn() + fadeIn()).togetherWith(scaleOut() + fadeOut())
                        },
                        label = "questionIndex",
                    ) { c ->
                        Text(
                            text = "$c/$total",
                            color = TextSecondary,
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        )
                    }
                }
            }
            if (testRemainingMs != null) {
                val ms = testRemainingMs
                val m = (ms / 60000).toInt()
                val s = ((ms % 60000) / 1000).toInt()
                val color = when {
                    ms <= 60_000L -> ErrorRed
                    ms <= 5 * 60_000L -> WarningAmber
                    else -> TextPrimary
                }
                val pulse = ms in 1..10_000L
                val scale by animateFloatAsState(
                    targetValue = if (pulse) 1.08f else 1f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "timerPulse",
                )
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = GlassFill.copy(alpha = 0.45f),
                ) {
                    AnimatedContent(
                        targetState = m * 60 + s,
                        transitionSpec = {
                            (fadeIn() + scaleIn()).togetherWith(fadeOut() + scaleOut())
                        },
                        label = "timerDigits",
                    ) {
                        Text(
                            text = String.format(Locale.getDefault(), "%02d:%02d", m, s),
                            color = color,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .scale(scale)
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                        )
                    }
                }
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
        if (belowProgress != null) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                belowProgress()
            }
        }
    }
}
