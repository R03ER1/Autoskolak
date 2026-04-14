package cz.autokolk.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import cz.autokolk.ui.screens.AlexScreen
import cz.autokolk.ui.screens.HomeScreen
import cz.autokolk.ui.screens.onboarding.OnboardingScreen
import cz.autokolk.ui.screens.PracticeScreen
import cz.autokolk.ui.screens.QuizScreen
import cz.autokolk.ui.screens.ReadingLessonScreen
import cz.autokolk.ui.screens.ResultsScreen
import cz.autokolk.ui.screens.SettingsComposeScreen
import cz.autokolk.ui.screens.SplashScreen
import cz.autokolk.ui.screens.TestResultsScreen
import cz.autokolk.ui.screens.TestScreen
import cz.autokolk.ui.screens.TestStatsScreen

@Composable
fun AutokolkNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Route.Splash.route,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable(Route.Splash.route) { SplashScreen(navController) }
        composable(Route.Onboarding.route) { OnboardingScreen(navController) }
        composable(Route.Home.route) { HomeScreen(navController) }
        composable(Route.Alex.route) { AlexScreen(navController) }
        composable(Route.TestStats.route) { TestStatsScreen(navController) }
        composable(
            route = Route.TestResults(0).route,
            arguments = listOf(navArgument("testId") { type = NavType.IntType }),
        ) { entry ->
            TestResultsScreen(navController, entry.arguments?.getInt("testId") ?: 0)
        }
        composable(Route.Test.route) { TestScreen(navController) }
        composable(Route.Practice.route) { PracticeScreen(navController) }
        composable(Route.Settings.route) { SettingsComposeScreen(navController) }
        composable(
            route = Route.Quiz(-1, false, -1).route,
            arguments = listOf(
                navArgument("lessonId") { type = NavType.IntType; defaultValue = -1 },
                navArgument("isTest") { type = NavType.BoolType; defaultValue = false },
                navArgument("categoryId") { type = NavType.IntType; defaultValue = -1 },
            ),
        ) { entry ->
            QuizScreen(
                navController = navController,
                lessonId = entry.arguments?.getInt("lessonId") ?: -1,
                isTest = entry.arguments?.getBoolean("isTest") ?: false,
                categoryId = entry.arguments?.getInt("categoryId") ?: -1,
            )
        }
        composable(
            route = Route.ReadingLesson(0).route,
            arguments = listOf(navArgument("lessonId") { type = NavType.IntType }),
        ) { entry ->
            ReadingLessonScreen(navController, entry.arguments?.getInt("lessonId") ?: 0)
        }
        composable(
            route = Route.Results(0, 0, 0).route,
            arguments = listOf(
                navArgument("lessonId") { type = NavType.IntType },
                navArgument("score") { type = NavType.IntType },
                navArgument("total") { type = NavType.IntType },
            ),
        ) { entry ->
            ResultsScreen(
                navController,
                entry.arguments?.getInt("lessonId") ?: 0,
                entry.arguments?.getInt("score") ?: 0,
                entry.arguments?.getInt("total") ?: 0,
            )
        }
    }
}
