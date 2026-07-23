package cz.autokolk.ui.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import androidx.core.content.ContextCompat
import cz.autokolk.ui.settings.AppSettingsStore

/**
 * Centrální utilita pro haptickou odezvu.
 *
 * Všechny veřejné metody respektují uživatelský přepínač
 * [AppSettingsStore.isHapticEnabled]. K dispozici jsou dvě rodiny volání:
 *
 *  * primitivní: [light], [medium], [heavy], [success], [error], [streak] —
 *    berou [View] nebo [Context] (pro ViewModely) a používají systémové
 *    [HapticFeedbackConstants] tam kde to jde, jinak [Vibrator] fallback,
 *  * sémantické: [onCorrect], [onWrong], [onTap], [onCombo], [onCountdown],
 *    [onMilestone], [onAchievement] — přímé pojmenované eventy pro UI
 *    i ViewModely.
 *
 * `minSdk` projektu je 24, takže lze používat vzory ≥ O.
 */
object HapticFeedback {

    // ── Sémantické eventy (preferovaná API) ─────────────────────────────

    /** Kvíz — správná odpověď. */
    fun onCorrect(context: Context) = light(context)
    fun onCorrect(view: View) = light(view)

    /** Kvíz — špatná odpověď (silnější). */
    fun onWrong(context: Context) = error(context)
    fun onWrong(view: View) = error(view)

    /** Kvíz — combo streak (jemný success). */
    fun onCombo(context: Context) = success(context)
    fun onCombo(view: View) = success(view)

    /** Bottom nav / obecné klepnutí — nejjemnější. */
    fun onTap(context: Context) = light(context)
    fun onTap(view: View) = light(view)

    /** Test — tik při odpočtu (velmi krátký). */
    fun onCountdown(context: Context) = tick(context)
    fun onCountdown(view: View) = view.performHapticFeedbackChecked(HapticFeedbackConstants.CLOCK_TICK)

    /** Odemčení achievementu / milníku (silný krátký pattern). */
    fun onAchievement(context: Context) = heavy(context)
    fun onAchievement(view: View) = heavy(view)

    /** Milník streaku — oslavná vlnovka. */
    fun onMilestone(context: Context) = streak(context)
    fun onMilestone(view: View) = streak(view)

    // ── Primitivní úrovně ───────────────────────────────────────────────

    fun light(view: View) {
        if (!isEnabled(view.context)) return
        view.performHapticFeedbackChecked(HapticFeedbackConstants.CLOCK_TICK)
    }

    fun light(context: Context) {
        if (!isEnabled(context)) return
        tick(context)
    }

    fun medium(view: View) {
        if (!isEnabled(view.context)) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            view.performHapticFeedbackChecked(HapticFeedbackConstants.CONFIRM)
        } else {
            view.performHapticFeedbackChecked(HapticFeedbackConstants.CONTEXT_CLICK)
        }
    }

    fun medium(context: Context) {
        if (!isEnabled(context)) return
        oneShot(context, 30L)
    }

    fun heavy(view: View) {
        if (!isEnabled(view.context)) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            view.performHapticFeedbackChecked(HapticFeedbackConstants.REJECT)
        } else {
            oneShot(view.context, 80L)
        }
    }

    fun heavy(context: Context) {
        if (!isEnabled(context)) return
        oneShot(context, 80L)
    }

    fun success(view: View) {
        if (!isEnabled(view.context)) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            view.performHapticFeedbackChecked(HapticFeedbackConstants.CONFIRM)
        } else {
            waveform(view.context, longArrayOf(0, 30, 60, 30), -1)
        }
    }

    fun success(context: Context) {
        if (!isEnabled(context)) return
        waveform(context, longArrayOf(0, 30, 60, 30), -1)
    }

    fun error(view: View) {
        if (!isEnabled(view.context)) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            view.performHapticFeedbackChecked(HapticFeedbackConstants.REJECT)
        } else {
            waveform(view.context, longArrayOf(0, 40, 50, 40, 50, 40), -1)
        }
    }

    fun error(context: Context) {
        if (!isEnabled(context)) return
        waveform(context, longArrayOf(0, 40, 50, 40, 50, 40), -1)
    }

    fun streak(view: View) {
        if (!isEnabled(view.context)) return
        waveform(view.context, longArrayOf(0, 30, 80, 50, 80, 70), -1)
    }

    fun streak(context: Context) {
        if (!isEnabled(context)) return
        waveform(context, longArrayOf(0, 30, 80, 50, 80, 70), -1)
    }

    // ── privátní helpery ────────────────────────────────────────────────

    private fun isEnabled(context: Context): Boolean =
        AppSettingsStore.isHapticEnabled(context)

    private fun tick(context: Context) = oneShot(context, 12L, amplitude = 28)

    private fun oneShot(
        context: Context,
        ms: Long,
        amplitude: Int = VibrationEffect.DEFAULT_AMPLITUDE,
    ) {
        if (!hasVibratePermission(context)) return
        val vibrator = vibrator(context) ?: return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val amp = if (amplitude == VibrationEffect.DEFAULT_AMPLITUDE) {
                    VibrationEffect.DEFAULT_AMPLITUDE
                } else {
                    amplitude.coerceIn(1, 255)
                }
                vibrator.vibrate(VibrationEffect.createOneShot(ms, amp))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(ms)
            }
        } catch (_: SecurityException) {
        } catch (_: Throwable) {
        }
    }

    private fun waveform(context: Context, pattern: LongArray, repeat: Int) {
        if (!hasVibratePermission(context)) return
        val vibrator = vibrator(context) ?: return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, repeat))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(pattern, repeat)
            }
        } catch (_: SecurityException) {
        } catch (_: Throwable) {
        }
    }

    private fun hasVibratePermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.VIBRATE,
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun vibrator(context: Context): Vibrator? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE)
                as? VibratorManager
            manager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    /** Bezpečná varianta [View.performHapticFeedback], tichá při výjimce. */
    private fun View.performHapticFeedbackChecked(constant: Int) {
        try {
            performHapticFeedback(constant)
        } catch (_: Throwable) {
        }
    }
}

// ── Compose integration ─────────────────────────────────────────────────

class HapticFeedbackHelper(private val view: View) {
    fun light() = HapticFeedback.light(view)
    fun medium() = HapticFeedback.medium(view)
    fun heavy() = HapticFeedback.heavy(view)
    fun success() = HapticFeedback.success(view)
    fun error() = HapticFeedback.error(view)
    fun streak() = HapticFeedback.streak(view)

    // Sémantické API
    fun onCorrect() = HapticFeedback.onCorrect(view)
    fun onWrong() = HapticFeedback.onWrong(view)
    fun onCombo() = HapticFeedback.onCombo(view)
    fun onTap() = HapticFeedback.onTap(view)
    fun onCountdown() = HapticFeedback.onCountdown(view)
    fun onAchievement() = HapticFeedback.onAchievement(view)
    fun onMilestone() = HapticFeedback.onMilestone(view)
}

@Composable
fun rememberHaptic(): HapticFeedbackHelper {
    val view = LocalView.current
    return remember { HapticFeedbackHelper(view) }
}
