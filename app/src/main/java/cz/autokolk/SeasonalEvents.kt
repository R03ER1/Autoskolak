package cz.autokolk

import java.util.Calendar

/** Jednoduché sezónní bannery (bez serveru). */
object SeasonalEvents {

    fun activeMessage(): String? {
        val c = Calendar.getInstance()
        val m = c.get(Calendar.MONTH) + 1
        val d = c.get(Calendar.DAY_OF_MONTH)
        return when {
            m == 12 && d >= 20 -> "🎄 Sváteční výzvy: sbírej mince a XP!"
            m == 1 && d <= 15 -> "🎆 Novoroční motivace — drž streak!"
            m == 10 && d == 28 -> "🎃 Sezónní bonusy v obchodě!"
            else -> null
        }
    }
}
