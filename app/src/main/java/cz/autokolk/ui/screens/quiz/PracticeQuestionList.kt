package cz.autokolk.ui.screens.quiz

import cz.autokolk.LessonProgress
import cz.autokolk.Question
import cz.autokolk.ui.navigation.Route
import cz.autokolk.ui.screens.practice.PracticeMode

/**
 * Sestavení seznamu otázek pro procvičování — stejná logika jako [cz.autokolk.autokolk.MainActivity.loadQuestions]
 * pro větev s vyplněnou kategorií.
 */
object PracticeQuestionList {

    fun build(
        lessonProgress: LessonProgress,
        categoryKey: String,
        practiceMode: Int,
        subcategoryFilter: String?,
        focusQuestionId: String?,
    ): List<Question> {
        val sub = subcategoryFilter?.trim()?.takeIf {
            it.isNotEmpty() && !it.equals(Route.PracticeQuiz.ALL_SUB, ignoreCase = true)
        }
        val isUserMistakes = categoryKey.equals(LessonProgress.CATEGORY_USER_MISTAKES, ignoreCase = true)

        val rawList: List<Question> = when {
            isUserMistakes -> buildUserMistakesList(lessonProgress, practiceMode)
            else -> buildCategoryList(lessonProgress, categoryKey, practiceMode, sub)
        }

        val focused = applyFocus(lessonProgress, rawList, focusQuestionId)
        val ordered = if (isUserMistakes || practiceMode != PracticeMode.ALL) {
            focused
        } else {
            focused.shuffled()
        }
        return ordered.map { it.copy(userAnswer = null) }
    }

    private fun buildUserMistakesList(lessonProgress: LessonProgress, practiceMode: Int): List<Question> {
        val cat = LessonProgress.CATEGORY_USER_MISTAKES
        val (correctIds, wrongIds) = lessonProgress.getPracticeStatus(cat)
        val wrongOnly = lessonProgress.getQuestionsForIds(wrongIds).sortedWith(
            compareByDescending<Question> { lessonProgress.getMistakeConsecutiveCount(it.id) }
                .thenBy { it.id.toIntOrNull() ?: 0 },
        )
        val correctOnly = lessonProgress.getQuestionsForIds(correctIds)
        val unanswered = emptyList<Question>()
        return when (practiceMode) {
            PracticeMode.WRONG -> wrongOnly
            PracticeMode.CORRECT -> correctOnly
            PracticeMode.UNANSWERED -> unanswered
            else -> when {
                wrongOnly.isNotEmpty() -> wrongOnly
                correctOnly.isNotEmpty() -> correctOnly
                else -> emptyList()
            }
        }
    }

    private fun buildCategoryList(
        lessonProgress: LessonProgress,
        categoryKey: String,
        practiceMode: Int,
        sub: String?,
    ): List<Question> {
        val all = lessonProgress.getQuestionsForCategory(categoryKey, sub)
        val (correctIds, wrongIds) = lessonProgress.getPracticeStatus(categoryKey)
        val unanswered = all.filter { q -> q.id !in correctIds && q.id !in wrongIds }
        val wrongOnly = all.filter { q -> q.id in wrongIds }
        val correctOnly = all.filter { q -> q.id in correctIds }
        return when (practiceMode) {
            PracticeMode.WRONG -> wrongOnly.ifEmpty { all }
            PracticeMode.CORRECT -> correctOnly.ifEmpty { all }
            PracticeMode.UNANSWERED -> unanswered.ifEmpty { all }
            else -> when {
                unanswered.isNotEmpty() -> unanswered
                wrongOnly.isNotEmpty() -> wrongOnly
                else -> all
            }
        }
    }

    private fun applyFocus(
        lessonProgress: LessonProgress,
        list: List<Question>,
        focusQuestionId: String?,
    ): List<Question> {
        val fid = focusQuestionId?.trim()?.takeIf {
            it.isNotEmpty() && !it.equals(Route.PracticeQuiz.FOCUS_NONE, ignoreCase = true)
        } ?: return list
        list.firstOrNull { it.id.trim() == fid }?.let { return listOf(it) }
        val fromIds = lessonProgress.getQuestionsForIds(setOf(fid))
        return fromIds.takeIf { it.isNotEmpty() } ?: list
    }
}
