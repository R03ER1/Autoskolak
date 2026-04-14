package cz.autokolk.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/**
 * Low-latency sound effect engine backed by [SoundPool].
 *
 * Call [init] once (e.g. from [cz.autokolk.App.onCreate]) to build the pool
 * and pre-load every [Sound] whose raw resource already exists.  Resources
 * that are missing are silently skipped so the app won't crash before the
 * actual OGG files are added in Phase 12.
 *
 * Expected raw resource names: `sound_correct`, `sound_wrong`, `sound_streak`,
 * `sound_coin`, `sound_tap`, `sound_levelup`, `sound_countdown`.
 */
object SoundManager {

    private const val TAG = "SoundManager"
    private const val MAX_STREAMS = 4

    private var soundPool: SoundPool? = null
    private val soundIds = mutableMapOf<Sound, Int>()
    private var enabled = true

    enum class Sound(val resName: String) {
        CORRECT("sound_correct"),
        WRONG("sound_wrong"),
        STREAK("sound_streak"),
        COIN("sound_coin"),
        TAP("sound_tap"),
        LEVELUP("sound_levelup"),
        COUNTDOWN("sound_countdown"),
    }

    fun init(context: Context) {
        if (soundPool != null) return

        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        val pool = SoundPool.Builder()
            .setMaxStreams(MAX_STREAMS)
            .setAudioAttributes(attrs)
            .build()

        soundPool = pool

        val res = context.resources
        val pkg = context.packageName
        for (sound in Sound.entries) {
            val rawId = res.getIdentifier(sound.resName, "raw", pkg)
            if (rawId != 0) {
                soundIds[sound] = pool.load(context, rawId, 1)
            } else {
                Log.d(TAG, "Raw resource '${sound.resName}' not found — skipping")
            }
        }
    }

    fun play(sound: Sound) {
        if (!enabled) return
        val id = soundIds[sound] ?: return
        soundPool?.play(id, 1f, 1f, 1, 0, 1f)
    }

    fun setEnabled(enabled: Boolean) {
        this.enabled = enabled
    }

    fun isEnabled(): Boolean = enabled

    fun release() {
        soundPool?.release()
        soundPool = null
        soundIds.clear()
    }
}

// ── Compose integration ─────────────────────────────────────────────────

@Composable
fun rememberSoundManager(): SoundManager {
    val context = LocalContext.current
    remember { SoundManager.init(context) }
    return SoundManager
}
