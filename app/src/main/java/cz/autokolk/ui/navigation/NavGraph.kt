package cz.autokolk.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import cz.autokolk.ui.screens.alex.AlexDeathScreen
import cz.autokolk.ui.screens.alex.AlexScreen
import cz.autokolk.ui.screens.home.HomeScreen
import cz.autokolk.ui.screens.quiz.QuizScreen
import cz.autokolk.ui.screens.reading.ReadingLessonComposeScreen
import cz.autokolk.ui.screens.results.ResultsComposeScreen
import cz.autokolk.ui.screens.streak.StreakScreen
import cz.autokolk.ui.screens.onboarding.OnboardingScreen
import cz.autokolk.ui.screens.splash.SplashScreen
import cz.autokolk.ui.screens.test.TestResultsScreen
import cz.autokolk.ui.screens.test.TestScreen
import cz.autokolk.ui.screens.test.TestStatsScreen

private const val DURATION_DEFAULT = 300
private const val DURATION_TAB = 150
private const val DURATION_MODAL = 350

private val tabRoutes = Route.mainTabs.map { it.route }.toSet()

private val modalRoutes = setOf(
    Route.Streak.route,
    Route.AlexDeath.route,
)

private fun tabEnter(): EnterTransition = fadeIn(tween(DURATION_TAB))
private fun tabExit(): ExitTransition = fadeOut(tween(DURATION_TAB))

private fun modalEnter(): EnterTransition =
    fadeIn(tween(DURATION_MODAL)) + slideInVertically(tween(DURATION_MODAL)) { it / 3 }

private fun modalExit(): ExitTransition =
    fadeOut(tween(DURATION_MODAL)) + slideOutVertically(tween(DURATION_MODAL)) { it / 3 }

// TODO: Shared element transitions (Compose Navigation 2.8+):
//  - Wrap with SharedTransitionLayout
//  - LessonNode (Home) → Quiz header icon
//  - Alex image (Alex page) → Alex image (AlexDeath)
//  - Achievement card → Achievement detail
@Composable
fun AutokolkNavGraph(
    navController: NavHostController,
    startDestination: String = Route.Splash.route,
    onHomeLessonBoundsChanged: (Rect) -> Unit = {},
    homeScrollToCurrentSignal: Int = 0,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { fadeIn(tween(DURATION_DEFAULT)) + slideInHorizontally(tween(DURATION_DEFAULT)) { it / 4 } },
        exitTransition = { fadeOut(tween(DURATION_DEFAULT)) + slideOutHorizontally(tween(DURATION_DEFAULT)) { -it / 4 } },
        popEnterTransition = { fadeIn(tween(DURATION_DEFAULT)) + slideInHorizontally(tween(DURATION_DEFAULT)) { -it / 4 } },
        popExitTransition = { fadeOut(tween(DURATION_DEFAULT)) + slideOutHorizontally(tween(DURATION_DEFAULT)) { it / 4 } },
    ) {

        // ── Splash ──────────────────────────────────────────────────────
        composable(Route.Splash.route) {
            SplashScreen(navController)
        }

        // ── Onboarding (placeholder) ────────────────────────────────────
        composable(Route.Onboarding.route) {
            OnboardingScreen(navController)
        }

        // ── Main tabs — fast fade, no slide ─────────────────────────────
        composable(
            route = Route.Home.route,
            enterTransition = { tabEnter() },
            exitTransition = {
                if (targetState.destination.route in tabRoutes) tabExit()
                else fadeOut(tween(DURATION_DEFAULT)) + slideOutHorizontally(tween(DURATION_DEFAULT)) { -it / 4 }
            },
            popEnterTransition = { tabEnter() },
            popExitTransition = { tabExit() },
        ) {
            HomeScreen(
                navController = navController,
                onCurrentLessonNodeBoundsChanged = onHomeLessonBoundsChanged,
                scrollToCurrentLessonSignal = homeScrollToCurrentSignal,
            )
        }

        composable(
            route = Route.Alex.route,
            enterTransition = { tabEnter() },
            exitTransition = {
                if (targetState.destination.route in tabRoutes) tabExit()
                else fadeOut(tween(DURATION_DEFAULT)) + slideOutHorizontally(tween(DURATION_DEFAULT)) { -it / 4 }
            },
            popEnterTransition = { tabEnter() },
            popExitTransition = { tabExit() },
        ) {
            AlexScreen(navController = navController)
        }

        composable(
            route = Route.Test.route,
            enterTransition = { tabEnter() },
            exitTransition = {
                if (targetState.destination.route in tabRoutes) tabExit()
                else fadeOut(tween(DURATION_DEFAULT)) + slideOutHorizontally(tween(DURATION_DEFAULT)) { -it / 4 }
            },
            popEnterTransition = { tabEnter() },
            popExitTransition = { tabExit() },
        ) {
            TestScreen(navController = navController)
        }

        composable(
            route = Route.Practice.route,
            enterTransition = { tabEnter() },
            exitTransition = {
                if (targetState.destination.route in tabRoutes) tabExit()
                else fadeOut(tween(DURATION_DEFAULT)) + slideOutHorizontally(tween(DURATION_DEFAULT)) { -it / 4 }
            },
            popEnterTransition = { tabEnter() },
            popExitTransition = { tabExit() },
        ) {
            PlaceholderScreen("Procvičování")
        }

        composable(
            route = Route.Settings.route,
            enterTransition = { tabEnter() },
            exitTransition = {
                if (targetState.destination.route in tabRoutes) tabExit()
                else fadeOut(tween(DURATION_DEFAULT)) + slideOutHorizontally(tween(DURATION_DEFAULT)) { -it / 4 }
            },
            popEnterTransition = { tabEnter() },
            popExitTransition = { tabExit() },
        ) {
            PlaceholderScreen("Nastavení")
        }

        // ── Detail screens ──────────────────────────────────────────────

        composable(
            route = Route.Quiz().route,
            arguments = Route.Quiz.arguments,
        ) { entry ->
            val args = entry.arguments!!
            QuizScreen(
                navController = navController,
                lessonId = args.getInt(Route.ARG_LESSON_ID),
                isTest = args.getBoolean(Route.ARG_IS_TEST),
                categoryId = args.getInt(Route.ARG_CATEGORY_ID),
                isReview = args.getBoolean(Route.ARG_IS_REVIEW),
            )
        }

        composable(
            route = Route.ReadingLesson(lessonId = 0).route,
            arguments = Route.ReadingLesson.arguments,
        ) { entry ->
            val args = entry.arguments!!
            ReadingLessonComposeScreen(
                navController = navController,
                lessonId = args.getInt(Route.ARG_LESSON_ID),
                isReview = args.getBoolean(Route.ARG_IS_REVIEW),
            )
        }

        composable(
            route = Route.Results(lessonId = 0, score = 0, total = 0, firstOfDay = false, pointsAwarded = 0).route,
            arguments = Route.Results.arguments,
            enterTransition = { modalEnter() },
            exitTransition = { modalExit() },
            popEnterTransition = { modalEnter() },
            popExitTransition = { modalExit() },
        ) { entry ->
            val args = entry.arguments!!
            ResultsComposeScreen(
                navController = navController,
                lessonId = args.getInt(Route.ARG_LESSON_ID),
                score = args.getInt(Route.ARG_SCORE),
                total = args.getInt(Route.ARG_TOTAL),
                firstOfDay = args.getInt(Route.ARG_FIRST_OF_DAY) != 0,
                pointsAwarded = args.getInt(Route.ARG_POINTS_AWARDED),
            )
        }

        composable(
            route = Route.TestResults(testId = 0L).route,
            arguments = Route.TestResults.arguments,
        ) { entry ->
            val testId = entry.arguments!!.getLong(Route.ARG_TEST_ID)
            TestResultsScreen(navController = navController, attemptId = testId)
        }

        // ── Secondary static screens ────────────────────────────────────

        composable(Route.TestStats.route) {
            TestStatsScreen(navController = navController)
        }

        composable(Route.Achievements.route) {
            PlaceholderScreen("Úspěchy")
        }

        composable(Route.Changelog.route) {
            PlaceholderScreen("Changelog")
        }

        // ── Modal screens — slide up from bottom ────────────────────────

        composable(
            route = Route.Streak.route,
            enterTransition = { modalEnter() },
            exitTransition = { modalExit() },
            popEnterTransition = { modalEnter() },
            popExitTransition = { modalExit() },
        ) {
            StreakScreen(navController = navController)
        }

        composable(
            route = Route.AlexDeath.route,
            enterTransition = { modalEnter() },
            exitTransition = { modalExit() },
            popEnterTransition = { modalEnter() },
            popExitTransition = { modalExit() },
        ) {
            AlexDeathScreen(navController = navController)
        }
    }
}

@Composable
private fun PlaceholderScreen(name: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(name, style = MaterialTheme.typography.headlineMedium)
    }
}
