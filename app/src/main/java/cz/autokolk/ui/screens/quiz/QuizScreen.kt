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
import cz.autokolk.ui.sound.QuizTestCountdownSoundEffect
import cz.autokolk.ui.theme.DarkSurfaceVariant

@Composable
fun QuizScreen(
    navController: NavHostController,
    lessonId: Int,
    isTest: Boolean,
    categoryId: Int,
    isReview: Boolean,
) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val lessonProgress = remember { LessonProgress(application) }
    val vm: QuizViewModel = viewModel(
        factory = remember(lessonId, isTest, categoryId, isReview) {
            QuizViewModelFactory(application, lessonId, isTest, categoryId, isReview)
        },
    )
    val state by vm.state.collectAsStateWithLifecycle()
    val finish by vm.finish.collectAsStateWithLifecycle()

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
            is QuizFinish.Test -> {
                navController.navigate(
                    Route.Results(
                        lessonId = -1,
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
            null -> {}
        }
    }

    QuizTestCountdownSoundEffect(state.testRemainingMs)

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
                isActive = !isTest && state.awaitingAdvance && state.lastWasCorrect == true && state.comboStreak >= 10,
                modifier = Modifier.fillMaxSize(),
            )

            FloatingReward(
                visible = !isTest && state.showCoinPopup,
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
                    testRemainingMs = if (isTest) state.testRemainingMs else null,
                    hearts = if (!isTest) state.hearts else null,
                    comboStreak = state.comboStreak,
                    onClose = { vm.requestQuit() },
                )
                Spacer(Modifier.height(6.dp))
                QuizPowerUpRow(
                    onHint = {
                        Toast.makeText(context, "Nápovědy a bonusy přijdou ve Fázi 11.", Toast.LENGTH_SHORT).show()
                    },
                )
                Spacer(Modifier.height(6.dp))
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
                                isTest = isTest,
                                onPick = { vm.selectAnswer(it) },
                            )
                        }
                    }
                }
                if (!isTest) {
                    QuizResultStrip(
                        visible = state.awaitingAdvance && state.lastWasCorrect != null,
                        correct = state.lastWasCorrect == true,
                        combo = state.comboStreak,
                        funFact = currentQuestion?.funFact,
                        wrongDetail = wrongDetail,
                        onContinue = { vm.advance() },
                    )
                } else {
                    TextButton(
                        onClick = { vm.advance() },
                        modifier = Modifier.padding(16.dp),
                    ) {
                        Text(if (state.index >= state.questions.lastIndex) "Ukončit test" else "Další otázka")
                    }
                }
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
        AlertDialog(
            onDismissRequest = { vm.dismissQuitDialog() },
            containerColor = DarkSurfaceVariant,
            title = { Text("Ukončit lekci?") },
            text = { Text("Tvůj postup v této session se ukončí a výsledek se uloží.", color = MaterialTheme.colorScheme.onSurfaceVariant) },
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
    private val lessonId: Int,
    private val isTest: Boolean,
    private val categoryId: Int,
    private val isReview: Boolean,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return QuizViewModel(application, lessonId, isTest, categoryId, isReview) as T
    }
}
