package cz.autokolk.ui.screens.alex

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cz.autokolk.AlexDeadBitmapLoader
import cz.autokolk.HungerManager
import cz.autokolk.LessonProgress
import cz.autokolk.ui.components.animation.ConfettiOverlay
import cz.autokolk.ui.navigation.Route
import cz.autokolk.ui.theme.DarkBackground
import cz.autokolk.ui.theme.ErrorRed
import cz.autokolk.ui.theme.TextPrimary
import cz.autokolk.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.max

@Composable
fun AlexDeathScreen(navController: NavHostController) {
    val context = LocalContext.current
    val lessonProgress = remember { LessonProgress(context) }
    val hungerManager = remember { HungerManager(context) }
    val lionName = lessonProgress.getLionName()

    val deadBitmap = remember {
        AlexDeadBitmapLoader.load(context.assets)?.asImageBitmap()
    }

    val holdProgress = remember { Animatable(0f) }
    val lionRotation = remember { Animatable(90f) }
    var pressed by remember { mutableStateOf(false) }
    var holdStart by remember { mutableLongStateOf(0L) }
    var revived by remember { mutableStateOf(false) }
    var confetti by remember { mutableStateOf(false) }

    val holdDurationMs = 3000L

    LaunchedEffect(pressed) {
        if (!pressed || revived) return@LaunchedEffect
        holdStart = System.currentTimeMillis()
        while (isActive && pressed) {
            val elapsed = System.currentTimeMillis() - holdStart
            if (elapsed >= holdDurationMs) {
                hungerManager.setCurrentHunger(50)
                revived = true
                confetti = true
                holdProgress.snapTo(1f)
                lionRotation.snapTo(0f)
                delay(1200)
                confetti = false
                navController.popBackStack(Route.Alex.route, inclusive = false)
                return@LaunchedEffect
            }
            val p = elapsed.toFloat() / holdDurationMs
            holdProgress.snapTo(p)
            lionRotation.snapTo(90f - 90f * p)
            delay(32)
        }
        if (!revived) {
            holdProgress.animateTo(0f, tween(200, easing = LinearEasing))
            lionRotation.animateTo(90f, tween(200, easing = LinearEasing))
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentAlignment = Alignment.Center,
    ) {
        ConfettiOverlay(isActive = confetti, modifier = Modifier.fillMaxSize())

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp),
        ) {
            Text(
                "$lionName vyhladověl!",
                style = MaterialTheme.typography.headlineLarge,
                color = ErrorRed,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "Podrž prst na tlačítku a vrať svého lva zpět.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(32.dp))

            if (deadBitmap != null && !revived) {
                Image(
                    bitmap = deadBitmap,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(Color.White.copy(alpha = 0.85f)),
                    modifier = Modifier
                        .size(220.dp)
                        .rotate(lionRotation.value),
                )
            }

            Spacer(Modifier.height(40.dp))

            if (!revived) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(ErrorRed.copy(alpha = 0.25f + holdProgress.value * 0.5f))
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    pressed = true
                                    try {
                                        tryAwaitRelease()
                                    } finally {
                                        pressed = false
                                    }
                                },
                            )
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = if (pressed) {
                            val left = max(0L, holdDurationMs - (System.currentTimeMillis() - holdStart))
                            "%.1f s".format(left / 1000f)
                        } else {
                            "Podrž"
                        },
                        color = TextPrimary,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
        }
    }
}
