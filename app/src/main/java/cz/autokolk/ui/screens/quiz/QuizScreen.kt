package cz.autokolk.ui.screens.quiz

import android.app.Application
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import cz.autokolk.LessonProgress
import cz.autokolk.ui.components.animation.AnimatedBackground
import cz.autokolk.ui.components.animation.ConfettiOverlay
import cz.autokolk.ui.components.animation.FloatingReward
import cz.autokolk.ui.navigation.Route
import cz.autokolk.ui.theme.AccentCyan
import cz.autokolk.ui.theme.DarkSurfaceVariant

@Composable
fun QuizScreen(
    navController: NavHostController,
    lessonId: Int,
    isTest: Boolean,
    categoryId: Int,
    isReview: Boolean,
    practiceCategoryKey: String? = null,
    practiceMode: Int = 0,
    practiceSubcategoryKey: String = Route.PracticeQuiz.ALL_SUB,
    practiceFocusQuestionId: String = Route.PracticeQuiz.FOCUS_NONE,
) {
    if (isTest) {
        TestQuizSession(navController)
        return
    }

    val isPractice = !practiceCategoryKey.isNullOrBlank()
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val lessonProgress = remember { LessonProgress(application) }
    val session: QuizSession = remember(lessonId, isReview, practiceCategoryKey, practiceMode, practiceSubcategoryKey, practiceFocusQuestionId) {
        if (isPractice) {
            QuizSession.Practice(
                categoryKey = practiceCategoryKey!!,
                practiceMode = practiceMode,
                subcategoryKey = practiceSubcategoryKey,
                focusQuestionId = practiceFocusQuestionId,
            )
        } else {
            QuizSession.Lesson(lessonId = lessonId, isReview = isReview)
        }
    }
    val vm: QuizViewModel = viewModel(
        factory = remember(session) {
            QuizViewModelFactory(application, session)
        },
    )
    val state by vm.state.collectAsStateWithLifecycle()
    val finish by vm.finish.collectAsStateWithLifecycle()

    LaunchedEffect(isPractice, state.questions) {
        if (isPractice && state.questions.isEmpty()) {
            Toast.makeText(context, "Žádné otázky k procvičení.", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    }

    val shake = remember { Animatable(0f) }
    LaunchedEffect(state.shakeToken) {
        if (state.shakeToken > 0) {
            shake.snapTo(0f)
            shake.animateTo(12f, spring())
            shake.animateTo(-10f, spring())
            shake.animateTo(8f, spring())
            shake.animateTo(0f, spring())
        }
    }

    LaunchedEffect(finish) {
        when (val f = finish) {
            is QuizFinish.Lesson -> {
                navController.navigate(
                    Route.Results(
                        lessonId = f.lessonId,
                        score = f.score,
                        total = f.total,
                        firstOfDay = f.firstOfDay,
                        pointsAwarded = f.pointsAwarded,
                    ).buildPath(),
                ) {
                    popUpTo(Route.Home.route) { inclusive = false }
                }
                vm.clearFinish()
            }
            is QuizFinish.Practice -> {
                val encCat = Route.Results.encodeReplayPart(f.replayCategory)
                val encSub = Route.Results.encodeReplayPart(f.replaySubKey)
                val encFocus = Route.Results.encodeReplayPart(f.replayFocusQuestionId)
                navController.navigate(
                    Route.Results(
                        lessonId = 0,
                        score = f.score,
                        total = f.total,
                        firstOfDay = f.firstOfDay,
                        pointsAwarded = f.pointsAwarded,
                        fromPractice = true,
                        replayCategoryEncoded = encCat,
                        replayPracticeMode = f.replayPracticeMode,
                        replaySubEncoded = encSub,
                        replayFocusEncoded = encFocus,
                    ).buildPath(),
                ) {
                    popUpTo(Route.Practice.route) { inclusive = false }
                }
                vm.clearFinish()
            }
            null -> {}
        }
    }

    val total = state.questions.size.coerceAtLeast(1)
    val progress = ((state.index + 1).toFloat() / total.toFloat()).coerceIn(0f, 1f)
    val currentQuestion = state.questions.getOrNull(state.index)
    val wrongDetail = currentQuestion?.let { q ->
        when {
            !q.explanation.isNullOrBlank() -> q.explanation
            !q.funFact.isNullOrBlank() -> "Zkus se zamyslet nad touto otázkou znovu. 💡 ${q.funFact}"
            else -> null
        }
    }

    AnimatedBackground(modifier = Modifier.fillMaxSize()) {
        Box(
            Modifier
                .fillMaxSize()
                .graphicsLayer { translationX = shake.value },
        ) {
            ConfettiOverlay(
                isActive = !isPractice && state.awaitingAdvance && state.lastWasCorrect == true && state.comboStreak >= 10,
                modifier = Modifier.fillMaxSize(),
            )

            FloatingReward(
                visible = state.showCoinPopup,
                amount = state.coinPopupAmount,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding(),
                onDismiss = { vm.dismissCoinPopup() },
            )

            Column(
                Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .displayCutoutPadding()
                    .navigationBarsPadding(),
            ) {
                QuizTopBar(
                    progress = progress,
                    current = state.index + 1,
                    total = total,
                    testRemainingMs = null,
                    hearts = state.hearts,
                    comboStreak = state.comboStreak,
                    showCombo = !isPractice,
                    onClose = { vm.requestQuit() },
                )
                if (!isPractice) {
                    Spacer(Modifier.height(6.dp))
                    QuizPowerUpRow(
                        enabled = !isReview && !state.awaitingAdvance && state.pendingAnswerKey == null,
                        onEliminate = {
                            if (!vm.usePowerUpEliminate()) {
                                Toast.makeText(context, "Nelze použít (mince nebo už jedna akce).", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onSkip = {
                            if (!vm.usePowerUpSkip()) {
                                Toast.makeText(context, "Nelze přeskočit (mince nebo už jedna akce).", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onHint = {
                            if (!vm.usePowerUpHint()) {
                                Toast.makeText(context, "Nelze zobrazit nápovědu (mince nebo už jedna akce).", Toast.LENGTH_SHORT).show()
                            }
                        },
                    )
                    Spacer(Modifier.height(6.dp))
                }
                Box(Modifier.weight(1f)) {
                    AnimatedContent(
                        targetState = state.index,
                        transitionSpec = {
                            (slideInHorizontally { it } + fadeIn()).togetherWith(
                                slideOutHorizontally { -it } + fadeOut(),
                            )
                        },
                        label = "quizQuestion",
                    ) { idx ->
                        val question = state.questions.getOrNull(idx) ?: return@AnimatedContent
                        Column(
                            Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                        ) {
                            QuizMedia(imagePath = question.imagePath, videoPath = question.videoPath)
                            Spacer(Modifier.height(12.dp))
                            QuestionContent(
                                question = question,
                                awaitingAdvance = state.awaitingAdvance,
                                pendingAnswerKey = state.pendingAnswerKey,
                                isTest = false,
                                onPick = { vm.selectAnswer(it) },
                                eliminatedOptionKeys = state.eliminatedOptionKeys,
                                hintLine = if (state.hintVisible) {
                                    question.explanation?.takeIf { it.isNotBlank() }
                                        ?: "Přečti zadání znovu — někdy stačí vyloučit jednu z možností."
                                } else {
                                    null
                                },
                            )
                        }
                    }
                }
                QuizResultStrip(
                    visible = state.awaitingAdvance && state.lastWasCorrect != null,
                    correct = state.lastWasCorrect == true,
                    combo = state.comboStreak,
                    funFact = currentQuestion?.funFact,
                    wrongDetail = wrongDetail,
                    onContinue = { vm.advance() },
                )
            }

            if (state.showNoLivesOverlay) {
                QuizNoLivesOverlay(
                    visible = true,
                    lessonProgress = lessonProgress,
                    onDismissAfterReward = { vm.onHeartRefilledFromAd() },
                    onGoHome = { vm.goHomeFromNoLives() },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }

    if (state.showQuitDialog) {
        val title = if (isPractice) "Ukončit procvičování?" else "Ukončit lekci?"
        val text = if (isPractice) {
            "Session se ukončí a zobrazí se přehled výsledků."
        } else {
            "Tvůj postup v této session se ukončí a výsledek se uloží."
        }
        AlertDialog(
            onDismissRequest = { vm.dismissQuitDialog() },
            containerColor = DarkSurfaceVariant,
            title = { Text(title) },
            text = { Text(text, color = MaterialTheme.colorScheme.onSurfaceVariant) },
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

private class QuizViewModelFactory(
    private val application: Application,
    private val session: QuizSession,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return QuizViewModel(application, session) as T
    }
}
