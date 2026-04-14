package cz.autokolk.ui.navigation

import androidx.navigation.NavHostController

/**
 * Přepínání hlavních tabů bez hromadění back stacku; stav tabu lze obnovit ([restoreState]).
 */
fun NavHostController.navigateToTab(route: Route) {
    navigate(route.route) {
        popUpTo(Route.Home.route) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
