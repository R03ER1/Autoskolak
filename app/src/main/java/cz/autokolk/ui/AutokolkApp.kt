package cz.autokolk.ui

import android.content.SharedPreferences
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import cz.autokolk.LessonProgress
import cz.autokolk.ui.components.animation.AnimatedBackground
import cz.autokolk.ui.components.navigation.AutokolkBottomBar
import cz.autokolk.ui.components.navigation.AutokolkTopBar
import cz.autokolk.ui.components.sheets.CoinsSheet
import cz.autokolk.ui.components.sheets.HeartsSheet
import cz.autokolk.ui.components.sheets.StreakSheet
import cz.autokolk.ui.navigation.AutokolkNavGraph
import cz.autokolk.ui.navigation.Route
import cz.autokolk.ui.navigation.navigateToTab
import cz.autokolk.ui.screens.onboarding.OnboardingPreferences
import kotlin.collections.emptyList

private enum class TopSheet { None, Streak, Coins, Hearts }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutokolkApp() {
    val context = LocalContext.current
    val onboardingCompleted = remember { OnboardingPreferences.isCompleted(context) }
    val startDestination = if (!onboardingCompleted) Route.Onboarding.route else Route.Splash.route

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val tabRoutes = setOf(
        Route.Home.route,
        Route.Alex.route,
        Route.Test.route,
        Route.Practice.route,
        Route.Settings.route,
    )
    val showChrome = currentRoute in tabRoutes

    val lessonProgress = remember { LessonProgress(context) }
    var streak by remember { mutableIntStateOf(lessonProgress.getCurrentStreak()) }
    var coins by remember { mutableIntStateOf(lessonProgress.getTotalPoints()) }
    var lives by remember { mutableIntStateOf(lessonProgress.getCurrentHearts()) }
    var sheet by remember { mutableStateOf(TopSheet.None) }

    val listener = remember {
        SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            streak = lessonProgress.getCurrentStreak()
            coins = lessonProgress.getTotalPoints()
            lives = lessonProgress.getCurrentHearts()
        }
    }
    DisposableEffect(lessonProgress) {
        lessonProgress.registerOnLessonProgressChanged(listener)
        onDispose {
            lessonProgress.unregisterOnLessonProgressChanged(listener)
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            AnimatedVisibility(
                visible = showChrome,
                enter = slideInVertically { it },
                exit = slideOutVertically { it },
            ) {
                AutokolkBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { navController.navigateToTab(it) },
                )
            }
        },
    ) { paddingValues ->
        AnimatedBackground(Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize()) {
                if (showChrome) {
                    AutokolkTopBar(
                        streak = streak,
                        coins = coins,
                        lives = lives,
                        onStreakClick = { sheet = TopSheet.Streak },
                        onCoinsClick = { sheet = TopSheet.Coins },
                        onLivesClick = { sheet = TopSheet.Hearts },
                    )
                }
                BoxWithConstraints(
                    Modifier
                        .weight(1f)
                        .padding(paddingValues)
                        .fillMaxSize(),
                    contentAlignment = Alignment.TopCenter,
                ) {
                    AutokolkNavGraph(
                        navController = navController,
                        modifier = Modifier
                            .widthIn(max = 640.dp)
                            .fillMaxSize(),
                        startDestination = startDestination,
                    )
                }
            }
        }
    }

    StreakSheet(
        isVisible = sheet == TopSheet.Streak,
        streak = streak,
        streakHistory = emptyList(),
        onDismiss = { sheet = TopSheet.None },
        onWatchAd = { sheet = TopSheet.None },
        shouldShowProtection = false,
    )
    CoinsSheet(
        isVisible = sheet == TopSheet.Coins,
        totalCoins = coins,
        onDismiss = { sheet = TopSheet.None },
    )
    HeartsSheet(
        isVisible = sheet == TopSheet.Hearts,
        lives = lives,
        maxLives = 15,
        nextHeartIn = null,
        onDismiss = { sheet = TopSheet.None },
        onWatchAd = { sheet = TopSheet.None },
    )
}
