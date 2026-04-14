package cz.autokolk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cz.autokolk.ui.AutokolkApp
import cz.autokolk.ui.theme.AutokolkTheme

class ComposeMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AutokolkTheme {
                AutokolkApp()
            }
        }
    }
}
