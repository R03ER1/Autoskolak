package cz.autokolk.ui.screens.gamification

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cz.autokolk.LessonProgress
import cz.autokolk.ui.components.animation.AnimatedBackground
import cz.autokolk.ui.components.buttons.PrimaryGradientButton
import cz.autokolk.ui.navigation.Route
import cz.autokolk.ui.navigation.navigateToTab
import cz.autokolk.ui.theme.TextPrimary
import cz.autokolk.ui.theme.TextSecondary

@Composable
fun ReviseMistakesScreen(navController: NavHostController) {
    val context = LocalContext.current
    val lp = LessonProgress(context)
    val (ok, wrong) = lp.getPracticeStatus(LessonProgress.CATEGORY_USER_MISTAKES)
    val n = wrong.size

    AnimatedBackground(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.align(Alignment.Start)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zpět", tint = TextPrimary)
            }
            Text("Revize chyb", style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Otázek k opakování: $n (správně zodpovězených v této kategorii: ${ok.size})",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
            )
            Spacer(Modifier.height(24.dp))
            PrimaryGradientButton(
                text = if (n == 0) "Žádné chyby k revizi" else "Otevřít procvičování (tvoje chyby)",
                onClick = {
                    if (n == 0) {
                        navController.navigateToTab(Route.Practice)
                    } else {
                        navController.navigate(
                            Route.PracticeQuiz(
                                categoryKey = LessonProgress.CATEGORY_USER_MISTAKES,
                                practiceMode = 0,
                            ).buildPath(),
                        ) {
                            popUpTo(Route.Settings.route) { inclusive = false }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
