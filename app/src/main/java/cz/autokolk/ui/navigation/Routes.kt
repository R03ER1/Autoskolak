package cz.autokolk.ui.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Route(val route: String) {

    // region Static routes

    data object Splash : Route("splash")
    data object Onboarding : Route("onboarding")
    data object Home : Route("home")
    data object Alex : Route("alex")
    data object Test : Route("test")
    data object TestStats : Route("test_stats")
    data object Practice : Route("practice")
    data object Settings : Route("settings")
    data object Achievements : Route("achievements")
    data object Changelog : Route("changelog")
    data object Streak : Route("streak")
    data object AlexDeath : Route("alex_death")

    // endregion

    // region Parametric routes

    data class Quiz(
        val lessonId: Int = -1,
        val isTest: Boolean = false,
        val categoryId: Int = -1,
        val isReview: Boolean = false,
    ) : Route("quiz/{$ARG_LESSON_ID}/{$ARG_IS_TEST}/{$ARG_CATEGORY_ID}/{$ARG_IS_REVIEW}") {
        fun buildPath(): String = "quiz/$lessonId/$isTest/$categoryId/$isReview"

        companion object {
            val arguments: List<NamedNavArgument> = listOf(
                navArgument(ARG_LESSON_ID) { type = NavType.IntType; defaultValue = -1 },
                navArgument(ARG_IS_TEST) { type = NavType.BoolType; defaultValue = false },
                navArgument(ARG_CATEGORY_ID) { type = NavType.IntType; defaultValue = -1 },
                navArgument(ARG_IS_REVIEW) { type = NavType.BoolType; defaultValue = false },
            )
        }
    }

    data class ReadingLesson(
        val lessonId: Int,
        val isReview: Boolean = false,
    ) : Route("reading/{$ARG_LESSON_ID}/{$ARG_IS_REVIEW}") {
        fun buildPath(): String = "reading/$lessonId/$isReview"

        companion object {
            val arguments: List<NamedNavArgument> = listOf(
                navArgument(ARG_LESSON_ID) { type = NavType.IntType },
                navArgument(ARG_IS_REVIEW) { type = NavType.BoolType; defaultValue = false },
            )
        }
    }

    data class Results(
        val lessonId: Int,
        val score: Int,
        val total: Int,
    ) : Route("results/{$ARG_LESSON_ID}/{$ARG_SCORE}/{$ARG_TOTAL}") {
        fun buildPath(): String = "results/$lessonId/$score/$total"

        companion object {
            val arguments: List<NamedNavArgument> = listOf(
                navArgument(ARG_LESSON_ID) { type = NavType.IntType },
                navArgument(ARG_SCORE) { type = NavType.IntType },
                navArgument(ARG_TOTAL) { type = NavType.IntType },
            )
        }
    }

    data class TestResults(val testId: Int) : Route("test_results/{$ARG_TEST_ID}") {
        fun buildPath(): String = "test_results/$testId"

        companion object {
            val arguments: List<NamedNavArgument> = listOf(
                navArgument(ARG_TEST_ID) { type = NavType.IntType },
            )
        }
    }

    // endregion

    companion object {
        // Argument name constants
        const val ARG_LESSON_ID = "lessonId"
        const val ARG_IS_TEST = "isTest"
        const val ARG_CATEGORY_ID = "categoryId"
        const val ARG_IS_REVIEW = "isReview"
        const val ARG_SCORE = "score"
        const val ARG_TOTAL = "total"
        const val ARG_TEST_ID = "testId"

        /** The five main bottom-bar tabs in display order. */
        val mainTabs: List<Route> = listOf(Home, Alex, Test, Practice, Settings)
    }
}
