package cz.autokolk.ui.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.core.content.edit

/**
 * Jednoduchý přehrávač zvuků (krok 138–141 — rozšířitelné o assety v `res/raw`).
 */
object AppSound {
    private var pool: SoundPool? = null

    private fun ensurePool(context: Context): SoundPool {
        pool?.let { return it }
        val p = SoundPool.Builder()
            .setMaxStreams(4)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build(),
            )
            .build()
        pool = p
        return p
    }

    fun isEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences("autokolk_sound", Context.MODE_PRIVATE)
        return prefs.getBoolean("sound_enabled", true)
    }

    fun setEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences("autokolk_sound", Context.MODE_PRIVATE)
            .edit { putBoolean("sound_enabled", enabled) }
    }

    /** Placeholder — po přidání raw zdroje zde načti ID a přehraj. */
    fun playQuizCorrect(context: Context) {
        if (!isEnabled(context)) return
        ensurePool(context)
        // val id = pool.load(context, R.raw.quiz_correct, 1)
        // pool.play(id, 1f, 1f, 1, 0, 1f)
    }
}
