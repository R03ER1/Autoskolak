package cz.autokolk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cz.autokolk.ui.AutokolkApp
import cz.autokolk.ui.theme.AutokolkTheme

/**
 * Hlavní launcher Activity — po dokončení [AppBootstrap] (consent, DFM, podmínky) zobrazí Compose UI.
 */
class ComposeMainActivity : ComponentActivity() {

    private var bootstrap: AppBootstrap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val noopUi = object : LoadingUi {
            override fun updateProgress(percent: Int?) {}
            override fun showError() {}
        }
        bootstrap = AppBootstrap(this, noopUi) {
            setContent {
                AutokolkTheme {
                    AutokolkApp()
                }
            }
        }
        bootstrap?.start()
    }

    override fun onDestroy() {
        bootstrap?.dispose()
        bootstrap = null
        super.onDestroy()
    }
}
