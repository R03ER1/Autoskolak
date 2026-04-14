package cz.autokolk.ui.navigation

/**
 * Type-safe routes pro Compose Navigation.
 * Pro parametry použij [buildPath] u příslušné data třídy.
 */
sealed class Route(val route: String) {

    data object Splash : Route("splash")

    data object Onboarding : Route("onboarding")

    data object Home : Route("home")

    data object Alex : Route("alex")

    data object Test : Route("test")

    data object TestStats : Route("test/stats")

    data object Practice : Route("practice")

    data object Settings : Route("settings")

    data object Achievements : Route("achievements")

    data object Changelog : Route("changelog")

    data object Streak : Route("streak")

    data object AlexDeath : Route("alex/death")

    data class Quiz(
        val lessonId: Int = -1,
        val isTest: Boolean = false,
        val categoryId: Int = -1,
    ) : Route("quiz/{lessonId}/{isTest}/{categoryId}") {
        fun buildPath(): String = "quiz/$lessonId/$isTest/$categoryId"
    }

    data class ReadingLesson(val lessonId: Int) : Route("reading/{lessonId}") {
        fun buildPath(): String = "reading/$lessonId"
    }

    data class Results(
        val lessonId: Int,
        val score: Int,
        val total: Int,
    ) : Route("results/{lessonId}/{score}/{total}") {
        fun buildPath(): String = "results/$lessonId/$score/$total"
    }

    data class TestResults(val testId: Int) : Route("test/results/{testId}") {
        fun buildPath(): String = "test/results/$testId"
    }
}
