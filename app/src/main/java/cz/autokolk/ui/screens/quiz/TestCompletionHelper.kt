package cz.autokolk.ui.screens.quiz

import android.app.Application
import cz.autokolk.AchievementsManager
import cz.autokolk.LessonProgress
import cz.autokolk.Question

internal object TestCompletionHelper {

    /**
     * Stejné vedlejší efekty jako dřív [QuizViewModel.completeTestMode] před navigací na výsledky.
     * @return weighted skóre 0–50 a firstOfDay
     */
    fun applyPostTestRewards(
        application: Application,
        lessonProgress: LessonProgress,
        questions: List<Question>,
    ): Pair<Int, Boolean> {
        if (questions.isEmpty()) return 0 to false
        var correct = 0
        for (q in questions) {
            val ok = normalizeAnswerKey(q.userAnswer) == resolveCorrectKey(q)
            lessonProgress.recordMistakeStreak(q.id, ok)
            if (ok) correct++
        }
        try {
            AchievementsManager(application).onTestCorrectAdded(correct)
        } catch (_: Throwable) {
        }
        val firstOfDay = try {
            lessonProgress.updateStreakOnLessonCompleted()
        } catch (_: Throwable) {
            false
        }
        val weighted = (correct * 50 / questions.size.coerceAtLeast(1)).coerceIn(0, 50)
        try {
            if (weighted > 0) lessonProgress.addPoints(weighted)
        } catch (_: Throwable) {
        }
        try {
            lessonProgress.addTestScore(weighted, 50)
        } catch (_: Throwable) {
        }
        return weighted to firstOfDay
    }
}
