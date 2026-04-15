package cz.autokolk.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import cz.autokolk.ui.navigation.AutokolkNavGraph

@Composable
fun AutokolkApp() {
    val navController = rememberNavController()
    AutokolkNavGraph(navController = navController)
}
