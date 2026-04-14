package cz.autokolk.ui.screens.practice

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cz.autokolk.LessonProgress
import cz.autokolk.ui.components.glass.GlassCard
import cz.autokolk.ui.navigation.Route
import cz.autokolk.ui.theme.TextPrimary
import cz.autokolk.ui.theme.TextSecondary

@Composable
fun PracticeScreen(navController: NavHostController) {
    val context = LocalContext.current
    val groups = remember { LessonProgress(context).getCategoryGroups() }

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            Text("Procvičování", style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
            Text(
                "Vyber kategorii — náhodný výběr otázek.",
                color = TextSecondary,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        itemsIndexed(groups) { index, group ->
            Box(
                Modifier
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate(
                            Route.Quiz(-1, false, index, false).buildPath(),
                        )
                    },
            ) {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(group.category, color = TextPrimary, style = MaterialTheme.typography.titleMedium)
                        Text(
                            "${group.subcategories.size} podkategorií",
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
        }
    }
}
