package cz.autokolk.ui.components.feedback

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import cz.autokolk.TutorialManager
import cz.autokolk.ui.components.buttons.PrimaryGradientButton
import cz.autokolk.ui.components.glass.GlassCard
import cz.autokolk.ui.theme.GlassTone
import cz.autokolk.ui.theme.TextPrimary
import kotlinx.coroutines.delay

private const val PREFS_LESSON = "lesson_progress"
private const val KEY_COMPOSE_HOME_TUTORIAL = "compose_home_tutorial_done"

private val stepMessages = listOf(
    "Nahoře máš streak, mince a životy. Klepni na ně kdykoli pro detail.",
    "Tady je tvoje cesta lekcí. Klepni na kolečko pro náhled a start lekce.",
    "Spodní menu přepíná hlavní části aplikace.",
)

/**
 * Multi-step spotlight na Home: horní lišta → uzel lekce → spodní navigace.
 * Po dokončení zapíše i [TutorialManager] klíče pro náhodné události.
 */
@Composable
fun HomeTutorialSpotlightOverlay(
    active: Boolean,
    topBarRect: Rect?,
    lessonRect: Rect?,
    bottomBarRect: Rect?,
    onDismissed: () -> Unit = {},
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = !isComposeHomeTutorialDone(context)
    }
    if (!visible || !active) return

    var step by remember { mutableIntStateOf(0) }
    val target = when (step) {
        0 -> topBarRect
        1 -> lessonRect
        else -> bottomBarRect
    }

    LaunchedEffect(step, topBarRect, lessonRect, bottomBarRect) {
        if (target == null) delay(320)
    }

    Box(Modifier.fillMaxSize()) {
        val pad = with(density) { 10.dp.toPx() }
        val corner = with(density) { 16.dp.toPx() }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen),
        ) {
            drawRect(Color.Black.copy(alpha = 0.72f))
            target?.let { r ->
                val hole = Rect(
                    left = r.left - pad,
                    top = r.top - pad,
                    right = r.right + pad,
                    bottom = r.bottom + pad,
                )
                drawRoundRect(
                    color = Color.Transparent,
                    topLeft = hole.topLeft,
                    size = Size(hole.width, hole.height),
                    cornerRadius = CornerRadius(corner, corner),
                    blendMode = BlendMode.Clear,
                )
            }
        }

        val t = target
        val topPad = if (t != null && step != 2) {
            with(density) { t.bottom.toDp() + 14.dp }
        } else {
            48.dp
        }

        GlassCard(
            // Karta sedí na pevném tmavém scrimu (drawRect Black výše) — glass tón
            // musí zůstat vždy tmavý, jinak by v light režimu vznikl bílo-na-tmavém bug.
            tone = GlassTone.Dark,
            modifier = Modifier
                .align(if (step == 2 && t != null) Alignment.BottomCenter else Alignment.TopCenter)
                .then(
                    if (step == 2 && t != null) {
                        Modifier.padding(bottom = 100.dp, start = 22.dp, end = 22.dp)
                    } else {
                        Modifier.padding(top = topPad, start = 22.dp, end = 22.dp)
                    },
                )
                .fillMaxWidth(),
        ) {
            Column(Modifier.padding(20.dp)) {
                Text(
                    text = stepMessages.getOrElse(step) { "" },
                    color = TextPrimary,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Spacer(Modifier.height(16.dp))
                PrimaryGradientButton(
                    text = if (step < stepMessages.lastIndex) "Další" else "Rozumím",
                    onClick = {
                        if (step < stepMessages.lastIndex) {
                            step++
                        } else {
                            markAllHomeTutorialPrefs(context)
                            visible = false
                            onDismissed()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

private fun isComposeHomeTutorialDone(context: Context): Boolean {
    return context.getSharedPreferences(PREFS_LESSON, Context.MODE_PRIVATE)
        .getBoolean(KEY_COMPOSE_HOME_TUTORIAL, false)
}

private fun markAllHomeTutorialPrefs(context: Context) {
    context.getSharedPreferences(PREFS_LESSON, Context.MODE_PRIVATE)
        .edit()
        .putBoolean(KEY_COMPOSE_HOME_TUTORIAL, true)
        .apply()
    TutorialManager.markShown(context, "tutorial_welcome")
    TutorialManager.markShown(context, "tutorial_home")
}
