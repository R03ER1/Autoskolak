package cz.autokolk.ui.screens.onboarding

import android.Manifest
import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import cz.autokolk.Question
import cz.autokolk.R
import cz.autokolk.ui.components.animation.AnimatedBackground
import cz.autokolk.ui.components.animation.ConfettiOverlay
import cz.autokolk.ui.components.buttons.PrimaryGradientButton
import cz.autokolk.ui.navigation.Route
import cz.autokolk.ui.screens.quiz.QuestionContent
import cz.autokolk.ui.theme.AccentCyan
import cz.autokolk.ui.theme.PillShape
import cz.autokolk.ui.theme.accentCyanText
import cz.autokolk.ui.theme.glassPalette
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch

/** Maximální šířka CTA v patičce — na širokých displejích nepřetáhnout přes celou obrazovku. */
private val OnboardingFooterCtaMaxWidth = 400.dp

private data class DailyGoalRow(
    val emoji: String,
    val title: String,
    val subtitle: String,
    val value: Int,
)

private val dailyGoalRows = listOf(
    DailyGoalRow("🐢", "Pohoda", "1 lekce denně", 1),
    DailyGoalRow("🐇", "Normální", "3 lekce denně", 3),
    DailyGoalRow("🔥", "Intenzivní", "5 lekcí denně", 5),
    DailyGoalRow("💀", "Šílený", "10 lekcí denně", 10),
)

private fun demoQuestion(): Question = Question(
    id = "onboarding_demo",
    questionText = "Smí řidič motorového vozidla za jízdy držet v ruce mobilní telefon?",
    optionA = "Ano, pokud nepoužívá handsfree",
    optionB = "Ne",
    optionC = "Ano, ale jen ve městě",
    correctAnswer = "b",
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(navController: NavHostController) {
    val context = LocalContext.current
    val onboardingPrefs = remember { OnboardingPreferences(context) }
    val steps = remember { buildOnboardingSteps() }
    val pagerState = rememberPagerState(pageCount = { steps.size })
    val scope = rememberCoroutineScope()

    var selectedDailyGoal by remember { mutableIntStateOf(OnboardingPreferences.DEFAULT_DAILY_GOAL) }
    var lionName by remember { mutableStateOf(OnboardingPreferences.DEFAULT_LION_NAME) }
    var demoQuestion by remember { mutableStateOf(demoQuestion()) }
    var showConfetti by remember { mutableStateOf(false) }

    val bounce = remember { Animatable(1f) }
    LaunchedEffect(lionName.length) {
        bounce.snapTo(1f)
        bounce.animateTo(1.06f, tween(90))
        bounce.animateTo(1f, spring(dampingRatio = 0.45f))
    }

    val accent = steps.getOrNull(pagerState.currentPage)?.accentOrDefault() ?: AccentCyan

    fun finishOnboarding() {
        onboardingPrefs.dailyGoal = selectedDailyGoal
        onboardingPrefs.lionName = lionName
        onboardingPrefs.isCompleted = true
        navController.navigate(Route.Home.route) {
            popUpTo(Route.Onboarding.route) { inclusive = true }
        }
    }

    val notifLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { finishOnboarding() }

    AnimatedBackground(
        modifier = Modifier.fillMaxSize(),
        accentColor = accent,
    ) {
        Box(Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .fillMaxSize()
                    .systemBarsPadding(),
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f),
                    userScrollEnabled = false,
                ) { page ->
                    when (val step = steps[page]) {
                        is OnboardingStep.InfoPage -> OnboardingInfoPage(step)
                        OnboardingStep.DailyGoalPage -> OnboardingDailyGoalPage(
                            selected = selectedDailyGoal,
                            onSelect = { selectedDailyGoal = it },
                        )
                        OnboardingStep.NameLionPage -> OnboardingNameLionPage(
                            lionName = lionName,
                            onLionNameChange = { lionName = it },
                            imageScale = bounce.value,
                        )
                        OnboardingStep.DemoQuestionPage -> OnboardingDemoQuestionPage(
                            question = demoQuestion,
                            onAnswer = { key ->
                                demoQuestion = demoQuestion.copy(userAnswer = key)
                                val correct = key == "b"
                                showConfetti = correct
                            },
                        )
                        OnboardingStep.NotificationPage -> OnboardingNotificationPage(
                            onAllow = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    finishOnboarding()
                                }
                            },
                            onSkip = { finishOnboarding() },
                        )
                    }
                }

                if (steps[pagerState.currentPage] !is OnboardingStep.NotificationPage) {
                    OnboardingFooter(
                        pageCount = steps.size,
                        currentPage = pagerState.currentPage,
                        accent = accent,
                        isInfoPage = steps[pagerState.currentPage] is OnboardingStep.InfoPage,
                        isLastPage = pagerState.currentPage == steps.lastIndex,
                        isDemoPage = steps[pagerState.currentPage] is OnboardingStep.DemoQuestionPage,
                        demoAnswered = demoQuestion.userAnswer != null,
                        hasNotificationStep = steps.any { it is OnboardingStep.NotificationPage },
                        onSkip = {
                            scope.launch { pagerState.animateScrollToPage(4) }
                        },
                        onNext = {
                            scope.launch {
                                when {
                                    pagerState.currentPage < steps.lastIndex -> {
                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    }
                                    else -> finishOnboarding()
                                }
                            }
                        },
                        onFinishFromDemo = { finishOnboarding() },
                    )
                }
            }

            ConfettiOverlay(
                isActive = showConfetti,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun OnboardingInfoPage(page: OnboardingStep.InfoPage) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset(page.lottieAssetPath))
    Column(
        Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier.size(250.dp),
        )
        Spacer(Modifier.height(32.dp))
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun OnboardingDailyGoalPage(
    selected: Int,
    onSelect: (Int) -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            "Nastav si denní cíl",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Kolik lekcí chceš denně zvládnout?",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(24.dp))
        val goalRowBorder = glassPalette().borderStart
        dailyGoalRows.forEach { row ->
            val selectedRow = row.value == selected
            val scale by animateFloatAsState(
                targetValue = if (selectedRow) 1.04f else 1f,
                animationSpec = spring(dampingRatio = 0.65f),
                label = "goalRowScale",
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .scale(scale)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
                    .then(
                        if (selectedRow) {
                            Modifier.border(2.dp, AccentCyan, RoundedCornerShape(16.dp))
                        } else {
                            Modifier.border(1.dp, goalRowBorder, RoundedCornerShape(16.dp))
                        },
                    )
                    .clickable { onSelect(row.value) }
                    .padding(16.dp),
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(row.emoji, style = MaterialTheme.typography.headlineMedium)
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text(row.title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                        Text(row.subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    if (selectedRow) {
                        Text("✓", style = MaterialTheme.typography.titleLarge, color = accentCyanText())
                    }
                }
            }
        }
    }
}

@Composable
private fun OnboardingNameLionPage(
    lionName: String,
    onLionNameChange: (String) -> Unit,
    imageScale: Float,
) {
    val context = LocalContext.current
    val alexBitmap = remember(context) {
        try {
            context.assets.open("images/alex/AlexHappy.png").use { stream ->
                BitmapFactory.decodeStream(stream)?.asImageBitmap()
            }
        } catch (_: Throwable) {
            null
        }
    }
    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            "Pojmenuj svého lva",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Bude tě doprovázet celou cestu k řidičáku.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(24.dp))
        if (alexBitmap != null) {
            Image(
                bitmap = alexBitmap,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(200.dp)
                    .graphicsLayer {
                        scaleX = imageScale
                        scaleY = imageScale
                    },
            )
        } else {
            Image(
                painter = painterResource(R.drawable.ic_alex),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(200.dp)
                    .graphicsLayer {
                        scaleX = imageScale
                        scaleY = imageScale
                    },
            )
        }
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = lionName,
            onValueChange = { raw ->
                if (raw.length <= 20) onLionNameChange(raw)
            },
            label = { Text("Jméno") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedBorderColor = AccentCyan,
                unfocusedBorderColor = glassPalette().borderStart,
            ),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun OnboardingDemoQuestionPage(
    question: Question,
    onAnswer: (String) -> Unit,
) {
    val awaiting = question.userAnswer != null
    Column(
        Modifier
            .fillMaxSize()
            .padding(top = 24.dp),
    ) {
        Text(
            "Vyzkoušej si ukázkovou otázku",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        )
        Spacer(Modifier.height(16.dp))
        QuestionContent(
            question = question,
            awaitingAdvance = awaiting,
            pendingAnswerKey = null,
            isTest = false,
            onPick = { key ->
                if (question.userAnswer == null) onAnswer(key)
            },
        )
        if (awaiting) {
            Spacer(Modifier.height(16.dp))
            val correct = question.userAnswer == "b"
            Text(
                text = if (correct) "Skvěle! Vidíš? To zvládneš!" else "Zkus to znovu příště — důležité je učit se!",
                style = MaterialTheme.typography.bodyLarge,
                color = if (correct) accentCyanText() else MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            )
        }
    }
}

@Composable
private fun OnboardingNotificationPage(
    onAllow: () -> Unit,
    onSkip: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "🔔",
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        Spacer(Modifier.height(24.dp))
        Text(
            "Připomínky",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "Pošleme ti připomínku, abys nepřišel o streak a o Alexe!",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(32.dp))
        PrimaryGradientButton(
            text = "Povolit notifikace",
            onClick = onAllow,
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = OnboardingFooterCtaMaxWidth),
        )
        Spacer(Modifier.height(12.dp))
        TextButton(
            onClick = onSkip,
            modifier = Modifier.wrapContentWidth(),
        ) {
            Text("Teď ne", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun OnboardingFooter(
    pageCount: Int,
    currentPage: Int,
    accent: Color,
    isInfoPage: Boolean,
    isLastPage: Boolean,
    isDemoPage: Boolean,
    demoAnswered: Boolean,
    hasNotificationStep: Boolean,
    onSkip: () -> Unit,
    onNext: () -> Unit,
    onFinishFromDemo: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val inactiveDotColor = glassPalette().borderStart
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(pageCount) { index ->
                val active = index == currentPage
                val width by animateDpAsState(
                    targetValue = if (active) 24.dp else 8.dp,
                    label = "onboardingDotW",
                )
                val color by animateColorAsState(
                    targetValue = if (active) accent else inactiveDotColor,
                    label = "onboardingDotC",
                )
                Box(
                    Modifier
                        .height(8.dp)
                        .width(width)
                        .clip(PillShape)
                        .background(color),
                )
            }
        }
        Spacer(Modifier.height(24.dp))

        // CTA: až po šířku rodiče, max 400dp — vycentrované přes horizontalAlignment sloupce
        val ctaWidthModifier = Modifier
            .fillMaxWidth()
            .widthIn(max = OnboardingFooterCtaMaxWidth)

        when {
            isLastPage && !hasNotificationStep && isDemoPage && demoAnswered -> {
                PrimaryGradientButton(
                    text = "Začít!",
                    onClick = onFinishFromDemo,
                    modifier = ctaWidthModifier,
                )
            }
            isDemoPage && !demoAnswered -> {
                PrimaryGradientButton(
                    text = "Vyber odpověď",
                    onClick = { },
                    enabled = false,
                    modifier = ctaWidthModifier,
                )
            }
            isDemoPage -> {
                PrimaryGradientButton(
                    text = "Další",
                    onClick = onNext,
                    modifier = ctaWidthModifier,
                )
            }
            isInfoPage -> {
                Row(
                    modifier = ctaWidthModifier,
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(
                        onClick = onSkip,
                        modifier = Modifier.wrapContentWidth(),
                    ) {
                        Text("Přeskočit", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(Modifier.width(24.dp))
                    PrimaryGradientButton(
                        text = "Další",
                        onClick = onNext,
                        modifier = Modifier.wrapContentWidth(),
                    )
                }
            }
            else -> {
                PrimaryGradientButton(
                    text = "Další",
                    onClick = onNext,
                    modifier = ctaWidthModifier,
                )
            }
        }
    }
}
