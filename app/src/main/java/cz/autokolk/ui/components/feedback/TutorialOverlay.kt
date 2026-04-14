package cz.autokolk.ui.components.feedback

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cz.autokolk.ui.theme.TextPrimary

private const val PREFS = "lesson_progress"
private const val KEY_HOME_TUTORIAL = "compose_home_tutorial_done"

/**
 * Jednoduchý tutoriál na Home (první spuštění Compose cesty).
 */
@Composable
fun TutorialOverlay(
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = !isHomeTutorialDone(context)
    }
    if (!visible) return
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f)),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier
                .padding(24.dp)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f), MaterialTheme.shapes.large)
                .padding(20.dp),
        ) {
            Text(
                text = "Tady je tvoje cesta lekcí. Klepni na kolečko pro detail a start.",
                color = TextPrimary,
                style = MaterialTheme.typography.bodyLarge,
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    markHomeTutorialDone(context)
                    visible = false
                    onDismiss()
                },
            ) {
                Text("Rozumím")
            }
        }
    }
}

private fun isHomeTutorialDone(context: Context): Boolean {
    return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        .getBoolean(KEY_HOME_TUTORIAL, false)
}

private fun markHomeTutorialDone(context: Context) {
    context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        .edit()
        .putBoolean(KEY_HOME_TUTORIAL, true)
        .apply()
}
