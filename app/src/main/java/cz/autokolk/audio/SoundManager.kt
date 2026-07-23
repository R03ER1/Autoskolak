package cz.autokolk.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import cz.autokolk.ui.settings.AppSettingsStore

/**
 * Low-latency sound effect engine backed by [SoundPool].
 *
 * Call [init] once (e.g. from [cz.autokolk.App.onCreate]) to build the pool
 * and pre-load every [Sound] whose raw resource already exists. Resources
 * that are missing are silently skipped so the app won't crash before the
 * actual audio files are added.
 *
 * Raw resource names (extension free) are listed in [Sound.resName]. Any of
 * `.ogg`, `.wav` or `.mp3` on disk works — resource ID lookup is agnostic.
 *
 * Sounds respect the "Zvuky" toggle in [AppSettingsStore] on every [play].
 */
object SoundManager {

    private const val TAG = "SoundManager"
    private const val MAX_STREAMS = 6

    private var soundPool: SoundPool? = null
    private val soundIds = mutableMapOf<Sound, Int>()
    private var enabled = true
    private var appContext: Context? = null

    /** Semantic sound events. `resName` is the raw-resource base name. */
    enum class Sound(val resName: String) {
        /** Kvíz — správná odpověď. */
        CORRECT("sound_correct"),

        /** Kvíz — špatná odpověď. */
        WRONG("sound_wrong"),

        /** Kvíz — řetězec správných odpovědí (combo). */
        COMBO("sound_combo"),

        /** Test — poslední sekundy odpočtu / start countdown. */
        COUNTDOWN("sound_countdown"),

        /** Navigace — jemné klepnutí (bottom nav / drobné akce). */
        TAP("sound_tap"),

        /** Navigace — otevření/zavření sheetu. */
        WHOOSH("sound_whoosh"),

        /** Alex — krmení. */
        ALEX_FEED("sound_alex_feed"),

        /** Alex — pohlazení / interakce. */
        ALEX_TAP("sound_alex_tap"),

        /** Overlay odemčení úspěchu. */
        ACHIEVEMENT("sound_achievement"),

        /** Bonusové kolo — tik segmentu. */
        WHEEL_TICK("sound_wheel_tick"),

        /** Bonusové kolo / mystery box — výherní jingle. */
        WHEEL_WIN("sound_wheel_win"),

        /** Milník streaku (overlay). */
        STREAK("sound_streak"),

        /** Získání mincí. */
        COIN("sound_coin"),

        /** XP level-up. */
        LEVELUP("sound_levelup"),
    }

    fun init(context: Context) {
        appContext = context.applicationContext
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

    /**
     * Play a sound event. No-op if the SoundPool hasn't been initialized,
     * if the user disabled sounds, if the raw resource wasn't found, or if
     * the app has been silenced via [setEnabled].
     */
    fun play(sound: Sound, volume: Float = 1f, rate: Float = 1f) {
        if (!enabled) return
        val ctx = appContext
        if (ctx != null && !AppSettingsStore.isSoundEnabled(ctx)) return
        val id = soundIds[sound] ?: return
        val v = volume.coerceIn(0f, 1f)
        val r = rate.coerceIn(0.5f, 2f)
        soundPool?.play(id, v, v, 1, 0, r)
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

/**
 * Composable helper. Guarantees the pool is initialized (idempotent) and
 * returns the singleton for call sites that don't have direct access.
 */
@Composable
fun rememberSoundManager(): SoundManager {
    val context = LocalContext.current
    remember { SoundManager.init(context) }
    return SoundManager
}
