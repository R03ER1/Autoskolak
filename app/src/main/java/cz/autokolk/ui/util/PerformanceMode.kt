package cz.autokolk.ui.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.PowerManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

/**
 * Detekce Low Power Mode / battery saver (krok 156–160 z REDESIGN_PLAN.md), použitá
 * ke snížení náročnosti dekorativních animací a particle efektů (počet částic v konfetách,
 * animovaná pozadí apod.) — vizuální styl zůstává stejný, jen se sníží množství práce na frame.
 */
private fun readPowerSaveModeEnabled(context: Context): Boolean = try {
    val pm = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
    pm?.isPowerSaveMode == true
} catch (_: Throwable) {
    false
}

@Composable
fun rememberPowerSaveModeEnabled(): Boolean {
    val context = LocalContext.current
    var enabled by remember { mutableStateOf(readPowerSaveModeEnabled(context)) }

    DisposableEffect(context) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                enabled = readPowerSaveModeEnabled(context)
            }
        }
        val filter = IntentFilter(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)
        try {
            ContextCompat.registerReceiver(context, receiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
        } catch (_: Throwable) {
        }
        onDispose {
            try {
                context.unregisterReceiver(receiver)
            } catch (_: Throwable) {
            }
        }
    }

    return enabled
}

/**
 * Kombinovaný "nízký výkonový režim": true pokud má uživatel zapnuté systémové odstranění
 * animací (reduced motion) NEBO zařízení běží v režimu spořiče baterie. Používej pro snížení
 * počtu částic / vzorků animace v náročnějších Canvas/particle komponentách
 * ([cz.autokolk.ui.components.animation.ConfettiOverlay], [cz.autokolk.ui.components.animation.AnimatedBackground],
 * lesson path pozadí), zatímco vizuální styl zůstává zachován.
 */
@Composable
fun rememberLowPerformanceModeEnabled(): Boolean {
    val reducedMotion = rememberReducedMotionEnabled()
    val powerSave = rememberPowerSaveModeEnabled()
    return reducedMotion || powerSave
}
