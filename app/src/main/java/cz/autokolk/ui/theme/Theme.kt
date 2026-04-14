package cz.autokolk.ui.theme

import android.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext

@Composable
fun AutokolkTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val activity = LocalContext.current as? ComponentActivity
    SideEffect {
        activity?.enableEdgeToEdge(
            statusBarStyle = if (darkTheme)
                SystemBarStyle.dark(Color.TRANSPARENT)
            else
                SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = if (darkTheme)
                SystemBarStyle.dark(Color.TRANSPARENT)
            else
                SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )
    }

    MaterialTheme(content = content)
}
