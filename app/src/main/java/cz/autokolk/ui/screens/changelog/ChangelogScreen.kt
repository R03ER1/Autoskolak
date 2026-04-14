package cz.autokolk.ui.screens.changelog

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cz.autokolk.ui.theme.TextPrimary
import java.io.BufferedReader
import java.io.InputStreamReader

@Composable
fun ChangelogScreen(navController: NavHostController) {
    val context = LocalContext.current
    val text = remember {
        try {
            context.assets.open("CHANGELOG.md").use { stream ->
                BufferedReader(InputStreamReader(stream, Charsets.UTF_8)).readText()
            }
        } catch (_: Exception) {
            "Changelog se nepodařilo načíst."
        }
    }
    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        item {
            Text("Historie změn", style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
        }
        item {
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = TextPrimary,
                modifier = Modifier.padding(top = 12.dp),
            )
        }
    }
}
