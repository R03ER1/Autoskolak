package cz.autokolk.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import cz.autokolk.ui.components.navigation.AutokolkBottomBar
import cz.autokolk.ui.navigation.AutokolkNavGraph
import cz.autokolk.ui.navigation.Route

@Composable
fun AutokolkApp() {
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
    val showBottomBar = currentRoute in tabRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                AutokolkBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route.route) {
                            launchSingleTop = true
                        }
                    },
                )
            }
        },
    ) { padding ->
        AutokolkNavGraph(
            navController = navController,
            modifier = Modifier.padding(padding),
        )
    }
}
