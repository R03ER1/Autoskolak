package cz.autokolk.ui.screens.quiz

import android.app.Application
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import cz.autokolk.ui.components.animation.AnimatedBackground
import cz.autokolk.ui.navigation.Route
import cz.autokolk.ui.sound.QuizTestCountdownSoundEffect
import cz.autokolk.ui.theme.AccentCyan
import cz.autokolk.ui.util.rememberIsExpandedLandscape
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun TestQuizSession(navController: NavHostController) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val vm: TestViewModel = viewModel(
        factory = remember {
            TestViewModelFactory(application)
        },
    )
    val state by vm.state.collectAsStateWithLifecycle()
    val navEvent by vm.navEvent.collectAsStateWithLifecycle()

    LaunchedEffect(state.loadState) {
        if (state.loadState == TestLoadState.InsufficientPool) {
            navController.popBackStack()
        }
    }

    LaunchedEffect(navEvent) {
        when (val e = navEvent) {
            is TestNavEvent.ToResults -> {
                navController.navigate(Route.TestResults(testId = e.attemptId).buildPath()) {
                    popUpTo(Route.Test.route) { inclusive = false }
                }
                vm.clearNavEvent()
            }
            null -> {}
        }
    }

    if (state.runPhase == TestRunPhase.Running) {
        QuizTestCountdownSoundEffect(state.testRemainingMs)
    }

    when (state.loadState) {
        TestLoadState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AccentCyan)
            }
        }
        TestLoadState.InsufficientPool -> {
            Box(Modifier.fillMaxSize())
        }
        TestLoadState.Ready -> {
            key(state.questions) {
                TestQuizSessionReadyContent(vm, state)
            }
        }
    }

    if (state.showQuitDialog) {
        AlertDialog(
            onDismissRequest = { vm.dismissQuitDialog() },
            title = { Text("Ukončit zkoušku?") },
            text = {
                Text(
                    "Výsledek se uloží podle aktuálně zadaných odpovědí.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            confirmButton = {
                TextButton(onClick = { vm.confirmQuit() }) {
                    Text("Ukončit", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { vm.dismissQuitDialog() }) {
                    Text("Pokračovat", color = AccentCyan)
                }
            },
        )
    }
}

@Composable
private fun TestQuizSessionReadyContent(
    vm: TestViewModel,
    state: TestUiState,
) {
    val total = state.questions.size.coerceAtLeast(1)
    val progress = ((state.index + 1).toFloat() / total.toFloat()).coerceIn(0f, 1f)
    // Krok 161 (tablet/landscape): stejná side-by-side úprava jako v QuizScreen.
    val isExpandedLandscape = rememberIsExpandedLandscape()

    val pagerState = rememberPagerState(
        initialPage = state.index.coerceIn(0, state.questions.lastIndex.coerceAtLeast(0)),
        pageCount = { state.questions.size.coerceAtLeast(1) },
    )

    LaunchedEffect(state.index) {
        if (state.questions.isNotEmpty() && pagerState.currentPage != state.index) {
            pagerState.animateScrollToPage(state.index)
        }
    }

    LaunchedEffect(pagerState, state.questions) {
        if (state.questions.isEmpty()) return@LaunchedEffect
        snapshotFlow { pagerState.isScrollInProgress to pagerState.currentPage }
            .distinctUntilChanged()
            .collect { (inProgress, page) ->
                if (!inProgress) {
                    vm.goToQuestion(page)
                }
            }
    }

    AnimatedBackground(modifier = Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .displayCutoutPadding()
                .navigationBarsPadding(),
        ) {
            val examSubtitle = examBlockSubtitle(state.examBlocks, state.index)
            QuizTopBar(
                progress = progress,
                current = state.index + 1,
                total = total,
                testRemainingMs = state.testRemainingMs,
                hearts = null,
                comboStreak = 0,
                showCombo = false,
                onClose = { vm.requestQuit() },
                belowProgress = if (examSubtitle.isNotBlank()) {
                    {
                        Text(
                            text = examSubtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 4,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                } else {
                    null
                },
            )
            Spacer(Modifier.height(8.dp))
            Box(Modifier.weight(1f)) {
                if (state.questions.isNotEmpty()) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize(),
                        userScrollEnabled = state.runPhase == TestRunPhase.Running,
                    ) { page ->
                        val question = state.questions[page]
                        val hasMedia = !question.imagePath.isNullOrBlank() || !question.videoPath.isNullOrBlank()
                        if (isExpandedLandscape && hasMedia) {
                            Row(Modifier.fillMaxSize()) {
                                Box(
                                    Modifier
                                        .weight(0.42f)
                                        .fillMaxSize()
                                        .padding(end = 8.dp),
                                ) {
                                    QuizMedia(
                                        imagePath = question.imagePath,
                                        videoPath = question.videoPath,
                                        modifier = Modifier.align(Alignment.Center),
                                    )
                                }
                                Column(
                                    Modifier
                                        .weight(0.58f)
                                        .fillMaxSize()
                                        .verticalScroll(rememberScrollState()),
                                ) {
                                    key(question.id, state.answersByQuestionId[question.id]) {
                                        QuestionContent(
                                            question = question,
                                            awaitingAdvance = false,
                                            pendingAnswerKey = null,
                                            isTest = true,
                                            onPick = { vm.selectAnswer(it) },
                                            testSelectionKey = state.answersByQuestionId[question.id],
                                        )
                                    }
                                }
                            }
                        } else {
                            Column(
                                Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState()),
                            ) {
                                QuizMedia(imagePath = question.imagePath, videoPath = question.videoPath)
                                Spacer(Modifier.height(12.dp))
                                key(question.id, state.answersByQuestionId[question.id]) {
                                    QuestionContent(
                                        question = question,
                                        awaitingAdvance = false,
                                        pendingAnswerKey = null,
                                        isTest = true,
                                        onPick = { vm.selectAnswer(it) },
                                        testSelectionKey = state.answersByQuestionId[question.id],
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedButton(
                    onClick = { vm.prevQuestion() },
                    enabled = state.index > 0,
                ) {
                    Text("Předchozí")
                }
                Text(
                    text = "Otázka ${state.index + 1}/$TEST_QUESTION_COUNT",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                )
                OutlinedButton(
                    onClick = { vm.nextQuestion() },
                    enabled = state.index < state.questions.lastIndex,
                ) {
                    Text("Další")
                }
            }
            Button(
                onClick = { vm.finishTest() },
                enabled = state.runPhase == TestRunPhase.Running,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Text("Dokončit test")
            }
        }

        val cd = state.countdownShow
        if (cd != null) {
            TestCountdownOverlay(value = cd)
        }
    }
}

@Composable
private fun TestCountdownOverlay(value: Int) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.82f)),
        contentAlignment = Alignment.Center,
    ) {
        AnimatedContent(
            targetState = value,
            transitionSpec = { (scaleIn() + fadeIn()).togetherWith(scaleOut() + fadeOut()) },
            label = "testCountdown",
        ) { v ->
            Text(
                text = if (v > 0) "$v" else "Start!",
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 96.sp),
                color = AccentCyan,
            )
        }
    }
}

private fun examBlockSubtitle(blocks: List<OfficialExamBlock>, questionIndex: Int): String {
    val b = blocks.find { questionIndex >= it.startIndex && questionIndex < it.startIndex + it.questionCount }
    return b?.descriptionLine.orEmpty()
}

private class TestViewModelFactory(
    private val application: Application,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TestViewModel(application) as T
    }
}
