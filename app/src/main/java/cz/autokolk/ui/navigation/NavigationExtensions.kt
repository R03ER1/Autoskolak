package cz.autokolk.ui.navigation

import androidx.navigation.NavHostController

/**
 * Navigates to a main tab while preventing back-stack accumulation.
 * Back press from any non-Home tab always returns to Home.
 */
fun NavHostController.navigateToTab(route: Route) {
    navigate(route.route) {
        popUpTo(Route.Home.route) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
