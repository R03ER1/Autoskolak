package cz.autokolk.ui.screens.onboarding

import android.os.Build
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.filled.DirectionsCarFilled
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import cz.autokolk.ui.theme.AccentBlue
import cz.autokolk.ui.theme.AccentCyan
import cz.autokolk.ui.theme.AccentTeal
import cz.autokolk.ui.theme.WarningAmber

sealed class OnboardingStep {
    data class InfoPage(
        val title: String,
        val description: String,
        val lottieAssetPath: String,
        val accentColor: Color,
        /**
         * Statická náhrada za lottie animaci, použije se, pokud je [lottieAssetPath]
         * (ještě) jen prázdný placeholder bez vrstev — viz `OnboardingInfoPage`.
         * `null` pro stránku s Alexem, kde se místo ikony ukáže bitmapa lva.
         */
        val fallbackIcon: ImageVector? = null,
    ) : OnboardingStep()

    data object DailyGoalPage : OnboardingStep()
    data object NameLionPage : OnboardingStep()
    data object DemoQuestionPage : OnboardingStep()
    data object NotificationPage : OnboardingStep()
}

fun buildOnboardingSteps(): List<OnboardingStep> = buildList {
    add(
        OnboardingStep.InfoPage(
            title = "Vítej v Autoškoláku!",
            description = "Připrav se na zkoušku hravě a rychle.",
            lottieAssetPath = "lottie/onboarding_welcome.json",
            accentColor = AccentCyan,
            fallbackIcon = Icons.Filled.DirectionsCarFilled,
        ),
    )
    add(
        OnboardingStep.InfoPage(
            title = "Tohle je Alex",
            description = "Tvůj lev, který potřebuje tvoji pomoc. Uč se a nakrm ho!",
            lottieAssetPath = "lottie/onboarding_alex.json",
            accentColor = AccentTeal,
            fallbackIcon = null,
        ),
    )
    add(
        OnboardingStep.InfoPage(
            title = "Sbírej body",
            description = "Za každou lekci získáš body a prodloužíš svůj streak.",
            lottieAssetPath = "lottie/onboarding_points.json",
            accentColor = WarningAmber,
            fallbackIcon = Icons.Filled.EmojiEvents,
        ),
    )
    add(
        OnboardingStep.InfoPage(
            title = "Zvládni zkoušku",
            description = "Až budeš připraven, vyzkoušej si ostrý test.",
            lottieAssetPath = "lottie/onboarding_test.json",
            accentColor = AccentBlue,
            fallbackIcon = Icons.AutoMirrored.Filled.FactCheck,
        ),
    )
    add(OnboardingStep.DailyGoalPage)
    add(OnboardingStep.NameLionPage)
    add(OnboardingStep.DemoQuestionPage)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        add(OnboardingStep.NotificationPage)
    }
}

fun OnboardingStep.accentOrDefault(): Color = when (this) {
    is OnboardingStep.InfoPage -> accentColor
    else -> AccentCyan
}
