package cz.autokolk.ui.screens.alex

import android.graphics.BitmapFactory
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import cz.autokolk.audio.SoundManager
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun AlexCharacter(
    mood: AlexMood,
    hasSunglassesVisual: Boolean,
    modifier: Modifier = Modifier,
    bounceTrigger: Long = 0L,
    heartParticlesTrigger: Long = 0L,
) {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    val fileName = remember(mood, hasSunglassesVisual) {
        AlexAssetResolver.firstExistingAlexFace(context, mood, hasSunglassesVisual)
    }
    val bitmap = remember(fileName) {
        runCatching {
            context.assets.open("images/alex/$fileName").use { stream ->
                BitmapFactory.decodeStream(stream)?.asImageBitmap()
            }
        }.getOrNull()
    }

    val breath = rememberInfiniteTransition(label = "alexBreath")
    val breathScale by breath.animateFloat(
        initialValue = 1f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "breath",
    )

    val bounceScale = remember { Animatable(1f) }
    LaunchedEffect(bounceTrigger) {
        if (bounceTrigger == 0L) return@LaunchedEffect
        bounceScale.snapTo(1f)
        bounceScale.animateTo(1.15f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        bounceScale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
    }

    val rotation = remember { Animatable(0f) }

    var showHeart by remember { mutableStateOf(false) }
    LaunchedEffect(heartParticlesTrigger) {
        if (heartParticlesTrigger == 0L) return@LaunchedEffect
        showHeart = true
        kotlinx.coroutines.delay(900)
        showHeart = false
    }

    Box(modifier, contentAlignment = Alignment.Center) {
        if (showHeart) {
            Text(
                "♥",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.error.copy(alpha = 0.85f),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-8).dp),
            )
        }

        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = "Alex",
                modifier = Modifier
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                SoundManager.play(SoundManager.Sound.ALEX_TAP)
                                scope.launch {
                                    rotation.animateTo(5f, tween(60))
                                    rotation.animateTo(-5f, tween(80))
                                    rotation.animateTo(0f, tween(80))
                                }
                            },
                            onDoubleTap = {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                SoundManager.play(SoundManager.Sound.TAP)
                                scope.launch {
                                    rotation.animateTo(360f, tween(600))
                                    rotation.snapTo(0f)
                                }
                            },
                            onLongPress = {
                                haptics.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
                                SoundManager.play(SoundManager.Sound.ALEX_TAP)
                                showHeart = true
                                scope.launch {
                                    kotlinx.coroutines.delay(800)
                                    showHeart = false
                                }
                            },
                        )
                    }
                    .scale(breathScale * bounceScale.value)
                    .rotate(rotation.value),
            )
        }
    }
}
