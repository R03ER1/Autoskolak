package cz.autokolk

/**
 * Body za dokončenou lekci / opakování — stejná pravidla jako v [ResultsActivity].
 */
object LessonPoints {

    fun computeLessonPointsAwarded(
        isPractice: Boolean,
        isRandom: Boolean,
        isReviewMode: Boolean,
        correctAnswers: Int,
        totalQuestions: Int,
    ): Int {
        if (isPractice || isRandom) return 0
        if (totalQuestions <= 0) return 0
        val percentage = (correctAnswers * 100.0 / totalQuestions).toInt()
        return if (!isReviewMode) {
            when {
                percentage == 100 -> 8
                percentage >= 65 -> 6
                percentage < 35 -> 1
                else -> 4
            }
        } else {
            when {
                totalQuestions in 6..10 -> {
                    when {
                        percentage == 100 -> 6
                        percentage >= 65 -> 4
                        else -> 2
                    }
                }
                totalQuestions in 1..5 -> {
                    if (percentage >= 65) 2 else 1
                }
                else -> 0
            }
        }
    }
}
