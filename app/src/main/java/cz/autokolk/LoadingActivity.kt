package cz.autokolk

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.activity.OnBackPressedCallback
import com.google.android.material.button.MaterialButton

/**
 * Fallback / legacy obrazovka stahování DFM modulu. Hlavní start aplikace je [ComposeMainActivity].
 */
class LoadingActivity : AutokolkActivity() {

    private lateinit var statusText: TextView
    private lateinit var detailText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var retryButton: MaterialButton

    private var bootstrap: AppBootstrap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Block leaving while we are preparing required content
                }
            },
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
            bootstrap?.retryInstall()
        }

        val ui = object : LoadingUi {
            override fun updateProgress(percent: Int?) {
                this@LoadingActivity.updateProgress(percent)
            }

            override fun showError() {
                this@LoadingActivity.showError()
            }
        }
        bootstrap = AppBootstrap(this, ui) { navigateToHome() }
        bootstrap?.start()
    }

    override fun onDestroy() {
        bootstrap?.dispose()
        bootstrap = null
        super.onDestroy()
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
        val intent = Intent(this, ComposeMainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}
