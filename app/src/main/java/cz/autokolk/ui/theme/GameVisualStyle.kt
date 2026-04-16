package cz.autokolk.ui.theme

/**
 * Kosmetický motiv aplikace (barvy Material 3, typografie, tvary karet).
 * Ukládá se v [cz.autokolk.LessonProgress].
 */
enum class GameVisualStyle(
    val storageId: String,
    val titleCs: String,
    val subtitleCs: String,
    /** null = zdarma součástí aplikace */
    val priceCoins: Int?,
) {
    CLASSIC(
        storageId = "classic",
        titleCs = "Klasický Autoškolák",
        subtitleCs = "Původní vzhled — zdarma",
        priceCoins = null,
    ),
    NEON_GRID(
        storageId = "neon_grid",
        titleCs = "Neon mřížka",
        subtitleCs = "Chladné akcenty, ostřejší karty",
        priceCoins = 1200,
    ),
    SUNSET_WARM(
        storageId = "sunset_warm",
        titleCs = "Západ slunce",
        subtitleCs = "Teplé barvy, měkčí tvary",
        priceCoins = 900,
    ),
    ;

    fun isFree(): Boolean = priceCoins == null

    companion object {
        fun fromStorageId(id: String?): GameVisualStyle {
            if (id.isNullOrBlank()) return CLASSIC
            return entries.find { it.storageId == id } ?: CLASSIC
        }
    }
}
