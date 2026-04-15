package cz.autokolk.ui.screens.quiz

import cz.autokolk.LessonProgress
import cz.autokolk.Question
import kotlin.random.Random

/** Jedna „skupina“ podle znění zkoušky (řazeno v pořadí a–g). */
data class OfficialExamBlock(
    val startIndex: Int,
    val questionCount: Int,
    val descriptionLine: String,
)

data class OfficialExamBuildResult(
    val questions: List<Question>,
    val pointsPerQuestion: List<Int>,
    val blocks: List<OfficialExamBlock>,
)

private data class BucketSpec(
    val categories: List<String>,
    val count: Int,
    val pointsEach: Int,
    val descriptionLine: String,
)

private val officialBuckets = listOf(
    BucketSpec(
        categories = listOf("def", "prav"),
        count = 10,
        pointsEach = 2,
        descriptionLine = "10 otázek ze skupiny Pravidla provozu na pozemních komunikacích (každá otázka za 2 body)",
    ),
    BucketSpec(
        categories = listOf("znak"),
        count = 3,
        pointsEach = 1,
        descriptionLine = "3 otázky ze skupiny Dopravní značky (každá otázka za 1 bod)",
    ),
    BucketSpec(
        categories = listOf("bez"),
        count = 4,
        pointsEach = 2,
        descriptionLine = "4 otázky ze skupiny Zásady bezpečné jízdy (každá otázka za 2 body)",
    ),
    BucketSpec(
        categories = listOf("res"),
        count = 3,
        pointsEach = 4,
        descriptionLine = "3 otázky ze skupiny Dopravní situace (každá otázka za 4 body)",
    ),
    BucketSpec(
        categories = listOf("voz"),
        count = 2,
        pointsEach = 1,
        descriptionLine = "2 otázky ze skupiny Předpisy o podmínkách provozu vozidel (každá otázka za 1 bod)",
    ),
    BucketSpec(
        categories = listOf("souv"),
        count = 2,
        pointsEach = 2,
        descriptionLine = "2 otázky ze skupiny Předpisy související s provozem (každá otázka za 2 body)",
    ),
    BucketSpec(
        categories = listOf("med"),
        count = 1,
        pointsEach = 1,
        descriptionLine = "1 otázka ze skupiny Zdravotnická příprava (1 bod)",
    ),
)

/**
 * Složení jako u klasické zkoušky: 25 otázek, celkem 50 bodů.
 * @return null, pokud nelze naplnit některý blok (nedostatek dat v CSV).
 */
fun buildOfficialExamQuestionSet(
    lessonProgress: LessonProgress,
    random: Random = Random.Default,
): OfficialExamBuildResult? {
    fun pickFrom(categories: List<String>, count: Int, used: MutableSet<String>): List<Question> {
        val pool = categories.flatMap { cat -> lessonProgress.getQuestionsForCategory(cat) }
        val uniquePool = pool.distinctBy { it.id }.toMutableList()
        uniquePool.shuffle(random)
        val takeList = mutableListOf<Question>()
        for (q in uniquePool) {
            if (takeList.size >= count) break
            if (q.id !in used) {
                takeList.add(q)
                used.add(q.id)
            }
        }
        var idx = 0
        while (takeList.size < count && takeList.isNotEmpty()) {
            takeList.add(takeList[idx % takeList.size])
            idx += 1
        }
        if (takeList.size < count) return emptyList()
        return takeList.map { it.copy(userAnswer = null) }
    }

    val result = mutableListOf<Question>()
    val points = mutableListOf<Int>()
    val blocks = mutableListOf<OfficialExamBlock>()
    val usedIds = mutableSetOf<String>()
    var start = 0

    for (spec in officialBuckets) {
        val picked = pickFrom(spec.categories, spec.count, usedIds)
        if (picked.size < spec.count) return null
        blocks.add(
            OfficialExamBlock(
                startIndex = start,
                questionCount = spec.count,
                descriptionLine = spec.descriptionLine,
            ),
        )
        result.addAll(picked)
        repeat(spec.count) { points.add(spec.pointsEach) }
        start += spec.count
    }

    if (result.size != TEST_QUESTION_COUNT || points.sum() != 50) return null
    return OfficialExamBuildResult(result, points, blocks)
}
