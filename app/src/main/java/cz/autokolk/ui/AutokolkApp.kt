package cz.autokolk.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import cz.autokolk.ui.screens.onboarding.OnboardingPreferences
import cz.autokolk.ui.components.animation.AnimatedBackground
import cz.autokolk.ui.components.navigation.AutokolkBottomBar
import cz.autokolk.ui.components.navigation.AutokolkTopBar
import cz.autokolk.ui.navigation.AutokolkNavGraph
import cz.autokolk.ui.navigation.Route
import cz.autokolk.ui.navigation.navigateToTab

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
                        streak = 0,
                        coins = 0,
                        lives = 0,
                        onStreakClick = {},
                        onCoinsClick = {},
                        onLivesClick = {},
                    )
                }
                Box(
                    Modifier
                        .weight(1f)
                        .padding(paddingValues),
                ) {
                    AutokolkNavGraph(
                        navController = navController,
                        modifier = Modifier.fillMaxSize(),
                        startDestination = startDestination,
                    )
                }
            }
        }
    }
}
