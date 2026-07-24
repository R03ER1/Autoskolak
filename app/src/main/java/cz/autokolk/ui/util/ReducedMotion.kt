package cz.autokolk.ui.util

import android.content.Context
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

/**
 * Reduced-motion podpora (krok 159–160 z REDESIGN_PLAN.md).
 *
 * Respektuje systémové accessibility nastavení "Odstranit animace" (Android Nastavení
 * → Přístupnost → Odstranění animací), které se promítá do [Settings.Global.ANIMATOR_DURATION_SCALE]
 * (hodnota `0f` = uživatel animace vypnul). Používá se pro potlačení čistě dekorativních
 * animací (pulzující glow, nekonečné shimmer/gradient smyčky, konfety) — funkční animace
 * (např. vyplňování progress baru) se nevypínají, jen zkracují/zjednodušují podle potřeby
 * volajícího místa.
 */
private fun readReducedMotionEnabled(context: Context): Boolean = try {
    Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
} catch (_: Throwable) {
    false
}

/**
 * Composable helper — sleduje [Settings.Global.ANIMATOR_DURATION_SCALE] i za běhu (uživatel
 * může přepnout nastavení, aniž by aplikaci restartoval), pomocí [ContentObserver].
 */
@Composable
fun rememberReducedMotionEnabled(): Boolean {
    val context = LocalContext.current
    var enabled by remember { mutableStateOf(readReducedMotionEnabled(context)) }

    DisposableEffect(context) {
        val handler = Handler(Looper.getMainLooper())
        val observer = object : ContentObserver(handler) {
            override fun onChange(selfChange: Boolean) {
                enabled = readReducedMotionEnabled(context)
            }
        }
        val uri = Settings.Global.getUriFor(Settings.Global.ANIMATOR_DURATION_SCALE)
        try {
            context.contentResolver.registerContentObserver(uri, false, observer)
        } catch (_: Throwable) {
        }
        onDispose {
            try {
                context.contentResolver.unregisterContentObserver(observer)
            } catch (_: Throwable) {
            }
        }
    }

    return enabled
}
