package cz.autokolk.ui

import android.content.SharedPreferences
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import cz.autokolk.LessonProgress
import cz.autokolk.ui.components.feedback.HomeTutorialSpotlightOverlay
import cz.autokolk.ui.components.navigation.AutokolkBottomBar
import cz.autokolk.ui.components.navigation.AutokolkTopBar
import cz.autokolk.ui.components.sheets.CoinsSheet
import cz.autokolk.ui.components.sheets.HeartsSheet
import cz.autokolk.ui.components.sheets.StreakSheet
import cz.autokolk.ui.navigation.AutokolkNavGraph
import cz.autokolk.ui.navigation.Route
import cz.autokolk.ui.navigation.navigateToTab

private val tabRoutes = Route.mainTabs.map { it.route }.toSet()

@Composable
fun AutokolkApp() {
    val navController = rememberNavController()
    val currentEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentEntry?.destination?.route

    val showBars = currentRoute in tabRoutes
    val onHomeTab = currentRoute == Route.Home.route

    var homeScrollToCurrentSignal by remember { mutableIntStateOf(0) }

    val context = LocalContext.current
    val lessonProgress = remember { LessonProgress(context) }

    var streak by remember { mutableIntStateOf(lessonProgress.getCurrentStreak()) }
    var coins by remember { mutableIntStateOf(lessonProgress.getTotalPoints()) }
    var lives by remember { mutableIntStateOf(lessonProgress.getCurrentHearts()) }
    var streakHistory by remember { mutableStateOf(lessonProgress.getStreakHistory()) }

    fun refreshStats() {
        streak = lessonProgress.getCurrentStreak()
        coins = lessonProgress.getTotalPoints()
        lives = lessonProgress.getCurrentHearts()
        streakHistory = lessonProgress.getStreakHistory()
    }

    DisposableEffect(lessonProgress) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            refreshStats()
        }
        lessonProgress.registerOnLessonProgressChanged(listener)
        onDispose { lessonProgress.unregisterOnLessonProgressChanged(listener) }
    }

    var streakSheetVisible by remember { mutableStateOf(false) }
    var heartsSheetVisible by remember { mutableStateOf(false) }
    var coinsSheetVisible by remember { mutableStateOf(false) }

    val tutorialTopRectState = remember { mutableStateOf<Rect?>(null) }
    val tutorialBottomRectState = remember { mutableStateOf<Rect?>(null) }
    val tutorialLessonRectState = remember { mutableStateOf<Rect?>(null) }
    val tutorialTopRect by tutorialTopRectState
    val tutorialBottomRect by tutorialBottomRectState
    val tutorialLessonRect by tutorialLessonRectState

    LaunchedEffect(currentRoute) {
        if (!onHomeTab) {
            tutorialLessonRectState.value = null
        }
    }

    Box(Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
                AnimatedVisibility(
                    visible = showBars,
                    enter = slideInVertically { it },
                    exit = slideOutVertically { it },
                ) {
                    Box(
                        Modifier.onGloballyPositioned { coords ->
                            tutorialBottomRectState.value = coords.boundsInRoot()
                        },
                    ) {
                        AutokolkBottomBar(
                            currentRoute = currentRoute ?: "",
                            onNavigate = { route ->
                                if (route == Route.Home && currentRoute == Route.Home.route) {
                                    homeScrollToCurrentSignal++
                                } else {
                                    navController.navigateToTab(route)
                                }
                            },
                        )
                    }
                }
            },
            containerColor = Color.Transparent,
        ) { paddingValues ->
            Column(Modifier.fillMaxSize()) {
                AnimatedVisibility(
                    visible = showBars,
                    enter = slideInVertically { -it },
                    exit = slideOutVertically { -it },
                ) {
                    Box(
                        Modifier.onGloballyPositioned { coords ->
                            tutorialTopRectState.value = coords.boundsInRoot()
                        },
                    ) {
                        AutokolkTopBar(
                            streak = streak,
                            coins = coins,
                            lives = lives,
                            onStreakClick = { streakSheetVisible = true },
                            onCoinsClick = { coinsSheetVisible = true },
                            onLivesClick = { heartsSheetVisible = true },
                        )
                    }
                }
                Box(Modifier.weight(1f).padding(bottom = paddingValues.calculateBottomPadding())) {
                    AutokolkNavGraph(
                        navController = navController,
                        onHomeLessonBoundsChanged = { r -> tutorialLessonRectState.value = r },
                        homeScrollToCurrentSignal = homeScrollToCurrentSignal,
                    )
                }
            }
        }

        if (onHomeTab) {
            HomeTutorialSpotlightOverlay(
                active = true,
                topBarRect = tutorialTopRect,
                lessonRect = tutorialLessonRect,
                bottomBarRect = tutorialBottomRect,
                onDismissed = { refreshStats() },
            )
        }

        StreakSheet(
            isVisible = streakSheetVisible,
            streak = streak,
            streakHistory = streakHistory,
            lessonProgress = lessonProgress,
            onDismiss = { streakSheetVisible = false },
            onStreakUpdated = {
                refreshStats()
                streakSheetVisible = false
            },
        )

        HeartsSheet(
            isVisible = heartsSheetVisible,
            lives = lives,
            lessonProgress = lessonProgress,
            onDismiss = { heartsSheetVisible = false },
            onLivesUpdated = { refreshStats() },
        )

        CoinsSheet(
            isVisible = coinsSheetVisible,
            totalCoins = coins,
            onDismiss = { coinsSheetVisible = false },
        )
    }
}
