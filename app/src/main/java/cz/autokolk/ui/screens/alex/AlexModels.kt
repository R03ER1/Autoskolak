package cz.autokolk.ui.screens.alex

/** Nálada podle REDESIGN (krok 95), odvozena od sytosti 0–100. */
enum class AlexMood {
    /** Nejvyšší stupeň radosti (historicky hunger 81–100 % → `AlexCool.png`). */
    Cool,
    Happy,
    Neutral,
    Hungry,
    Starving,
}

fun hungerPercentToMood(percent: Int): AlexMood = when {
    percent >= 81 -> AlexMood.Cool
    percent >= 80 -> AlexMood.Happy
    percent >= 50 -> AlexMood.Neutral
    percent >= 20 -> AlexMood.Hungry
    else -> AlexMood.Starving
}

fun moodTitle(mood: AlexMood): String = when (mood) {
    AlexMood.Cool -> "Naprosto spokojený lev"
    AlexMood.Happy -> "Spokojený lev"
    AlexMood.Neutral -> "Lev v pohodě"
    AlexMood.Hungry -> "Hladový lev"
    AlexMood.Starving -> "Velmi hladový lev"
}

enum class AlexFeedKind {
    /** Obyčejné jídlo: +hungerDelta */
    Delta,

    /** Pivo: okamžitě na max sytost */
    FullMax,

    /** Kámen: jen zmrazení úbytku */
    FreezeDecay,
}

data class AlexFoodItem(
    val achievementKey: String,
    val displayName: String,
    /** Cesta v assets, např. `images/sausage.png` */
    val assetImagePath: String,
    val priceCoins: Int,
    val hungerDelta: Int,
    val kind: AlexFeedKind,
)

val DefaultAlexFoodMenu: List<AlexFoodItem> = listOf(
    AlexFoodItem("klobaska", "Klobása", "images/sausage.png", 4, 1, AlexFeedKind.Delta),
    AlexFoodItem("kure", "Kuře", "images/chicken.png", 30, 10, AlexFeedKind.Delta),
    AlexFoodItem("zmrzlina", "Zmrzlina", "images/IceCream.png", 10, 3, AlexFeedKind.Delta),
    AlexFoodItem("mrkev", "Mrkev", "images/carrot.png", 16, 5, AlexFeedKind.Delta),
    AlexFoodItem("pivo", "Pivo", "images/beer.png", 150, 0, AlexFeedKind.FullMax),
    AlexFoodItem("kameni", "Kámen", "images/stone.png", 80, 0, AlexFeedKind.FreezeDecay),
)

enum class AlexFeedAnimationPhase {
    Idle,
    Flying,
    Bouncing,
    Done,
}
