package cz.autokolk.ui.screens.home

import androidx.compose.ui.graphics.Color
import cz.autokolk.GlobalLesson
import cz.autokolk.LessonProgress
import cz.autokolk.LessonState
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.sin

object HomePathListBuilder {

    fun buildRows(
        lessonProgress: LessonProgress,
        reordered: List<GlobalLesson>,
    ): List<HomePathRow> {
        if (reordered.isEmpty()) return emptyList()
        val totalLessons = reordered.size
        val totalSections =
            (totalLessons + LessonProgress.QUESTIONS_PER_LESSON - 1) / LessonProgress.QUESTIONS_PER_LESSON
        val defSubcategoryCodes = setOf("uca", "aut", "vec", "cho", "poj")
        val defLessonsAll = lessonProgress.getGlobalLessonPlan()
            .filter { it.subcategory.trim().lowercase() in defSubcategoryCodes }
        val defBlock = defLessonsAll.take(14)
        val defCount = defBlock.size

        val nonDefGroups = listOf(
            "Začátečník" to 10,
            "Pokročilý" to 15,
            "Profesionál" to 20,
            "Znalec" to 20,
            "Génius" to 20,
            "Alfa samec" to 20,
        )
        var currentGroupIndex = -1
        var nextBoundary = defCount
        var startedSkoroHotovo = false

        fun maybeStartNextGroup(currentIdx: Int, out: MutableList<HomePathRow>) {
            if (currentIdx < defCount) return
            if (currentGroupIndex == -1 && currentIdx == defCount) {
                currentGroupIndex = 0
                nextBoundary = defCount + nonDefGroups[0].second
                out.add(HomePathRow.Header(nonDefGroups[0].first))
                return
            }
            while (currentGroupIndex in 0..nonDefGroups.lastIndex && currentIdx == nextBoundary) {
                currentGroupIndex++
                if (currentGroupIndex <= nonDefGroups.lastIndex) {
                    out.add(HomePathRow.Header(nonDefGroups[currentGroupIndex].first))
                    nextBoundary += nonDefGroups[currentGroupIndex].second
                } else if (!startedSkoroHotovo) {
                    out.add(HomePathRow.Header("Skoro hotovo!"))
                    startedSkoroHotovo = true
                    nextBoundary = Int.MAX_VALUE
                }
            }
        }

        val hasAnyProgress = lessonProgress.getAllLessonStates().isNotEmpty()
        val nextAllowed =
            if (hasAnyProgress) lessonProgress.getNextAvailableLesson() else firstLessonNumber(reordered)
        val displayMap = reordered.mapIndexed { idx, gl -> gl.lessonNumber to (idx + 1) }.toMap()

        val out = mutableListOf<HomePathRow>()
        if (defCount > 0) {
            out.add(HomePathRow.Header("Základní pojmy"))
        }

        var globalWaveIndex = 0
        for (sectionIndex in 0 until totalSections) {
            val hueRange = 300f
            val hueStart = 30f
            val hue =
                (hueStart + (sectionIndex.toFloat() / max(1, totalSections - 1).toFloat()) * hueRange) % 360f
            val hsv = floatArrayOf(hue, 0.7f, 0.95f)
            val sectionColor = Color(android.graphics.Color.HSVToColor(hsv))

            val startIndexInclusive = sectionIndex * LessonProgress.QUESTIONS_PER_LESSON
            val endIndexExclusive =
                minOf(startIndexInclusive + LessonProgress.QUESTIONS_PER_LESSON, totalLessons)
            for (idx in startIndexInclusive until endIndexExclusive) {
                maybeStartNextGroup(idx, out)
                val gl = reordered[idx]
                val st = lessonProgress.getLessonState(gl.lessonNumber)
                val tried = st.completed
                val canStart = tried || gl.lessonNumber == nextAllowed
                val nodeState = toNodeState(gl.lessonNumber, st, nextAllowed, canStart)
                val ringProgress = ringProgressFor(st)
                val subtitle = subtitleFor(st)
                out.add(
                    HomePathRow.LessonItem(
                        lesson = gl,
                        displayNumber = displayMap[gl.lessonNumber] ?: gl.lessonNumber,
                        waveIndex = globalWaveIndex,
                        sectionColor = sectionColor,
                        nodeState = nodeState,
                        ringProgress = ringProgress,
                        subtitle = subtitle,
                    ),
                )
                globalWaveIndex++
            }
        }
        out.add(HomePathRow.Footer)
        return out
    }

    private fun firstLessonNumber(reordered: List<GlobalLesson>): Int {
        return reordered.firstOrNull()?.lessonNumber ?: 1
    }

    private fun ringProgressFor(st: LessonState): Float {
        if (!st.completed) return 0f
        val wrong = st.incorrectQuestionIds?.size ?: 0
        val correct = LessonProgress.QUESTIONS_PER_LESSON - wrong
        return (correct.toFloat() / LessonProgress.QUESTIONS_PER_LESSON.toFloat()).coerceIn(0f, 1f)
    }

    private fun subtitleFor(st: LessonState): String {
        val base = "10 otázek"
        if (!st.completed) return base
        val wrong = st.incorrectQuestionIds?.size ?: 0
        return when {
            wrong > 0 -> "$base ($wrong chyb)"
            else -> "$base ✓"
        }
    }

    private fun toNodeState(
        lessonNumber: Int,
        st: LessonState,
        nextAllowed: Int,
        canStart: Boolean,
    ): LessonNodeState {
        if (!canStart) return LessonNodeState.LOCKED
        val incorrect = st.incorrectQuestionIds?.size ?: 0
        if (st.completed) {
            return if (incorrect == 0) LessonNodeState.PERFECT else LessonNodeState.COMPLETED
        }
        return if (lessonNumber == nextAllowed) LessonNodeState.CURRENT else LessonNodeState.LOCKED
    }

    /** Horizontální odsazení jako u [cz.autokolk.HomeActivity] (sinus přes „periodItems“ položek). */
    fun horizontalOffsetDp(waveIndex: Int, baseMaxOffsetDp: Double = 250.0): Int {
        val centerDp = baseMaxOffsetDp / 2.0
        val amplitudeDp = centerDp * 0.8
        val periodItems = 8.0
        val angle = (waveIndex.toDouble() / periodItems) * (2.0 * PI)
        val offsetDp = centerDp + amplitudeDp * sin(angle)
        return offsetDp.toInt()
    }
}
