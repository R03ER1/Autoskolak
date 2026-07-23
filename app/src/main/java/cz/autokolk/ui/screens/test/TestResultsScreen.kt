package cz.autokolk.ui.screens.test

import android.app.Application
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import cz.autokolk.ui.components.animation.AnimatedBackground
import cz.autokolk.ui.components.animation.ConfettiOverlay
import cz.autokolk.ui.components.buttons.PrimaryGradientButton
import cz.autokolk.ui.components.glass.GlassCard
import cz.autokolk.ui.navigation.Route
import cz.autokolk.ui.theme.ErrorRed
import cz.autokolk.ui.theme.SuccessGreen

@Composable
fun TestResultsScreen(navController: NavHostController, attemptId: Long) {
    val context = LocalContext.current
    val app = context.applicationContext as Application
    val vm: TestResultsViewModel = viewModel(
        factory = remember(attemptId) {
            TestResultsViewModelFactory(app, attemptId)
        },
    )
    val state by vm.state.collectAsStateWithLifecycle()

    AnimatedBackground(Modifier.fillMaxSize()) {
        when {
            state.loading -> {
                Column(
                    Modifier
                        .fillMaxSize()
                        .systemBarsPadding()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("Načítám výsledek…", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            state.missing -> {
                Column(
                    Modifier
                        .fillMaxSize()
                        .systemBarsPadding()
                        .padding(24.dp),
                ) {
                    Text("Pokus nenalezen.", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(Modifier.height(16.dp))
                    PrimaryGradientButton(
                        text = "Zpět na zkoušku",
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            else -> {
                TestResultsContent(navController, state)
            }
        }
    }
}

@Composable
private fun TestResultsContent(
    navController: NavHostController,
    state: TestResultsUiState,
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset(
            if (state.passed) "lottie/correct_answer.json" else "lottie/wrong_answer.json",
        ),
    )

    Box(
        Modifier
            .fillMaxSize()
            .systemBarsPadding(),
    ) {
        ConfettiOverlay(isActive = state.passed, modifier = Modifier.fillMaxSize())
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(
                        if (state.passed) SuccessGreen.copy(alpha = 0.12f) else ErrorRed.copy(alpha = 0.12f),
                    )
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
            LottieAnimation(
                composition = composition,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier.height(120.dp),
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = if (state.passed) "Úspěšně složeno!" else "Nesloženo",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "${state.score}",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "z ${state.maxScore} bodů",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (!state.hasDetails) {
            Text(
                text = "U tohoto pokusu nejsou uložené podrobnosti jednotlivých otázek.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp),
            )
        } else {
            Text(
                text = "Podrobnosti",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(16.dp),
            )
            state.rows.forEach { row ->
                TestDetailRow(row)
            }
        }
        Spacer(Modifier.height(16.dp))
        PrimaryGradientButton(
            text = "Domů",
            onClick = {
                navController.navigate(Route.Home.route) {
                    popUpTo(Route.Home.route) { inclusive = true }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        )
        Spacer(Modifier.height(12.dp))
        PrimaryGradientButton(
            text = "Zpět na zkoušku",
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        )
        Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TestDetailRow(detail: TestResultRowUi) {
    var expanded by remember { mutableStateOf(false) }
    GlassCard(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { expanded = !expanded },
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (detail.correct) Icons.Default.Check else Icons.Default.Close,
                    contentDescription = null,
                    tint = if (detail.correct) SuccessGreen else ErrorRed,
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = detail.questionText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = if (expanded) Int.MAX_VALUE else 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = detail.pointsLabel,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            AnimatedVisibility(visible = expanded) {
                Text(
                    text = "Tvoje odpověď: ${detail.userAnswerLabel}\nSprávně: ${detail.correctAnswerLabel}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}

private class TestResultsViewModelFactory(
    private val application: Application,
    private val attemptId: Long,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TestResultsViewModel(application, attemptId) as T
    }
}
