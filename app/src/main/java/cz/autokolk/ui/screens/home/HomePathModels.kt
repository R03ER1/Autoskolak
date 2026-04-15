package cz.autokolk.ui.screens.home

import androidx.compose.ui.graphics.Color
import cz.autokolk.GlobalLesson

sealed interface HomePathRow {
    data class Header(
        val title: String,
        val doneCount: Int,
        val totalCount: Int,
        val sectionColor: Color,
    ) : HomePathRow

    data class LessonItem(
        val lesson: GlobalLesson,
        val displayNumber: Int,
        val waveIndex: Int,
        val sectionColor: Color,
        val nodeState: LessonNodeState,
        val ringProgress: Float,
        val subtitle: String,
    ) : HomePathRow

    data object Footer : HomePathRow
}
