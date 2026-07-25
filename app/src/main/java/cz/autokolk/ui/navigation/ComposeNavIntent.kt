package cz.autokolk.ui.navigation

/** Intent extra pro [cz.autokolk.ComposeMainActivity] — otevře záložku v Compose navigaci. */
object ComposeNavIntent {
    const val EXTRA_OPEN_TAB = "cz.autokolk.extra.OPEN_TAB"

    /** Hodnota extra: otevřít Alex (krmení z notifikace). */
    const val OPEN_TAB_ALEX = "alex"

    /** Otevřít záložku Nastavení (např. z legacy Home). */
    const val OPEN_TAB_SETTINGS = "settings"

    /** Hodnota extra: otevřít týdenní souhrn (z notifikace WeeklySummaryWorker). */
    const val OPEN_TAB_WEEKLY_XP = "weekly_xp"

    /** Otevřít záložku Procvičování (fallback za zrušenou legacy [cz.autokolk.PracticeActivity], krok 153). */
    const val OPEN_TAB_PRACTICE = "practice"

    /** Otevřít záložku Test (fallback za zrušenou legacy [cz.autokolk.TestAttemptActivity], krok 153). */
    const val OPEN_TAB_TEST = "test"

    /** Otevřít modální obrazovku streaku (fallback za zrušenou legacy [cz.autokolk.StreakActivity], krok 153). */
    const val OPEN_TAB_STREAK = "streak"

    /**
     * Otevřít obrazovku výsledků lekce s reálnými parametry (fallback za zrušenou legacy
     * [cz.autokolk.ResultsActivity], krok 153). Doplňkové extra klíče níže.
     */
    const val OPEN_RESULTS = "results"

    const val EXTRA_RESULTS_LESSON_ID = "cz.autokolk.extra.RESULTS_LESSON_ID"
    const val EXTRA_RESULTS_SCORE = "cz.autokolk.extra.RESULTS_SCORE"
    const val EXTRA_RESULTS_TOTAL = "cz.autokolk.extra.RESULTS_TOTAL"
    const val EXTRA_RESULTS_FIRST_OF_DAY = "cz.autokolk.extra.RESULTS_FIRST_OF_DAY"
    const val EXTRA_RESULTS_POINTS_AWARDED = "cz.autokolk.extra.RESULTS_POINTS_AWARDED"
    const val EXTRA_RESULTS_FROM_PRACTICE = "cz.autokolk.extra.RESULTS_FROM_PRACTICE"
}
