package cz.autokolk.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import cz.autokolk.ui.screens.achievements.AchievementsScreen
import cz.autokolk.ui.screens.alex.AlexDeathScreen
import cz.autokolk.ui.screens.alex.AlexScreen
import cz.autokolk.ui.screens.changelog.ChangelogScreen
import cz.autokolk.ui.screens.home.HomeScreen
import cz.autokolk.ui.screens.onboarding.OnboardingScreen
import cz.autokolk.ui.screens.practice.PracticeScreen
import cz.autokolk.ui.screens.quiz.QuizScreen
import cz.autokolk.ui.screens.reading.ReadingLessonComposeScreen
import cz.autokolk.ui.screens.results.ResultsComposeScreen
import cz.autokolk.ui.screens.settings.SettingsComposeScreen
import cz.autokolk.ui.screens.splash.SplashScreen
import cz.autokolk.ui.screens.streak.StreakCelebrationScreen
import cz.autokolk.ui.screens.test.TestHubScreen
import cz.autokolk.ui.screens.test.TestResultsDetailScreen
import cz.autokolk.ui.screens.test.TestStatsScreen

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
        composable(Route.AlexDeath.route) { AlexDeathScreen(navController) }
        composable(Route.TestStats.route) { TestStatsScreen(navController) }
        composable(
            route = Route.TestResults(0).route,
            arguments = listOf(navArgument("testId") { type = NavType.IntType }),
        ) { entry ->
            TestResultsDetailScreen(navController, entry.arguments?.getInt("testId") ?: 0)
        }
        composable(Route.Test.route) { TestHubScreen(navController) }
        composable(Route.Practice.route) { PracticeScreen(navController) }
        composable(Route.Settings.route) { SettingsComposeScreen(navController) }
        composable(Route.Achievements.route) { AchievementsScreen(navController) }
        composable(Route.Changelog.route) { ChangelogScreen(navController) }
        composable(
            route = Route.StreakCelebration(0).route,
            arguments = listOf(navArgument("streak") { type = NavType.IntType }),
        ) { entry ->
            StreakCelebrationScreen(
                navController,
                entry.arguments?.getInt("streak") ?: 0,
            )
        }
        composable(
            route = Route.Quiz(-1, false, -1, false).route,
            arguments = listOf(
                navArgument("lessonId") { type = NavType.IntType; defaultValue = -1 },
                navArgument("isTest") { type = NavType.BoolType; defaultValue = false },
                navArgument("categoryId") { type = NavType.IntType; defaultValue = -1 },
                navArgument("isReview") { type = NavType.BoolType; defaultValue = false },
            ),
        ) { entry ->
            QuizScreen(
                navController = navController,
                lessonId = entry.arguments?.getInt("lessonId") ?: -1,
                isTest = entry.arguments?.getBoolean("isTest") ?: false,
                categoryId = entry.arguments?.getInt("categoryId") ?: -1,
                isReview = entry.arguments?.getBoolean("isReview") ?: false,
            )
        }
        composable(
            route = Route.ReadingLesson(0, false).route,
            arguments = listOf(
                navArgument("lessonId") { type = NavType.IntType },
                navArgument("isReview") { type = NavType.BoolType; defaultValue = false },
            ),
        ) { entry ->
            ReadingLessonComposeScreen(
                navController = navController,
                lessonId = entry.arguments?.getInt("lessonId") ?: 0,
                isReview = entry.arguments?.getBoolean("isReview") ?: false,
            )
        }
        composable(
            route = Route.Results(0, 0, 0).route,
            arguments = listOf(
                navArgument("lessonId") { type = NavType.IntType },
                navArgument("score") { type = NavType.IntType },
                navArgument("total") { type = NavType.IntType },
            ),
        ) { entry ->
            ResultsComposeScreen(
                navController,
                entry.arguments?.getInt("lessonId") ?: 0,
                entry.arguments?.getInt("score") ?: 0,
                entry.arguments?.getInt("total") ?: 0,
            )
        }
    }
}
