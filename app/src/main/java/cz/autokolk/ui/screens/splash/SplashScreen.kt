package cz.autokolk.ui.screens.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import cz.autokolk.ui.navigation.Route
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {
    LaunchedEffect(Unit) {
        delay(900)
        navController.navigate(Route.Home.route) {
            popUpTo(Route.Splash.route) { inclusive = true }
        }
    }
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Autoškolák", style = MaterialTheme.typography.headlineMedium)
    }
}
