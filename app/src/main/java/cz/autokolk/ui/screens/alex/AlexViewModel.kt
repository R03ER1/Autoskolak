package cz.autokolk.ui.screens.alex

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cz.autokolk.AchievementsManager
import cz.autokolk.HungerManager
import cz.autokolk.LessonProgress
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AlexState(
    val lionName: String = "Alex",
    val title: String = moodTitle(AlexMood.Happy),
    val hungerPercent: Int = 100,
    val mood: AlexMood = AlexMood.Happy,
    /** Zobrazení „cool“ varianty (brýle zapnuté a vlastněné). */
    val hasSunglassesVisual: Boolean = false,
    val sunglassesOwned: Boolean = false,
    val isFrozen: Boolean = false,
    val coins: Int = 0,
    val foodItems: List<AlexFoodItem> = DefaultAlexFoodMenu,
    val showFoodMenu: Boolean = false,
    val showShop: Boolean = false,
    val feedPhase: AlexFeedAnimationPhase = AlexFeedAnimationPhase.Idle,
    val feedFoodAssetPath: String? = null,
    /** Útrata za poslední krmení (pro FloatingReward). */
    val lastSpendRewardCoins: Int? = null,
    val bounceTrigger: Long = 0L,
    val heartParticlesTrigger: Long = 0L,
    val snackMessage: String? = null,
)

class AlexViewModel(application: Application) : AndroidViewModel(application) {

    private val hungerManager = HungerManager(application)
    private val lessonProgress = LessonProgress(application)

    private val _state = MutableStateFlow(AlexState())
    val state: StateFlow<AlexState> = _state.asStateFlow()

    private val hungerPrefs = application.getSharedPreferences("hunger_prefs", Application.MODE_PRIVATE)

    private val hungerPrefsListener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
        refreshState()
    }

    private val lessonPrefsListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == null ||
            key == "lion_name" ||
            key == "total_points" ||
            key.startsWith("accessory_")
        ) {
            refreshState()
        }
    }

    init {
        hungerPrefs.registerOnSharedPreferenceChangeListener(hungerPrefsListener)
        lessonProgress.registerOnLessonProgressChanged(lessonPrefsListener)
        refreshState()
    }

    override fun onCleared() {
        hungerPrefs.unregisterOnSharedPreferenceChangeListener(hungerPrefsListener)
        lessonProgress.unregisterOnLessonProgressChanged(lessonPrefsListener)
        super.onCleared()
    }

    fun refreshState() {
        val h = hungerManager.getCurrentHunger()
        val mood = hungerPercentToMood(h)
        _state.update { s ->
            s.copy(
                lionName = lessonProgress.getLionName(),
                title = moodTitle(mood),
                hungerPercent = h,
                mood = mood,
                hasSunglassesVisual = lessonProgress.isSunglassesEnabled(),
                sunglassesOwned = lessonProgress.hasSunglasses(),
                isFrozen = hungerManager.isFrozenNow(),
                coins = lessonProgress.getTotalPoints(),
            )
        }
    }

    fun openFoodMenu() {
        _state.update { it.copy(showFoodMenu = true, snackMessage = null) }
    }

    fun closeFoodMenu() {
        _state.update { it.copy(showFoodMenu = false) }
    }

    fun openShop() {
        _state.update { it.copy(showShop = true, snackMessage = null) }
    }

    fun closeShop() {
        _state.update { it.copy(showShop = false) }
    }

    fun clearSnack() {
        _state.update { it.copy(snackMessage = null) }
    }

    fun setSunglassesEnabled(enabled: Boolean) {
        lessonProgress.setSunglassesEnabled(enabled)
        refreshState()
    }

    /** @return true při úspěchu */
    fun buySunglasses(): Boolean {
        val ok = lessonProgress.buySunglassesIfAffordable(1000)
        refreshState()
        if (!ok) {
            _state.update { it.copy(snackMessage = "Nedostatek bodů") }
        }
        return ok
    }

    fun feed(item: AlexFoodItem) {
        if (_state.value.feedPhase != AlexFeedAnimationPhase.Idle) return
        viewModelScope.launch {
            val h0 = hungerManager.getCurrentHunger()
            when (item.kind) {
                AlexFeedKind.Delta -> {
                    if (h0 + item.hungerDelta > HungerManager.MAX_HUNGER) {
                        _state.update { it.copy(snackMessage = "Nelze přes 100") }
                        return@launch
                    }
                    if (!lessonProgress.spendPoints(item.priceCoins)) {
                        _state.update { it.copy(snackMessage = "Nedostatek bodů") }
                        return@launch
                    }
                }
                AlexFeedKind.FullMax -> {
                    if (h0 >= HungerManager.MAX_HUNGER) {
                        _state.update { it.copy(snackMessage = "Už na maximu") }
                        return@launch
                    }
                    if (!lessonProgress.spendPoints(item.priceCoins)) {
                        _state.update { it.copy(snackMessage = "Nedostatek bodů") }
                        return@launch
                    }
                }
                AlexFeedKind.FreezeDecay -> {
                    if (!lessonProgress.spendPoints(item.priceCoins)) {
                        _state.update { it.copy(snackMessage = "Nedostatek bodů") }
                        return@launch
                    }
                }
            }

            _state.update {
                it.copy(
                    feedPhase = AlexFeedAnimationPhase.Flying,
                    feedFoodAssetPath = item.assetImagePath,
                    lastSpendRewardCoins = item.priceCoins,
                    showFoodMenu = false,
                )
            }
            delay(320)

            when (item.kind) {
                AlexFeedKind.Delta -> {
                    hungerManager.setCurrentHunger(
                        (h0 + item.hungerDelta).coerceAtMost(HungerManager.MAX_HUNGER),
                    )
                }
                AlexFeedKind.FullMax -> {
                    hungerManager.setCurrentHunger(HungerManager.MAX_HUNGER)
                }
                AlexFeedKind.FreezeDecay -> {
                    hungerManager.freezeDecayForHours(48)
                }
            }
            try {
                AchievementsManager(getApplication()).onFed(item.achievementKey)
            } catch (_: Throwable) {
            }

            val animKey = System.currentTimeMillis()
            _state.update {
                it.copy(
                    feedPhase = AlexFeedAnimationPhase.Bouncing,
                    bounceTrigger = animKey,
                    heartParticlesTrigger = animKey,
                )
            }
            delay(550)

            _state.update { it.copy(feedPhase = AlexFeedAnimationPhase.Done) }
            delay(200)
            refreshState()
            _state.update {
                it.copy(
                    feedPhase = AlexFeedAnimationPhase.Idle,
                    feedFoodAssetPath = null,
                )
            }
        }
    }

    fun dismissFeedSpendPopup() {
        _state.update { it.copy(lastSpendRewardCoins = null) }
    }

    companion object {
        private val SYMBOL_OTHER = Regex("\\p{So}")
        private val NAME_ALLOWED = Regex("^[\\p{L}\\p{N}\\s'.-]{1,20}$")

        fun isValidLionName(name: String): Boolean {
            val t = name.trim()
            if (t.length !in 1..20) return false
            if (SYMBOL_OTHER.containsMatchIn(t)) return false
            if (!NAME_ALLOWED.matches(t)) return false
            return true
        }
    }

    fun renameLion(newName: String): Boolean {
        if (!isValidLionName(newName)) return false
        lessonProgress.setLionName(newName.trim())
        refreshState()
        return true
    }
}
