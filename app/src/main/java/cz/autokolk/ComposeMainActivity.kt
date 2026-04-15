package cz.autokolk

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import cz.autokolk.ui.AutokolkApp
import cz.autokolk.ui.navigation.ComposeNavIntent
import cz.autokolk.ui.theme.AutokolkTheme
import cz.autokolk.ui.theme.LocalThemeController
import cz.autokolk.ui.theme.ThemeController
import cz.autokolk.ui.theme.readThemeMode
import cz.autokolk.ui.theme.writeThemeMode
import kotlinx.coroutines.flow.MutableStateFlow

class ComposeMainActivity : FragmentActivity() {
    private val openTabExtra = MutableStateFlow<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        openTabExtra.value = intent.getStringExtra(ComposeNavIntent.EXTRA_OPEN_TAB)
        enableEdgeToEdge()
        setContent {
            val initialOpenTab by openTabExtra.collectAsState()
            val ctx = LocalContext.current
            var themeMode by remember { mutableStateOf(readThemeMode(ctx)) }
            val controller = ThemeController(
                mode = themeMode,
                setMode = { m ->
                    themeMode = m
                    writeThemeMode(ctx, m)
                },
            )
            CompositionLocalProvider(LocalThemeController provides controller) {
                AutokolkTheme(themeMode = themeMode) {
                    AutokolkApp(
                        initialOpenTab = initialOpenTab,
                        onConsumeInitialTab = { openTabExtra.value = null },
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        openTabExtra.value = intent.getStringExtra(ComposeNavIntent.EXTRA_OPEN_TAB)
    }
}
