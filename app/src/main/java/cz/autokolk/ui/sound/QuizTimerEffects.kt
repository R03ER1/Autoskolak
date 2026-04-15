package cz.autokolk.ui.sound

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
fun QuizTestCountdownSoundEffect(testRemainingMs: Long?) {
    val context = LocalContext.current
    val lastPlayedSecond = remember { mutableIntStateOf(-1) }
    LaunchedEffect(testRemainingMs) {
        val ms = testRemainingMs ?: return@LaunchedEffect
        if (ms <= 0L || ms > 10_000L) {
            lastPlayedSecond.intValue = -1
            return@LaunchedEffect
        }
        val sec = (ms / 1000L).toInt()
        if (sec == lastPlayedSecond.intValue) return@LaunchedEffect
        lastPlayedSecond.intValue = sec
        playTick(context)
    }
}

private fun playTick(context: Context) {
    try {
        val tg = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 35)
        tg.startTone(ToneGenerator.TONE_PROP_BEEP2, 120)
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            tg.release()
        }, 200)
    } catch (_: Throwable) {
    }
}
