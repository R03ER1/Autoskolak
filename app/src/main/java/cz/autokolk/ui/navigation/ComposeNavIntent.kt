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
}
