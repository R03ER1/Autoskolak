package cz.autokolk.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

@Composable
fun SplashScreen(navController: NavHostController) {
    ScreenStub("Splash")
}

@Composable
fun OnboardingScreen(navController: NavHostController) {
    ScreenStub("Onboarding")
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
    ScreenStub("Settings")
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
