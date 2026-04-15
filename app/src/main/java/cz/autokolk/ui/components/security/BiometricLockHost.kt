package cz.autokolk.ui.components.security

import android.util.Log
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import cz.autokolk.ui.settings.AppSettingsStore

private const val TAG = "BiometricLock"

/**
 * Po návratu do aplikace z pozadí vyžádá biometrii, pokud je zámek zapnutý.
 */
@Composable
fun BiometricLockHost() {
    val context = LocalContext.current
    val activity = context as? FragmentActivity ?: return
    val lifecycleOwner = LocalLifecycleOwner.current
    val sawPause = remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val obs = object : DefaultLifecycleObserver {
            override fun onPause(owner: LifecycleOwner) {
                if (AppSettingsStore.isBiometricLockEnabled(context)) {
                    sawPause.value = true
                }
            }

            override fun onResume(owner: LifecycleOwner) {
                if (!AppSettingsStore.isBiometricLockEnabled(context)) return
                if (!sawPause.value) return
                sawPause.value = false

                val bm = BiometricManager.from(context)
                val can = bm.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)
                if (can != BiometricManager.BIOMETRIC_SUCCESS) {
                    Log.d(TAG, "Biometric not available: $can")
                    return
                }

                val executor = ContextCompat.getMainExecutor(context)
                val prompt = BiometricPrompt(
                    activity,
                    executor,
                    object : BiometricPrompt.AuthenticationCallback() {
                        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {}

                        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                            Log.d(TAG, "Biometric error $errorCode: $errString")
                        }

                        override fun onAuthenticationFailed() {
                            Log.d(TAG, "Biometric failed")
                        }
                    },
                )
                val info = BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Odemknout Autoškoláka")
                    .setSubtitle("Pokračuj otiskem nebo obličejem")
                    .setNegativeButtonText("Zrušit")
                    .build()
                try {
                    prompt.authenticate(info)
                } catch (e: Exception) {
                    Log.e(TAG, "authenticate", e)
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(obs)
        onDispose { lifecycleOwner.lifecycle.removeObserver(obs) }
    }
}
