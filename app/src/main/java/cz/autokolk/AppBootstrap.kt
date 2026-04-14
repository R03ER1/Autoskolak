package cz.autokolk

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.FormError
import com.google.android.ump.UserMessagingPlatform

/** Aktualizace UI během stahování DFM modulu (volitelné u Compose startu). */
interface LoadingUi {
    fun updateProgress(percent: Int?)
    fun showError()
}

/**
 * Sdílená logika: GDPR consent → instalace imageassets modulu → podmínky → [onReady].
 * Používá [LoadingActivity] (s layoutem) i [ComposeMainActivity] (bez progress UI).
 */
class AppBootstrap(
    private val activity: ComponentActivity,
    private val ui: LoadingUi?,
    private val onReady: () -> Unit,
) {
    private lateinit var splitInstallManager: SplitInstallManager
    private val imageModuleName = "imageassets"

    private val stateListener = SplitInstallStateUpdatedListener { state ->
        val modules = state.moduleNames()
        if (!modules.contains(imageModuleName)) {
            return@SplitInstallStateUpdatedListener
        }
        when (state.status()) {
            SplitInstallSessionStatus.DOWNLOADING -> {
                val bytesDownloaded = state.bytesDownloaded()
                val totalBytes = state.totalBytesToDownload()
                if (totalBytes > 0L) {
                    val progress = (bytesDownloaded * 100 / totalBytes).toInt()
                    ui?.updateProgress(progress)
                } else {
                    ui?.updateProgress(null)
                }
            }
            SplitInstallSessionStatus.INSTALLED -> {
                Log.d(TAG, "Image module '$imageModuleName' installed")
                ensureTermsAcceptedThenNavigate()
            }
            SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
                ui?.updateProgress(null)
            }
            SplitInstallSessionStatus.INSTALLING,
            SplitInstallSessionStatus.PENDING,
            SplitInstallSessionStatus.CANCELING,
            SplitInstallSessionStatus.CANCELED,
            SplitInstallSessionStatus.UNKNOWN,
            -> {
                ui?.updateProgress(null)
            }
            SplitInstallSessionStatus.FAILED -> {
                Log.e(TAG, "Image module install failed: ${state.errorCode()}")
                ui?.showError()
            }
        }
    }

    fun start() {
        splitInstallManager = SplitInstallManagerFactory.create(activity)
        splitInstallManager.registerListener(stateListener)
        requestAdsConsentThenStartInstall()
    }

    fun dispose() {
        if (::splitInstallManager.isInitialized) {
            try {
                splitInstallManager.unregisterListener(stateListener)
            } catch (_: Throwable) {
            }
        }
    }

    private fun requestAdsConsentThenStartInstall() {
        val params = ConsentRequestParameters.Builder().build()
        val consentInformation = UserMessagingPlatform.getConsentInformation(activity)
        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { _: FormError? ->
                    startOrCheckInstall()
                }
            },
            { _: FormError? ->
                startOrCheckInstall()
            },
        )
    }

    fun retryInstall() {
        startOrCheckInstall()
    }

    private fun startOrCheckInstall() {
        if (splitInstallManager.installedModules.contains(imageModuleName)) {
            Log.d(TAG, "Image module already installed")
            ensureTermsAcceptedThenNavigate()
            return
        }
        ui?.updateProgress(null)
        try {
            val request = SplitInstallRequest.newBuilder()
                .addModule(imageModuleName)
                .build()
            splitInstallManager
                .startInstall(request)
                .addOnSuccessListener { sessionId ->
                    Log.d(TAG, "Image module install started (sessionId=$sessionId)")
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Failed to request install for module '$imageModuleName'", exception)
                    ui?.showError()
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error while requesting image module installation", e)
            ui?.showError()
        }
    }

    private fun ensureTermsAcceptedThenNavigate() {
        val prefs = activity.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val key = "terms_accepted_v1"
        if (prefs.getBoolean(key, false)) {
            onReady()
            return
        }

        val fullText = "Používáním aplikace souhlasíte s podmínkami používání a zásadami ochrany soukromí."
        val linkText = "podmínkami používání a zásadami ochrany soukromí"
        val start = fullText.indexOf(linkText)
        val spannable = SpannableString(fullText)
        if (start >= 0) {
            val end = start + linkText.length
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    try {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://sites.google.com/view/dos-pachos-studio/zásady-ochrany-soukromí"),
                        )
                        activity.startActivity(intent)
                    } catch (_: Throwable) {
                    }
                }
            }
            spannable.setSpan(clickableSpan, start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
            val linkColor = ContextCompat.getColor(activity, R.color.text_primary)
            spannable.setSpan(
                ForegroundColorSpan(linkColor),
                start,
                end,
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE,
            )
            spannable.setSpan(
                UnderlineSpan(),
                start,
                end,
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE,
            )
        }

        val messageView = TextView(activity).apply {
            text = spannable
            movementMethod = LinkMovementMethod.getInstance()
            setTextColor(ContextCompat.getColor(activity, R.color.text_primary))
            textSize = 14f
            setPadding(
                (24 * resources.displayMetrics.density).toInt(),
                (16 * resources.displayMetrics.density).toInt(),
                (24 * resources.displayMetrics.density).toInt(),
                (8 * resources.displayMetrics.density).toInt(),
            )
            gravity = Gravity.START
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
        }

        AlertDialog.Builder(activity)
            .setTitle("Podmínky používání")
            .setView(messageView)
            .setCancelable(false)
            .setPositiveButton("OK") { _, _ ->
                prefs.edit().putBoolean(key, true).apply()
                onReady()
            }
            .show()
    }

    companion object {
        private const val TAG = "AppBootstrap"
    }
}
