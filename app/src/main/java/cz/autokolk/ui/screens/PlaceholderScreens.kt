package cz.autokolk.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cz.autokolk.ui.navigation.Route
import cz.autokolk.ui.screens.onboarding.OnboardingPreferences

@Composable
fun SplashScreen(navController: NavHostController) {
    ScreenStub("Splash")
}

@Composable
fun HomeScreen(navController: NavHostController) {
    ScreenStub("Home")
}

@Composable
fun AlexScreen(navController: NavHostController) {
    ScreenStub("Alex")
}

@Composable
fun TestScreen(navController: NavHostController) {
    ScreenStub("Test")
}

@Composable
fun TestStatsScreen(navController: NavHostController) {
    ScreenStub("Test stats")
}

@Composable
fun PracticeScreen(navController: NavHostController) {
    ScreenStub("Practice")
}

@Composable
fun SettingsComposeScreen(navController: NavHostController) {
    val context = LocalContext.current
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Text("Nastavení (Compose)")
        TextButton(
            onClick = {
                OnboardingPreferences.clearCompletedFlagForReplay(context)
                navController.navigate(Route.Onboarding.route) {
                    launchSingleTop = true
                }
            },
        ) {
            Text("Znovu zobrazit onboarding")
        }
    }
}

@Composable
fun QuizScreen(
    navController: NavHostController,
    lessonId: Int,
    isTest: Boolean,
    categoryId: Int,
) {
    ScreenStub("Quiz $lessonId test=$isTest cat=$categoryId")
}

@Composable
fun ReadingLessonScreen(navController: NavHostController, lessonId: Int) {
    ScreenStub("Reading $lessonId")
}

@Composable
fun ResultsScreen(
    navController: NavHostController,
    lessonId: Int,
    score: Int,
    total: Int,
) {
    ScreenStub("Results $lessonId $score/$total")
}

@Composable
fun TestResultsScreen(navController: NavHostController, testId: Int) {
    ScreenStub("Test results $testId")
}

@Composable
private fun ScreenStub(title: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(title)
    }
}
