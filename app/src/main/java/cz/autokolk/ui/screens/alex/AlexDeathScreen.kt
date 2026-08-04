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
import cz.autokolk.ui.components.buttons.PrimaryGradientButton
import cz.autokolk.ui.components.media.AssetImageFromPath
import cz.autokolk.ui.navigation.Route
import cz.autokolk.ui.theme.ErrorRed
import cz.autokolk.ui.theme.SuccessGreen
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
    val happyFacePath = remember {
        AlexAssetResolver.firstExistingAlexFace(context, AlexMood.Happy, lessonProgress.isSunglassesEnabled())
    }

    val holdProgress = remember { Animatable(0f) }
    val lionRotation = remember { Animatable(90f) }
    var pressed by remember { mutableStateOf(false) }
    var holdStart by remember { mutableLongStateOf(0L) }
    var revived by remember { mutableStateOf(false) }
    var confetti by remember { mutableStateOf(false) }

    val holdDurationMs = 3000L

    // Pozn.: tato korutina NESMÍ záviset na `pressed` v okamžiku dokončení revive gesta —
    // jakmile se `revived` nastaví na true, tlačítko (Box s pointerInput níže) zmizí
    // z kompozice, což zruší probíhající `tryAwaitRelease()` a ve `finally` nastaví
    // `pressed = false`. Změna klíče `pressed` by LaunchedEffect(pressed) restartovala
    // a zrušila by rozběhnutou korutinu dřív, než stihne dokončit revive (proto dřív
    // obrazovka po podržení "zamrzla" bez možnosti pokračovat). Konfety a přechod na
    // stav "zachráněn" proto řeší samostatný LaunchedEffect(revived) níže.
    LaunchedEffect(pressed) {
        if (!pressed || revived) return@LaunchedEffect
        holdStart = System.currentTimeMillis()
        while (isActive && pressed) {
            val elapsed = System.currentTimeMillis() - holdStart
            if (elapsed >= holdDurationMs) {
                hungerManager.setCurrentHunger(50)
                holdProgress.snapTo(1f)
                lionRotation.snapTo(0f)
                revived = true
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

    LaunchedEffect(revived) {
        if (!revived) return@LaunchedEffect
        confetti = true
        delay(1200)
        confetti = false
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        ConfettiOverlay(isActive = confetti, modifier = Modifier.fillMaxSize())

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp),
        ) {
            Text(
                if (revived) "$lionName zachráněn!" else "$lionName vyhladověl!",
                style = MaterialTheme.typography.headlineLarge,
                color = if (revived) SuccessGreen else ErrorRed,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                if (revived) {
                    "Podařilo se! $lionName má zase sytost 50 % a čeká na tebe."
                } else {
                    "Podrž prst na tlačítku a vrať svého lva zpět."
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(32.dp))

            if (revived) {
                AssetImageFromPath(
                    assetPath = "images/alex/$happyFacePath",
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(220.dp),
                )
            } else if (deadBitmap != null) {
                Image(
                    bitmap = deadBitmap,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)),
                    modifier = Modifier
                        .size(220.dp)
                        .rotate(lionRotation.value),
                )
            }

            Spacer(Modifier.height(40.dp))

            if (revived) {
                PrimaryGradientButton(
                    text = "Pokračovat",
                    onClick = {
                        navController.popBackStack(Route.Alex.route, inclusive = false)
                    },
                )
            } else {
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
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
        }
    }
}
