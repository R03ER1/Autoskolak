package cz.autokolk.ui.screens.quiz

import android.app.Application
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cz.autokolk.R
import kotlinx.coroutines.delay
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import cz.autokolk.ui.components.animation.ConfettiOverlay
import cz.autokolk.ui.navigation.Route

private val quizFunFacts = listOf(
    "Tip: Při řazení zkontroluj zpětné zrcátko.",
    "V testu platí stejné časové limity jako u zkoušky.",
    "Značka „Stůj“ platí pro všechny účastníky provozu.",
)

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
                    Route.Results(f.lessonId, f.score, f.total).buildPath(),
                ) {
                    popUpTo(Route.Home.route) { inclusive = false }
                }
                vm.clearFinish()
            }
            is QuizFinish.Test -> {
                navController.navigate(
                    Route.Results(-1, f.score, f.total).buildPath(),
                ) {
                    popUpTo(Route.Home.route) { inclusive = false }
                }
                vm.clearFinish()
            }
            is QuizFinish.Practice -> {
                navController.navigate(
                    Route.Results(-3, f.score, f.total).buildPath(),
                ) {
                    popUpTo(Route.Home.route) { inclusive = false }
                }
                vm.clearFinish()
            }
            null -> {}
        }
    }

    var heartLossVisible by remember { mutableStateOf(false) }
    LaunchedEffect(state.heartLossToken) {
        if (state.heartLossToken > 0) {
            heartLossVisible = true
            delay(800)
            heartLossVisible = false
        }
    }

    val funFactLine = remember(state.shakeToken, state.lastWasCorrect) {
        if (state.lastWasCorrect == false) {
            quizFunFacts[state.shakeToken % quizFunFacts.size]
        } else {
            null
        }
    }

    val total = state.questions.size.coerceAtLeast(1)
    val progress = ((state.index + 1).toFloat() / total.toFloat()).coerceIn(0f, 1f)

    Box(
        Modifier
            .fillMaxSize()
            .graphicsLayer { translationX = shake.value },
    ) {
        AnimatedVisibility(
            visible = heartLossVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 8.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_live),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
            )
        }
        ConfettiOverlay(
            isActive = !isTest && state.awaitingAdvance && state.lastWasCorrect == true,
            modifier = Modifier.fillMaxSize(),
        )
        Column(Modifier.fillMaxSize()) {
            QuizTopBar(
                progress = progress,
                current = state.index + 1,
                total = total,
                testRemainingMs = if (isTest) state.testRemainingMs else null,
                onClose = { vm.requestQuit() },
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Bonusy — brzy (nápovědy)",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(Modifier.height(8.dp))
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
                    onContinue = { vm.advance() },
                    funFact = funFactLine,
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
    }

    if (state.showQuitDialog) {
        AlertDialog(
            onDismissRequest = { vm.dismissQuitDialog() },
            title = { Text("Ukončit?") },
            text = { Text("Opravdu chceš odejít z kvízu?") },
            confirmButton = {
                TextButton(onClick = { vm.confirmQuit() }) {
                    Text("Ukončit")
                }
            },
            dismissButton = {
                TextButton(onClick = { vm.dismissQuitDialog() }) {
                    Text("Zpět")
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
