package cz.autokolk

/**
 * Globální XP / úrovně — tabulka prahů a pomocné výpočty.
 */
data class XpLevel(val level: Int, val title: String, val minXp: Int)

data class LevelUpPending(
    val level: Int,
    val title: String,
    val bonusCoins: Int,
)

data class XpGrantResult(
    val xpAdded: Int,
    val totalXpAfter: Int,
    val levelBefore: XpLevel,
    val levelAfter: XpLevel,
    val leveledUp: Boolean,
    /** Bonusové peníze při přechodu na vyšší úroveň (0 pokud žádný level-up). */
    val levelUpBonusCoins: Int,
) {
    companion object {
        fun none(totalXp: Int): XpGrantResult {
            val lv = XpSystem.levelForTotalXp(totalXp)
            return XpGrantResult(0, totalXp, lv, lv, false, 0)
        }
    }
}

object XpSystem {

    val levels: List<XpLevel> = listOf(
        XpLevel(1, "Začátečník", 0),
        XpLevel(2, "Učeň", 100),
        XpLevel(3, "Student", 300),
        XpLevel(4, "Řidič-junior", 600),
        XpLevel(5, "Řidič", 1000),
        XpLevel(6, "Řidič+", 1500),
        XpLevel(7, "Pokročilý", 2200),
        XpLevel(8, "Zkušený", 3000),
        XpLevel(9, "Expert", 4000),
        XpLevel(10, "Mistr teorie", 5200),
        XpLevel(11, "Šampion", 6600),
        XpLevel(12, "Elita", 8200),
        XpLevel(13, "Profík", 10000),
        XpLevel(14, "Veterán", 12000),
        XpLevel(15, "Legenda silnic", 14500),
        XpLevel(16, "As", 17500),
        XpLevel(17, "Šampión", 21000),
        XpLevel(18, "Mistr", 25000),
        XpLevel(19, "Velmistr", 30000),
        XpLevel(20, "Mistr volantu", 36000),
    )

    fun levelForTotalXp(totalXp: Int): XpLevel {
        val xp = totalXp.coerceAtLeast(0)
        var current = levels.first()
        for (l in levels) {
            if (xp >= l.minXp) current = l
        }
        return current
    }

    /** 0f–1f postup v rámci aktuální úrovně směrem k další (u max. úrovně 1f). */
    fun progressWithinLevel(totalXp: Int): Float {
        val xp = totalXp.coerceAtLeast(0)
        val current = levelForTotalXp(xp)
        val next = levels.firstOrNull { it.level == current.level + 1 }
        if (next == null) return 1f
        val span = (next.minXp - current.minXp).coerceAtLeast(1)
        val inLevel = xp - current.minXp
        return (inLevel.toFloat() / span.toFloat()).coerceIn(0f, 1f)
    }

    fun xpToNextLevel(totalXp: Int): Int {
        val xp = totalXp.coerceAtLeast(0)
        val current = levelForTotalXp(xp)
        val next = levels.firstOrNull { it.level == current.level + 1 } ?: return 0
        return (next.minXp - xp).coerceAtLeast(0)
    }

    fun bonusCoinsForLevelUp(newLevel: Int): Int = when {
        newLevel <= 5 -> 15
        newLevel <= 10 -> 25
        newLevel <= 15 -> 40
        else -> 60
    }
}

/** Odměny XP za typy akcí (základ bez násobičů). */
object XpRewardTable {

    fun lessonXp(correctAnswers: Int, totalQuestions: Int, isReview: Boolean): Int {
        if (totalQuestions <= 0) return 0
        val pct = (correctAnswers * 100) / totalQuestions
        val base = when {
            isReview -> if (pct == 100) 12 else if (pct >= 65) 8 else 5
            else -> when {
                pct == 100 -> 25
                pct >= 65 -> 18
                pct >= 35 -> 12
                else -> 8
            }
        }
        return base
    }

    fun testXp(weightedScore0to50: Int): Int = (weightedScore0to50.coerceIn(0, 50) * 3) / 5

    fun streakFirstLessonOfDay(): Int = 5

    fun feedAlex(): Int = 4

    fun practiceBucketXp(): Int = 2

    fun achievementBonus(): Int = 35
}
