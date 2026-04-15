package cz.autokolk.ui.screens.reading

/**
 * Mimořádné ukončení čtecí lekce místo navigace na [cz.autokolk.ui.navigation.Route.Quiz] v Compose.
 */
data class ReadingLessonExternalExit(
    /** Z [MainActivity] in-place náhled — jen zavřít overlay. */
    val onDismissEmbedded: (() -> Unit)? = null,
    /** Z [cz.autokolk.autokolk.HomeActivity] — po přečtení spustit [cz.autokolk.autokolk.MainActivity]. */
    val onOpenMainActivity: ((lessonNumber: Int, isReview: Boolean, displayLessonNumber: Int?) -> Unit)? = null,
)
