package cz.autokolk.ui.screens.changelog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cz.autokolk.data.changelog.ChangelogParser
import cz.autokolk.ui.components.animation.AnimatedBackground
import cz.autokolk.ui.components.glass.GlassCard
import cz.autokolk.ui.theme.accentCyanText

@Composable
fun ChangelogScreen(@Suppress("UNUSED_PARAMETER") navController: NavHostController) {
    val context = LocalContext.current
    val entries = remember {
        runCatching {
            val text = context.assets.open("CHANGELOG.md").bufferedReader().use { it.readText() }
            ChangelogParser.parse(text)
                .filter { it.version != "Unreleased" && it.changes.isNotEmpty() }
        }.getOrElse { emptyList() }
    }

    AnimatedBackground(Modifier.fillMaxSize()) {
        LazyColumn(
            Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp),
        ) {
            item {
                Text(
                    "Historie změn",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 16.dp),
                )
            }
            items(entries, key = { it.version }) { entry ->
                GlassCard(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            "v${entry.version}",
                            style = MaterialTheme.typography.titleMedium,
                            color = accentCyanText(),
                        )
                        if (entry.date != null) {
                            Text(
                                entry.date,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        entry.changes.forEach { change ->
                            Text(
                                "• $change",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(vertical = 2.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}
