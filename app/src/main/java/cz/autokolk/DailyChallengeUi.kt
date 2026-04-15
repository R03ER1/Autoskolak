package cz.autokolk

data class DailyChallengeUi(
    val id: String,
    val title: String,
    /** 0f–1f */
    val progress: Float,
    val done: Boolean,
    val rewardXp: Int,
)
