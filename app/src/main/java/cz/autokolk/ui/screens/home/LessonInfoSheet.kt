package cz.autokolk.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.autokolk.GlobalLesson
import cz.autokolk.LessonProgress
import cz.autokolk.LessonState
import cz.autokolk.ui.theme.TextPrimary
import cz.autokolk.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonInfoSheet(
    lesson: GlobalLesson?,
    displayNumber: Int,
    lessonState: LessonState,
    titleText: String,
    onDismiss: () -> Unit,
    onStart: () -> Unit,
    canStart: Boolean,
    startEnabled: Boolean,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
            Text(
                text = titleText,
                color = TextPrimary,
                style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = lesson?.let { mapCategoryDisplayName(it.category) } ?: "",
                color = TextSecondary,
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
            )
            val progressLine = when {
                !lessonState.completed -> ""
                lessonState.incorrectQuestionIds.isNullOrEmpty() -> "Hotovo"
                else -> "Zbývá ${lessonState.incorrectQuestionIds.size} otázek"
            }
            if (progressLine.isNotEmpty()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = progressLine,
                    color = TextSecondary,
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Lekce #$displayNumber · 10 otázek",
                color = TextSecondary,
                style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
            )
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = onStart,
                enabled = startEnabled && canStart,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Začít")
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

fun buildLessonTitle(
    lesson: GlobalLesson?,
    lessonNumber: Int,
    displayNumber: Int,
    lessonProgress: LessonProgress,
): String {
    if (lesson == null) return "Lekce $displayNumber"
    val plan = lessonProgress.getGlobalLessonPlan()
    val cat = lesson.category.trim()
    val sub = lesson.subcategory.trim()
    if (sub.isBlank()) {
        val sameCategory = plan
            .filter { it.category.trim().equals(cat, ignoreCase = true) }
            .sortedBy { it.lessonNumber }
        val idx = sameCategory.indexOfFirst { it.lessonNumber == lessonNumber }.let { if (it >= 0) it else 0 }
        val levelIndex = (idx + 1).coerceAtLeast(1)
        val totalLevels = sameCategory.size.coerceAtLeast(1)
        return "${mapCategoryDisplayName(cat)} $levelIndex/$totalLevels"
    }
    var sameSubcategory = plan
        .filter {
            it.category.trim().equals(cat, ignoreCase = true) &&
                it.subcategory.trim().equals(sub, ignoreCase = true)
        }
        .sortedBy { it.lessonNumber }
    if (sameSubcategory.isEmpty()) {
        sameSubcategory = plan
            .filter { it.subcategory.trim().equals(sub, ignoreCase = true) }
            .sortedBy { it.lessonNumber }
    }
    val foundByNumber = sameSubcategory.indexOfFirst { it.lessonNumber == lessonNumber }
    val idx = if (foundByNumber >= 0) foundByNumber else 0
    val subLevelIndex = (idx + 1).coerceAtLeast(1)
    val subTotalLevels = sameSubcategory.size.coerceAtLeast(1)
    return "${mapSubcategoryDisplayName(sub)} $subLevelIndex/$subTotalLevels"
}
