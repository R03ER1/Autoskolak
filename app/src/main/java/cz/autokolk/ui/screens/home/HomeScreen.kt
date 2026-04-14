package cz.autokolk.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import cz.autokolk.GlobalLesson
import cz.autokolk.ui.components.feedback.EventOverlay
import cz.autokolk.ui.components.feedback.TutorialOverlay
import cz.autokolk.ui.navigation.Route
import cz.autokolk.ui.theme.TextPrimary

@Composable
fun HomeScreen(navController: NavHostController) {
    val vm: HomeViewModel = viewModel()
    val rows by vm.pathRows.collectAsStateWithLifecycle()
    val reordered by vm.reorderedPlan.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val context = LocalContext.current

    var sheetLesson by remember { mutableStateOf<GlobalLesson?>(null) }
    var sheetDisplay by remember { mutableStateOf(1) }

    val progressFraction = remember(rows, reordered) {
        if (reordered.isEmpty()) 0f
        else {
            val done = rows.count {
                it is HomePathRow.LessonItem &&
                    (it.nodeState == LessonNodeState.COMPLETED || it.nodeState == LessonNodeState.PERFECT)
            }
            done.toFloat() / reordered.size.toFloat()
        }
    }

    LaunchedEffect(rows) {
        val idx = rows.indexOfFirst {
            it is HomePathRow.LessonItem && it.nodeState == LessonNodeState.CURRENT
        }
        if (idx >= 0) {
            listState.scrollToItem(idx.coerceAtLeast(0))
        }
    }

    Box(Modifier.fillMaxSize()) {
        LessonPathBackground(
            nodeCount = reordered.size.coerceAtLeast(2),
            progressFraction = progressFraction,
            modifier = Modifier.fillMaxSize(),
        )
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 8.dp),
        ) {
            items(rows, key = { row ->
                when (row) {
                    is HomePathRow.Header -> "h:${row.title}"
                    is HomePathRow.LessonItem -> "l:${row.lesson.lessonNumber}"
                    HomePathRow.Footer -> "footer"
                }
            }) { row ->
                when (row) {
                    is HomePathRow.Header -> {
                        Column(Modifier.fillMaxWidth()) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = row.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                            )
                            Spacer(Modifier.height(4.dp))
                        }
                    }
                    is HomePathRow.LessonItem -> {
                        val offsetDp = HomePathListBuilder.horizontalOffsetDp(row.waveIndex)
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(start = offsetDp.coerceIn(0, 280).dp, top = 6.dp, bottom = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            LessonNode(
                                iconFileName = mapSubcategoryToIconAsset(row.lesson.subcategory.trim().lowercase()),
                                sectionColor = row.sectionColor,
                                state = row.nodeState,
                                ringProgress = row.ringProgress,
                                onClick = {
                                    sheetLesson = row.lesson
                                    sheetDisplay = row.displayNumber
                                },
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = row.subtitle,
                                style = MaterialTheme.typography.labelMedium,
                                color = TextPrimary,
                            )
                        }
                    }
                    HomePathRow.Footer -> {
                        Text(
                            text = "Hotovo!",
                            style = MaterialTheme.typography.headlineMedium,
                            color = TextPrimary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }

        TutorialOverlay(
            onDismiss = { /* persisted inside */ },
        )
        EventOverlay()
    }

    val sl = sheetLesson
    if (sl != null) {
        val st = vm.lessonProgress.getLessonState(sl.lessonNumber)
        val title = buildLessonTitle(sl, sl.lessonNumber, sheetDisplay, vm.lessonProgress)
        val canStart = vm.canStartLesson(sl.lessonNumber)
        val isReview = st.completed && !st.incorrectQuestionIds.isNullOrEmpty()
        LessonInfoSheet(
            lesson = sl,
            displayNumber = sheetDisplay,
            lessonState = st,
            titleText = title,
            onDismiss = { sheetLesson = null },
            onStart = {
                if (!vm.hasHeartsOrInfinite()) {
                    sheetLesson = null
                } else {
                    sheetLesson = null
                    if (shouldShowTopicIntro(context, sl.lessonNumber, sl, vm.lessonProgress)) {
                        markTopicIntroShown(context, sl)
                        navController.navigate(Route.ReadingLesson(sl.lessonNumber, isReview).buildPath())
                    } else {
                        navController.navigate(Route.Quiz(sl.lessonNumber, false, -1, isReview).buildPath())
                    }
                }
            },
            canStart = canStart,
            startEnabled = vm.hasHeartsOrInfinite(),
        )
    }
}
