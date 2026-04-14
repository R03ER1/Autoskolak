package cz.autokolk.ui.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView

/**
 * Centralized haptic feedback utility.
 *
 * Uses [HapticFeedbackConstants] (API 30+ for CONFIRM/REJECT) with a
 * [Vibrator] fallback on older devices. minSdk of this project is 24.
 */
object HapticFeedback {

    fun light(view: View) {
        view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
    }

    fun medium(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
        } else {
            view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
        }
    }

    fun heavy(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            view.performHapticFeedback(HapticFeedbackConstants.REJECT)
        } else {
            vibrate(view.context, 80L)
        }
    }

    fun success(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
        } else {
            vibrate(view.context, longArrayOf(0, 30, 60, 30), -1)
        }
    }

    fun error(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            view.performHapticFeedback(HapticFeedbackConstants.REJECT)
        } else {
            vibrate(view.context, longArrayOf(0, 40, 50, 40, 50, 40), -1)
        }
    }

    fun streak(view: View) {
        vibrate(view.context, longArrayOf(0, 30, 80, 50, 80, 70), -1)
    }

    // ── Vibrator helpers ────────────────────────────────────────────────

    private fun vibrate(context: Context, ms: Long) {
        val vibrator = vibrator(context) ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(ms)
        }
    }

    private fun vibrate(context: Context, pattern: LongArray, repeat: Int) {
        val vibrator = vibrator(context) ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, repeat))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(pattern, repeat)
        }
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
}

// ── Compose integration ─────────────────────────────────────────────────

class HapticFeedbackHelper(private val view: View) {
    fun light() = HapticFeedback.light(view)
    fun medium() = HapticFeedback.medium(view)
    fun heavy() = HapticFeedback.heavy(view)
    fun success() = HapticFeedback.success(view)
    fun error() = HapticFeedback.error(view)
    fun streak() = HapticFeedback.streak(view)
}

@Composable
fun rememberHaptic(): HapticFeedbackHelper {
    val view = LocalView.current
    return remember { HapticFeedbackHelper(view) }
}
