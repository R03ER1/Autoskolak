package cz.autokolk

/**
 * Krok 142 — jednoduché intervalové opakování (spaced repetition) pro chybné otázky
 * ("Tvoje chyby" / [LessonProgress.CATEGORY_USER_MISTAKES]).
 *
 * Záměrně NEJDE o plný SM-2 algoritmus — jen fixní posloupnost intervalů podobná
 * Leitner boxům, aby byla logika jednoduchá, čitelná a snadno testovatelná:
 *
 * - Otázka, na kterou uživatel chybuje, je vždy okamžitě k dispozici (stage 0 / bez záznamu).
 * - Po každé správné odpovědi na otázku, která byla chybná, se posune do vyšší "stage"
 *   a naplánuje se další zobrazení až po delším intervalu ([INTERVAL_DAYS]: 1 / 3 / 7 dní).
 * - Po špatné odpovědi se vrátí zpět na stage 0 (znovu okamžitě k dispozici).
 * - Po úspěšném zvládnutí celé posloupnosti intervalů (3 správné odpovědi v narůstajících
 *   intervalech) je otázka "graduated" — zmizí z fronty k revizi úplně.
 *
 * Tato třída je čistě funkční (bez Android/Context závislostí) — [LessonProgress] pouze
 * persistuje výsledný [ReviewScheduleEntry] a volá sem pro rozhodnutí o dalším kroku.
 */
data class ReviewScheduleEntry(
    val stage: Int = 0,
    val nextReviewAtMs: Long = 0L,
)

object MistakeReviewScheduler {
    /** Intervaly ve dnech pro stage 1, 2, 3 (index 0 = interval naplánovaný po 1. úspěchu). */
    val INTERVAL_DAYS = longArrayOf(1L, 3L, 7L)

    const val DAY_MS: Long = 24L * 60L * 60L * 1000L

    /**
     * Otázka je „due“ (má se dnes/nyní nabídnout k revizi), pokud pro ni neexistuje žádný
     * záznam (nová chyba nebo stará data bez tohoto pole — bezpečná migrace na "ihned k dispozici")
     * nebo pokud už uplynul naplánovaný čas dalšího opakování.
     */
    fun isDue(entry: ReviewScheduleEntry?, nowMs: Long): Boolean {
        return entry == null || entry.nextReviewAtMs <= nowMs
    }

    /**
     * Po správné odpovědi na opakovanou (dříve chybnou) otázku: posune stage nahoru
     * a vrátí nový záznam s delším intervalem do dalšího zobrazení.
     *
     * @return nový [ReviewScheduleEntry], nebo `null` pokud otázka právě "graduated"
     *   (zvládla celou posloupnost intervalů) — v tom případě se má záznam odstranit
     *   a otázka se už k revizi nenabízí.
     */
    fun onCorrectAnswer(current: ReviewScheduleEntry?, nowMs: Long): ReviewScheduleEntry? {
        val newStage = (current?.stage ?: 0) + 1
        if (newStage > INTERVAL_DAYS.size) return null
        val intervalMs = INTERVAL_DAYS[newStage - 1] * DAY_MS
        return ReviewScheduleEntry(stage = newStage, nextReviewAtMs = nowMs + intervalMs)
    }

    /**
     * Po špatné odpovědi se otázka vrací na stage 0 — reprezentováno chybějícím záznamem
     * (viz [isDue], `null` znamená okamžitou dostupnost).
     */
    fun onWrongAnswer(): ReviewScheduleEntry? = null
}
