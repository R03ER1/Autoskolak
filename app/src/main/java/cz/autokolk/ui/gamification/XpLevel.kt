package cz.autokolk.ui.gamification

/**
 * Jednoduchý převod bodů na úroveň (krok 123 — základ pro rozšíření).
 */
object XpLevel {
    const val POINTS_PER_LEVEL = 500

    fun levelFromTotalPoints(points: Int): Int = (points.coerceAtLeast(0) / POINTS_PER_LEVEL) + 1

    fun progressWithinLevel(points: Int): Float {
        val p = points.coerceAtLeast(0) % POINTS_PER_LEVEL
        return p / POINTS_PER_LEVEL.toFloat()
    }
}
