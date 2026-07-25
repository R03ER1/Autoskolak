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
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import cz.autokolk.GlobalLesson
import cz.autokolk.SeasonalEvents
import cz.autokolk.audio.SoundManager
import cz.autokolk.ui.components.badges.SectionMilestoneBadge
import cz.autokolk.ui.components.feedback.RandomEventOverlay
import cz.autokolk.ui.components.glass.GlassCard
import cz.autokolk.ui.components.progress.AnimatedProgressBar
import cz.autokolk.ui.navigation.Route
import cz.autokolk.ui.navigation.lessonHeroTransitionKey
import cz.autokolk.ui.util.HapticFeedback
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    navController: NavHostController,
    onCurrentLessonNodeBoundsChanged: ((Rect) -> Unit)? = null,
    scrollToCurrentLessonSignal: Int = 0,
) {
    val vm: HomeViewModel = viewModel()
    val rows by vm.pathRows.collectAsStateWithLifecycle()
    val reordered by vm.reorderedPlan.collectAsStateWithLifecycle()
    val randomEvent by vm.randomEvent.collectAsStateWithLifecycle()
    val dailyChallenges by vm.dailyChallenges.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val context = LocalContext.current
    val density = LocalDensity.current

    var sheetLesson by remember { mutableStateOf<GlobalLesson?>(null) }
    var sheetDisplay by remember { mutableStateOf(1) }

    val lessonOrder = remember(reordered) { reordered.map { it.lessonNumber } }
    val measuredCenters = remember { mutableStateMapOf<Int, Offset>() }

    LaunchedEffect(lessonOrder) {
        measuredCenters.clear()
    }

    LaunchedEffect(Unit) {
        vm.tryShowRandomEventIfDue()
    }

    var pathRootCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }

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

    val seasonalMessage = SeasonalEvents.activeMessage()

    val currentLessonIndex = remember(rows) {
        rows.indexOfFirst {
            it is HomePathRow.LessonItem && it.nodeState == LessonNodeState.CURRENT
        }
    }

    val scrollOffsetPx = remember(density) { with(density) { (-200).dp.roundToPx() } }

    suspend fun scrollToCurrentLesson() {
        if (currentLessonIndex < 0) return
        delay(48)
        listState.animateScrollToItem(
            index = currentLessonIndex,
            scrollOffset = scrollOffsetPx,
        )
    }

    LaunchedEffect(currentLessonIndex, rows.size) {
        scrollToCurrentLesson()
    }

    LaunchedEffect(scrollToCurrentLessonSignal) {
        if (scrollToCurrentLessonSignal == 0) return@LaunchedEffect
        scrollToCurrentLesson()
    }

    Box(
        Modifier
            .fillMaxSize()
            .onGloballyPositioned { pathRootCoords = it },
    ) {
        LessonPathBackground(
            lessonOrder = lessonOrder,
            measuredCenters = measuredCenters,
            progressFraction = progressFraction,
            modifier = Modifier.fillMaxSize(),
        )
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 8.dp),
        ) {
            item(key = "daily_challenges") {
                DailyChallengesRow(challenges = dailyChallenges)
            }
            if (seasonalMessage != null) {
                item(key = "seasonal_banner") {
                    SeasonalBanner(message = seasonalMessage)
                }
            }
            items(rows, key = { row ->
                when (row) {
                    is HomePathRow.Header -> "h:${row.title}:${row.doneCount}:${row.totalCount}"
                    is HomePathRow.LessonItem -> "l:${row.lesson.lessonNumber}"
                    is HomePathRow.SectionBadge -> "badge:${row.sectionKey}"
                    HomePathRow.Footer -> "footer"
                }
            }) { row ->
                when (row) {
                    is HomePathRow.Header -> {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            GlassCard(
                                modifier = Modifier.fillMaxWidth(0.92f),
                                borderGradient = listOf(
                                    row.sectionColor.copy(alpha = 0.55f),
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                                ),
                            ) {
                                Column(
                                    Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    Text(
                                        text = row.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth(),
                                    )
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        text = "${row.doneCount}/${row.totalCount} dokončeno",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth(),
                                    )
                                    if (row.totalCount > 0 && row.doneCount >= row.totalCount) {
                                        Spacer(Modifier.height(6.dp))
                                        Text(
                                            text = "🏆 Sekce dokončena",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.onSurface,
                                        )
                                    }
                                    Spacer(Modifier.height(10.dp))
                                    val frac = if (row.totalCount > 0) {
                                        row.doneCount.toFloat() / row.totalCount.toFloat()
                                    } else {
                                        0f
                                    }
                                    AnimatedProgressBar(
                                        progress = frac,
                                        accent = row.sectionColor,
                                        modifier = Modifier.fillMaxWidth(),
                                    )
                                }
                            }
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
                            val root = pathRootCoords
                            Box(
                                Modifier
                                    .onGloballyPositioned { coords ->
                                        if (root == null || !root.isAttached || !coords.isAttached) return@onGloballyPositioned
                                        val centerLocal = Offset(
                                            coords.size.width / 2f,
                                            coords.size.height / 2f,
                                        )
                                        val inPath = root.localPositionOf(coords, centerLocal)
                                        measuredCenters[row.lesson.lessonNumber] = inPath
                                        if (row.nodeState == LessonNodeState.CURRENT) {
                                            val b = coords.boundsInRoot()
                                            onCurrentLessonNodeBoundsChanged?.invoke(b)
                                        }
                                    },
                                contentAlignment = Alignment.Center,
                            ) {
                                LessonNode(
                                    iconFileName = remember(row.lesson.subcategory) {
                                        mapSubcategoryToIconAsset(row.lesson.subcategory.trim().lowercase())
                                    },
                                    sectionColor = row.sectionColor,
                                    state = row.nodeState,
                                    ringProgress = row.ringProgress,
                                    onClick = {
                                        sheetLesson = row.lesson
                                        sheetDisplay = row.displayNumber
                                    },
                                    transitionKey = lessonHeroTransitionKey(row.lesson.lessonNumber),
                                    accessibilityLabel = "Lekce ${row.displayNumber}: ${row.subtitle}. ${lessonNodeStateLabel(row.nodeState)}.",
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = row.subtitle,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                    is HomePathRow.SectionBadge -> {
                        val alreadySeen = remember(row.sectionKey) {
                            vm.lessonProgress.isSectionBadgeSeen(row.sectionKey)
                        }
                        LaunchedEffect(row.sectionKey) {
                            if (!alreadySeen) {
                                SoundManager.play(SoundManager.Sound.ACHIEVEMENT)
                                HapticFeedback.onAchievement(context)
                                vm.lessonProgress.markSectionBadgeSeen(row.sectionKey)
                            }
                        }
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            SectionMilestoneBadge(
                                sectionTitle = row.sectionTitle,
                                sectionColor = row.sectionColor,
                                justUnlocked = !alreadySeen,
                            )
                        }
                    }
                    HomePathRow.Footer -> {
                        Text(
                            text = "Hotovo!",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }

        RandomEventOverlay(
            event = randomEvent,
            onDismiss = { vm.dismissRandomEvent() },
        )
    }

    val sl = sheetLesson
    if (sl != null) {
        val st = vm.lessonProgress.getLessonState(sl.lessonNumber)
        // Krok 156: buildLessonTitle filtruje/seřazuje celý lesson plán — nemá se přepočítávat
        // při každé recompozici HomeScreen (např. kvůli scrollu path), jen když se změní
        // vybraná lekce nebo její pořadové číslo.
        val title = remember(sl.lessonNumber, sheetDisplay) {
            buildLessonTitle(sl, sl.lessonNumber, sheetDisplay, vm.lessonProgress)
        }
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

private fun lessonNodeStateLabel(state: LessonNodeState): String = when (state) {
    LessonNodeState.LOCKED -> "Zamčeno"
    LessonNodeState.CURRENT -> "Aktuální lekce"
    LessonNodeState.COMPLETED -> "Dokončeno"
    LessonNodeState.PERFECT -> "Dokončeno na 100 %"
}
