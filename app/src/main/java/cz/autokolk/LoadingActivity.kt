package cz.autokolk

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import androidx.activity.OnBackPressedCallback

class LoadingActivity : AutokolkActivity() {

    private lateinit var splitInstallManager: SplitInstallManager
    private val imageModuleName = "imageassets"

    private lateinit var statusText: TextView
    private lateinit var detailText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var retryButton: MaterialButton

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
                    updateProgress(progress)
                } else {
                    updateProgress(null)
                }
            }
            SplitInstallSessionStatus.INSTALLED -> {
                Log.d("LoadingActivity", "Image module '$imageModuleName' installed")
                ensureTermsAcceptedThenNavigate()
            }
            SplitInstallSessionStatus.INSTALLING,
            SplitInstallSessionStatus.PENDING,
            SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION,
            SplitInstallSessionStatus.CANCELING,
            SplitInstallSessionStatus.CANCELED,
            SplitInstallSessionStatus.UNKNOWN -> {
                // For these states we keep showing the loading UI
                updateProgress(null)
            }
            SplitInstallSessionStatus.FAILED -> {
                Log.e("LoadingActivity", "Image module '$imageModuleName' installation failed: ${state.errorCode()}")
                showError()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Block leaving while we are preparing required content
                }
            }
        )
        setContentView(R.layout.activity_loading)

        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        statusText = findViewById(R.id.loadingStatusText)
        detailText = findViewById(R.id.loadingDetailText)
        progressBar = findViewById(R.id.loadingProgressBar)
        retryButton = findViewById(R.id.loadingRetryButton)

        retryButton.setOnClickListener {
            retryButton.visibility = View.GONE
            detailText.text = getString(R.string.loading_detail_initial)
            startOrCheckInstall()
        }

        splitInstallManager = SplitInstallManagerFactory.create(this)
        splitInstallManager.registerListener(stateListener)

        startOrCheckInstall()
    }

    override fun onDestroy() {
        if (::splitInstallManager.isInitialized) {
            try {
                splitInstallManager.unregisterListener(stateListener)
            } catch (_: Throwable) {
            }
        }
        super.onDestroy()
    }

    private fun startOrCheckInstall() {
        // If the image module is already installed, we can proceed immediately.
        if (splitInstallManager.installedModules.contains(imageModuleName)) {
            Log.d("LoadingActivity", "Image module already installed, navigating to HomeActivity")
            ensureTermsAcceptedThenNavigate()
            return
        }

        updateProgress(null)

        try {
            val request = SplitInstallRequest.newBuilder()
                .addModule(imageModuleName)
                .build()

            Log.d("LoadingActivity", "Requesting install of dynamic feature module: $imageModuleName")
            splitInstallManager
                .startInstall(request)
                .addOnSuccessListener { sessionId ->
                    Log.d("LoadingActivity", "Image module install started (sessionId=$sessionId)")
                }
                .addOnFailureListener { exception ->
                    Log.e("LoadingActivity", "Failed to request install for module '$imageModuleName'", exception)
                    showError()
                }
        } catch (e: Exception) {
            Log.e("LoadingActivity", "Error while requesting image module installation", e)
            showError()
        }
    }

    private fun updateProgress(percent: Int?) {
        statusText.text = getString(R.string.loading_status_title)
        if (percent != null) {
            detailText.text = getString(R.string.loading_detail_with_percent, percent)
            progressBar.isIndeterminate = false
            progressBar.progress = percent
        } else {
            detailText.text = getString(R.string.loading_detail_initial)
            progressBar.isIndeterminate = true
        }
    }

    private fun showError() {
        progressBar.isIndeterminate = false
        progressBar.progress = 0
        statusText.text = getString(R.string.loading_status_error)
        detailText.text = getString(R.string.loading_detail_error)
        retryButton.visibility = View.VISIBLE
    }

    private fun ensureTermsAcceptedThenNavigate() {
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val key = "terms_accepted_v1"
        if (prefs.getBoolean(key, false)) {
            navigateToHome()
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
                            Uri.parse("https://sites.google.com/view/dos-pachos-studio/zásady-ochrany-soukromí")
                        )
                        startActivity(intent)
                    } catch (_: Throwable) {
                    }
                }
            }
            spannable.setSpan(clickableSpan, start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
            val linkColor = ContextCompat.getColor(this, R.color.text_primary)
            spannable.setSpan(
                ForegroundColorSpan(linkColor),
                start,
                end,
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannable.setSpan(
                UnderlineSpan(),
                start,
                end,
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        val messageView = TextView(this).apply {
            text = spannable
            movementMethod = LinkMovementMethod.getInstance()
            setTextColor(ContextCompat.getColor(this@LoadingActivity, R.color.text_primary))
            textSize = 14f
            setPadding(
                (24 * resources.displayMetrics.density).toInt(),
                (16 * resources.displayMetrics.density).toInt(),
                (24 * resources.displayMetrics.density).toInt(),
                (8 * resources.displayMetrics.density).toInt()
            )
            gravity = Gravity.START
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        AlertDialog.Builder(this)
            .setTitle("Podmínky používání")
            .setView(messageView)
            .setCancelable(false)
            .setPositiveButton("OK") { _, _ ->
                prefs.edit().putBoolean(key, true).apply()
                navigateToHome()
            }
            .show()
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}

