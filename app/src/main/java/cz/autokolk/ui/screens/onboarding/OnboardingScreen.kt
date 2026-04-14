package cz.autokolk.ui.screens.onboarding

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import cz.autokolk.LessonProgress
import cz.autokolk.Question
import cz.autokolk.ui.components.animation.AnimatedBackground
import cz.autokolk.ui.components.animation.ConfettiOverlay
import cz.autokolk.ui.components.buttons.AnswerButton
import cz.autokolk.ui.components.buttons.AnswerState
import cz.autokolk.ui.components.buttons.PrimaryGradientButton
import cz.autokolk.ui.navigation.Route
import cz.autokolk.ui.theme.AccentCyan
import cz.autokolk.ui.theme.DarkSurfaceVariant
import cz.autokolk.ui.theme.GlassWhite
import cz.autokolk.ui.theme.PillShape
import cz.autokolk.ui.theme.TextSecondary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(navController: NavHostController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { ONBOARDING_PAGE_COUNT })

    var draft by remember { mutableStateOf(OnboardingDraft()) }
    var demoQuestion by remember { mutableStateOf<Question?>(null) }
    /** Vybrané písmeno odpovědi (A/B/C), null dokud uživatel neklikl. */
    var demoPickedLetter by remember { mutableStateOf<String?>(null) }
    var showNotificationPrimer by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        demoQuestion = withContext(Dispatchers.IO) {
            runCatching {
                LessonProgress(context).getRandomQuestions(1).firstOrNull()
            }.getOrNull()
        }
    }

    val activeQuestion = demoQuestion ?: demoFallbackQuestion()
    val demoCorrect = demoPickedLetter?.equals(activeQuestion.correctAnswer.trim(), ignoreCase = true) == true

    val notifPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { _ ->
        completeOnboarding(
            context = context,
            navController = navController,
            draft = draft,
            notificationsPromptShown = true,
        )
    }

    fun skipEntireOnboarding() {
        completeOnboarding(
            context = context,
            navController = navController,
            draft = OnboardingDraft(),
            notificationsPromptShown = false,
        )
    }

    val currentAccent = remember(pagerState.currentPage) {
        when (pagerState.currentPage) {
            in onboardingIntroPages.indices -> onboardingIntroPages[pagerState.currentPage].accentColor
            else -> AccentCyan
        }
    }

    Box(Modifier.fillMaxSize()) {
        AnimatedBackground(
            modifier = Modifier.fillMaxSize(),
            accentColor = currentAccent,
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .systemBarsPadding(),
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f),
                ) { page ->
                    when (page) {
                        in onboardingIntroPages.indices -> OnboardingIntroPageContent(onboardingIntroPages[page])
                        4 -> LicensePage(draft.selectedLicense) { draft = draft.copy(selectedLicense = it) }
                        5 -> DailyGoalPage(draft.dailyGoalLessons) { draft = draft.copy(dailyGoalLessons = it) }
                        6 -> LionNamePage(draft.lionName) { draft = draft.copy(lionName = it) }
                        7 -> DemoQuestionPage(
                            question = activeQuestion,
                            pickedLetter = demoPickedLetter,
                            onPick = { letter ->
                                if (demoPickedLetter != null) return@DemoQuestionPage
                                demoPickedLetter = letter
                            },
                        )
                    }
                }

                OnboardingBottomBar(
                    pagerState = pagerState,
                    scope = scope,
                    demoPickedLetter = demoPickedLetter,
                    onSkipAll = { skipEntireOnboarding() },
                    onNextPage = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    },
                    onFinishDemo = { showNotificationPrimer = true },
                )
            }
        }

        if (demoCorrect && pagerState.currentPage == 7) {
            ConfettiOverlay(
                isActive = true,
                modifier = Modifier.fillMaxSize(),
                particleCount = 80,
            )
        }

        if (showNotificationPrimer) {
            NotificationPrimerOverlay(
                onAllow = {
                    showNotificationPrimer = false
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        completeOnboarding(
                            context = context,
                            navController = navController,
                            draft = draft,
                            notificationsPromptShown = true,
                        )
                    }
                },
                onSkipNotifications = {
                    showNotificationPrimer = false
                    completeOnboarding(
                        context = context,
                        navController = navController,
                        draft = draft,
                        notificationsPromptShown = true,
                    )
                },
            )
        }
    }
}

private fun demoFallbackQuestion(): Question = Question(
    id = "onboarding_demo",
    questionText = "Kdy odbočuješ vlevo, které světlo rozsvítíš?",
    optionA = "Levé směrové světlo",
    optionB = "Pravé směrové světlo",
    optionC = "Dálková světla",
    correctAnswer = "A",
)

@Composable
private fun OnboardingIntroPageContent(page: OnboardingIntroPage) {
    val compositionResult = rememberLottieComposition(LottieCompositionSpec.Asset(page.lottieAsset))
    val composition = compositionResult.value
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
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
            page.title,
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(12.dp))
        Text(
            page.description,
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun LicensePage(selected: String, onSelect: (String) -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            "Co chceš řídit?",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Vyber skupinu oprávnění (filtr otázek doplníme později).",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(24.dp))
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            licenseOptions.chunked(3).forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    row.forEach { code ->
                        FilterChip(
                            selected = selected == code,
                            onClick = { onSelect(code) },
                            label = { Text(code) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AccentCyan.copy(alpha = 0.35f),
                            ),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DailyGoalPage(selectedLessons: Int, onSelect: (Int) -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            "Nastav si denní cíl",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Kolik lekcí denně je pro tebe reálných?",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(24.dp))
        dailyGoalOptions.forEach { opt ->
            val selected = opt.lessonsPerDay == selectedLessons
            val scale by animateFloatAsState(if (selected) 1.04f else 1f, label = "goalScale")
            Card(
                onClick = { onSelect(opt.lessonsPerDay) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .scale(scale),
                colors = CardDefaults.cardColors(
                    containerColor = if (selected) AccentCyan.copy(alpha = 0.2f) else DarkSurfaceVariant,
                ),
                shape = RoundedCornerShape(16.dp),
            ) {
                Row(
                    Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("${opt.emoji}  ${opt.label}", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "${opt.lessonsPerDay}× denně",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                    )
                }
            }
        }
    }
}

@Composable
private fun LionNamePage(name: String, onNameChange: (String) -> Unit) {
    val bounce by animateFloatAsState(
        targetValue = if (name.isNotEmpty()) 1.03f else 1f,
        label = "lionBounce",
    )
    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            "Pojmenuj svého lva",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Jméno se použije v notifikacích i v péči o Alexe.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = "🦁",
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.scale(bounce),
        )
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text("Jméno") },
            placeholder = { Text("Alex") },
        )
    }
}

@Composable
private fun DemoQuestionPage(
    question: Question,
    pickedLetter: String?,
    onPick: (String) -> Unit,
) {
    fun buttonState(label: String): AnswerState {
        if (pickedLetter == null) return AnswerState.DEFAULT
        val correct = question.correctAnswer.trim().uppercase()
        val l = label.uppercase()
        if (l == correct) return AnswerState.CORRECT
        if (pickedLetter.equals(l, ignoreCase = true)) return AnswerState.WRONG
        return AnswerState.DEFAULT
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            "Ukázková otázka",
            style = MaterialTheme.typography.labelLarge,
            color = TextSecondary,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            question.questionText,
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(Modifier.height(8.dp))
        Surface(
            color = AccentCyan.copy(alpha = 0.08f),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text(
                "Tip: nahoře v aplikaci najdeš streak, mince a životy — stejně jako po dokončení onboardingu.",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(12.dp),
                color = TextSecondary,
            )
        }
        Spacer(Modifier.height(20.dp))
        AnswerButton(
            text = question.optionA,
            state = buttonState("A"),
            label = "A",
            onClick = { onPick("A") },
        )
        Spacer(Modifier.height(10.dp))
        AnswerButton(
            text = question.optionB,
            state = buttonState("B"),
            label = "B",
            onClick = { onPick("B") },
        )
        Spacer(Modifier.height(10.dp))
        AnswerButton(
            text = question.optionC,
            state = buttonState("C"),
            label = "C",
            onClick = { onPick("C") },
        )
        if (pickedLetter != null) {
            Spacer(Modifier.height(16.dp))
            val ok = pickedLetter.equals(question.correctAnswer.trim(), ignoreCase = true)
            Text(
                if (ok) "Vidíš? To zvládneš!" else "Správně je ${question.correctAnswer.uppercase()} — v appce dostaneš vysvětlení.",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OnboardingBottomBar(
    pagerState: PagerState,
    scope: kotlinx.coroutines.CoroutineScope,
    demoPickedLetter: String?,
    onSkipAll: () -> Unit,
    onNextPage: () -> Unit,
    onFinishDemo: () -> Unit,
) {
    val lastIndex = ONBOARDING_PAGE_COUNT - 1
    Column(
        Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(ONBOARDING_PAGE_COUNT) { index ->
                val current = pagerState.currentPage == index
                val width by animateDpAsState(
                    if (current) 24.dp else 8.dp,
                    label = "dotW",
                )
                val color by animateColorAsState(
                    if (current) AccentCyan else GlassWhite.copy(alpha = 0.4f),
                    label = "dotC",
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
        when {
            pagerState.currentPage == lastIndex -> {
                when {
                    demoPickedLetter == null -> {
                        Text(
                            "Vyber odpověď",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                        )
                    }
                    else -> {
                        PrimaryGradientButton(
                            text = "Začít!",
                            onClick = onFinishDemo,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
            else -> {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    TextButton(onClick = onSkipAll) {
                        Text("Přeskočit", color = TextSecondary)
                    }
                    PrimaryGradientButton(text = "Další", onClick = onNextPage)
                }
            }
        }
    }
}

@Composable
private fun NotificationPrimerOverlay(
    onAllow: () -> Unit,
    onSkipNotifications: () -> Unit,
) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.72f)),
    ) {
        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(24.dp)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
            shape = RoundedCornerShape(20.dp),
        ) {
            Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Připomínky streaku",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "Pošleme ti připomínku, abys nepřišel o streak. Můžeš to kdykoli změnit v nastavení systému.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(20.dp))
                PrimaryGradientButton(
                    text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        "Povolit notifikace"
                    } else {
                        "Rozumím"
                    },
                    onClick = onAllow,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = onSkipNotifications, modifier = Modifier.fillMaxWidth()) {
                    Text("Nechat bez připomínek", color = TextSecondary)
                }
            }
        }
    }
}

private fun completeOnboarding(
    context: android.content.Context,
    navController: NavHostController,
    draft: OnboardingDraft,
    notificationsPromptShown: Boolean,
) {
    OnboardingPreferences.saveOnboardingResult(
        context = context,
        draft = draft,
        notificationsOptInShown = notificationsPromptShown,
    )
    navController.navigate(Route.Home.route) {
        popUpTo(Route.Onboarding.route) { inclusive = true }
    }
}
