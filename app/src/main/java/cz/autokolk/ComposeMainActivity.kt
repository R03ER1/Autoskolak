package cz.autokolk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cz.autokolk.ui.AutokolkApp
import cz.autokolk.ui.theme.AutokolkTheme
import cz.autokolk.ui.theme.ThemePreferences

class ComposeMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themePreferences = remember { ThemePreferences(this) }
            var themeMode by remember { mutableStateOf(themePreferences.themeMode) }
            AutokolkTheme(themeMode = themeMode) {
                AutokolkApp()
            }
        }
    }
}
