package cz.autokolk

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cz.autokolk.ui.AutokolkApp
import cz.autokolk.ui.navigation.ComposeNavIntent
import cz.autokolk.ui.theme.AutokolkTheme
import kotlinx.coroutines.flow.MutableStateFlow

class ComposeMainActivity : ComponentActivity() {
    private val openTabExtra = MutableStateFlow<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        openTabExtra.value = intent.getStringExtra(ComposeNavIntent.EXTRA_OPEN_TAB)
        enableEdgeToEdge()
        setContent {
            val initialOpenTab by openTabExtra.collectAsState()
            AutokolkTheme {
                AutokolkApp(
                    initialOpenTab = initialOpenTab,
                    onConsumeInitialTab = { openTabExtra.value = null },
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        openTabExtra.value = intent.getStringExtra(ComposeNavIntent.EXTRA_OPEN_TAB)
    }
}
