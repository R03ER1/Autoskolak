package cz.autokolk.ui.navigation

import android.net.Uri
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import java.nio.charset.StandardCharsets

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
        val firstOfDay: Boolean = false,
        val pointsAwarded: Int = 0,
        val fromPractice: Boolean = false,
        val replayCategoryEncoded: String = NO_REPLAY,
        val replayPracticeMode: Int = 0,
        val replaySubEncoded: String = NO_REPLAY,
        val replayFocusEncoded: String = NO_REPLAY,
    ) : Route(
        "results/{$ARG_LESSON_ID}/{$ARG_SCORE}/{$ARG_TOTAL}/{$ARG_FIRST_OF_DAY}/{$ARG_POINTS_AWARDED}/{$ARG_FROM_PRACTICE}/{$ARG_REPLAY_CATEGORY_ENC}/{$ARG_REPLAY_MODE}/{$ARG_REPLAY_SUB_ENC}/{$ARG_REPLAY_FOCUS_ENC}",
    ) {
        fun buildPath(): String =
            "results/$lessonId/$score/$total/${if (firstOfDay) 1 else 0}/$pointsAwarded/" +
                "${if (fromPractice) 1 else 0}/$replayCategoryEncoded/$replayPracticeMode/$replaySubEncoded/$replayFocusEncoded"

        companion object {
            const val NO_REPLAY = "none"

            val arguments: List<NamedNavArgument> = listOf(
                navArgument(ARG_LESSON_ID) { type = NavType.IntType },
                navArgument(ARG_SCORE) { type = NavType.IntType },
                navArgument(ARG_TOTAL) { type = NavType.IntType },
                navArgument(ARG_FIRST_OF_DAY) { type = NavType.IntType; defaultValue = 0 },
                navArgument(ARG_POINTS_AWARDED) { type = NavType.IntType; defaultValue = 0 },
                navArgument(ARG_FROM_PRACTICE) { type = NavType.IntType; defaultValue = 0 },
                navArgument(ARG_REPLAY_CATEGORY_ENC) {
                    type = NavType.StringType
                    defaultValue = NO_REPLAY
                },
                navArgument(ARG_REPLAY_MODE) { type = NavType.IntType; defaultValue = 0 },
                navArgument(ARG_REPLAY_SUB_ENC) {
                    type = NavType.StringType
                    defaultValue = NO_REPLAY
                },
                navArgument(ARG_REPLAY_FOCUS_ENC) {
                    type = NavType.StringType
                    defaultValue = NO_REPLAY
                },
            )

            fun encodeReplayPart(raw: String): String =
                Uri.encode(raw, StandardCharsets.UTF_8.name())
        }
    }

    /**
     * Procvičovací kvíz: kategorie (kód z CSV / [LessonProgress.CATEGORY_USER_MISTAKES]),
     * režim [PracticeMode], podkategorie "ALL" nebo kód podkategorie, volitelně jedno ID otázky.
     */
    data class PracticeQuiz(
        val categoryKey: String,
        val practiceMode: Int,
        val subcategoryKey: String = ALL_SUB,
        val focusQuestionId: String = FOCUS_NONE,
    ) : Route("practice_quiz/{$ARG_PRACTICE_CAT}/{$ARG_PRACTICE_MODE}/{$ARG_PRACTICE_SUB}/{$ARG_PRACTICE_FOCUS}") {
        fun buildPath(): String {
            val c = Uri.encode(categoryKey, StandardCharsets.UTF_8.name())
            val s = Uri.encode(subcategoryKey, StandardCharsets.UTF_8.name())
            val f = Uri.encode(focusQuestionId, StandardCharsets.UTF_8.name())
            return "practice_quiz/$c/$practiceMode/$s/$f"
        }

        companion object {
            const val ALL_SUB = "ALL"
            const val FOCUS_NONE = "none"

            val arguments: List<NamedNavArgument> = listOf(
                navArgument(ARG_PRACTICE_CAT) { type = NavType.StringType },
                navArgument(ARG_PRACTICE_MODE) { type = NavType.IntType; defaultValue = 0 },
                navArgument(ARG_PRACTICE_SUB) {
                    type = NavType.StringType
                    defaultValue = ALL_SUB
                },
                navArgument(ARG_PRACTICE_FOCUS) {
                    type = NavType.StringType
                    defaultValue = FOCUS_NONE
                },
            )
        }
    }

    data class TestResults(val testId: Long) : Route("test_results/{$ARG_TEST_ID}") {
        fun buildPath(): String = "test_results/$testId"

        companion object {
            val arguments: List<NamedNavArgument> = listOf(
                navArgument(ARG_TEST_ID) { type = NavType.LongType },
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
        const val ARG_FIRST_OF_DAY = "firstOfDay"
        const val ARG_POINTS_AWARDED = "pointsAwarded"
        const val ARG_TEST_ID = "testId"
        const val ARG_FROM_PRACTICE = "fromPractice"
        const val ARG_REPLAY_CATEGORY_ENC = "replayCategoryEnc"
        const val ARG_REPLAY_MODE = "replayMode"
        const val ARG_REPLAY_SUB_ENC = "replaySubEnc"
        const val ARG_REPLAY_FOCUS_ENC = "replayFocusEnc"
        const val ARG_PRACTICE_CAT = "practiceCategory"
        const val ARG_PRACTICE_MODE = "practiceMode"
        const val ARG_PRACTICE_SUB = "practiceSub"
        const val ARG_PRACTICE_FOCUS = "practiceFocus"

        /** The five main bottom-bar tabs in display order. */
        val mainTabs: List<Route> = listOf(Home, Alex, Test, Practice, Settings)
    }
}
