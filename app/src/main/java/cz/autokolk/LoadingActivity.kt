package cz.autokolk

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus

class LoadingActivity : AppCompatActivity() {

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
                navigateToHome()
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

    override fun onBackPressed() {
        // Block leaving while we are preparing required content
    }

    private fun startOrCheckInstall() {
        // If the image module is already installed, we can proceed immediately.
        if (splitInstallManager.installedModules.contains(imageModuleName)) {
            Log.d("LoadingActivity", "Image module already installed, navigating to HomeActivity")
            navigateToHome()
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

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}

