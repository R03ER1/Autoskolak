package cz.autokolk.ui.screens.onboarding

import androidx.compose.ui.graphics.Color
import cz.autokolk.ui.theme.AccentBlue
import cz.autokolk.ui.theme.AccentCyan
import cz.autokolk.ui.theme.AccentTeal
import cz.autokolk.ui.theme.WarningAmber

/** Počet stránek v onboardingu (4 úvodní + cíl + tempo + jméno + demo). */
internal const val ONBOARDING_PAGE_COUNT = 8

internal data class OnboardingIntroPage(
    val title: String,
    val description: String,
    /** Cesta k assetu v [android.content.res.AssetManager], např. `lottie/onboarding_welcome.json`. */
    val lottieAsset: String,
    val accentColor: Color,
)

internal val onboardingIntroPages: List<OnboardingIntroPage> = listOf(
    OnboardingIntroPage(
        title = "Vítej v Autoškoláku!",
        description = "Připrav se na zkoušku hravě a rychle.",
        lottieAsset = "lottie/onboarding_welcome.json",
        accentColor = AccentCyan,
    ),
    OnboardingIntroPage(
        title = "Tohle je Alex",
        description = "Tvůj lev, který potřebuje tvoji pomoc. Uč se a nakrmíš ho!",
        lottieAsset = "lottie/onboarding_alex.json",
        accentColor = AccentTeal,
    ),
    OnboardingIntroPage(
        title = "Sbírej body",
        description = "Za každou dokončenou lekci získáš body a prodloužíš streak.",
        lottieAsset = "lottie/onboarding_points.json",
        accentColor = WarningAmber,
    ),
    OnboardingIntroPage(
        title = "Zvládni zkoušku",
        description = "Až budeš připraven, vyzkoušej si ostrý test jako na úřadě.",
        lottieAsset = "lottie/onboarding_test.json",
        accentColor = AccentBlue,
    ),
)

/** Skupiny řidičáků (filtrace otázek v budoucnu). */
internal val licenseOptions = listOf("AM", "A", "B", "C", "D", "T")

internal data class DailyGoalOption(
    val lessonsPerDay: Int,
    val label: String,
    val emoji: String,
)

internal val dailyGoalOptions: List<DailyGoalOption> = listOf(
    DailyGoalOption(1, "Pohoda", "🐢"),
    DailyGoalOption(3, "Normální", "🐇"),
    DailyGoalOption(5, "Intenzivní", "🔥"),
    DailyGoalOption(10, "Šílený", "💀"),
)

data class OnboardingDraft(
    val selectedLicense: String = "B",
    val dailyGoalLessons: Int = 3,
    val lionName: String = "Alex",
)
