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
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class AlexUiState(
    val lionName: String = "Alex",
    val hunger: Int = HungerManager.MAX_HUNGER,
    val points: Int = 0,
    val frozenLabel: String? = null,
    val snackMessage: String? = null,
    val feedAnimToken: Int = 0,
)

class AlexViewModel(application: Application) : AndroidViewModel(application) {

    private val hungerManager = HungerManager(application)
    private val lessonProgress = LessonProgress(application)
    private val prefs = application.getSharedPreferences("lesson_progress", android.content.Context.MODE_PRIVATE)
    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
        refresh()
    }

    private val _state = MutableStateFlow(AlexUiState())
    val state: StateFlow<AlexUiState> = _state.asStateFlow()

    init {
        prefs.registerOnSharedPreferenceChangeListener(listener)
        refresh()
        viewModelScope.launch {
            while (isActive) {
                delay(30_000)
                refresh()
            }
        }
    }

    override fun onCleared() {
        prefs.unregisterOnSharedPreferenceChangeListener(listener)
        super.onCleared()
    }

    fun refresh() {
        val hm = HungerManager(getApplication())
        val h = hm.getCurrentHunger()
        val frozen = if (hm.isFrozenNow()) {
            val now = System.currentTimeMillis()
            val until = hm.getFreezeUntilEpochMillis()
            val remaining = (until - now).coerceAtLeast(0L)
            val hours = (remaining / 3_600_000L).toInt()
            val minutes = ((remaining % 3_600_000L) / 60_000L).toInt()
            "Hladovění začne za ${hours} hod a ${minutes} min"
        } else {
            null
        }
        _state.update {
            it.copy(
                lionName = prefs.getString("lion_name", "Alex") ?: "Alex",
                hunger = h,
                points = lessonProgress.getTotalPoints(),
                frozenLabel = frozen,
            )
        }
    }

    fun setLionName(name: String) {
        val clean = name.trim().ifEmpty { "Alex" }
        prefs.edit().putString("lion_name", clean).apply()
        refresh()
    }

    fun clearSnack() {
        _state.update { it.copy(snackMessage = null) }
    }

    fun purchaseFood(hungerDelta: Int, cost: Int, foodKey: String, successMsg: String) {
        val h = hungerManager.getCurrentHunger()
        if (h + hungerDelta > HungerManager.MAX_HUNGER) {
            _state.update { it.copy(snackMessage = "Nelze přes 100") }
            return
        }
        if (!lessonProgress.spendPoints(cost)) {
            _state.update { it.copy(snackMessage = "Nedostatek bodů") }
            return
        }
        hungerManager.setCurrentHunger((h + hungerDelta).coerceAtMost(HungerManager.MAX_HUNGER))
        try {
            AchievementsManager(getApplication()).onFed(foodKey)
        } catch (_: Throwable) {
        }
        _state.update {
            it.copy(
                snackMessage = successMsg,
                feedAnimToken = it.feedAnimToken + 1,
            )
        }
        refresh()
    }

    fun purchaseMaxBeer() {
        val h = hungerManager.getCurrentHunger()
        if (h >= HungerManager.MAX_HUNGER) {
            _state.update { it.copy(snackMessage = "Už na maximu") }
            return
        }
        if (!lessonProgress.spendPoints(150)) {
            _state.update { it.copy(snackMessage = "Nedostatek bodů") }
            return
        }
        hungerManager.setCurrentHunger(HungerManager.MAX_HUNGER)
        try {
            AchievementsManager(getApplication()).onFed("pivo")
        } catch (_: Throwable) {
        }
        _state.update { it.copy(snackMessage = "+ MAX", feedAnimToken = it.feedAnimToken + 1) }
        refresh()
    }

    fun purchaseFreeze() {
        if (!lessonProgress.spendPoints(80)) {
            _state.update { it.copy(snackMessage = "Nedostatek bodů") }
            return
        }
        hungerManager.freezeDecayForHours(48)
        try {
            AchievementsManager(getApplication()).onFed("kameni")
        } catch (_: Throwable) {
        }
        _state.update { it.copy(snackMessage = "+ 48h bez hladu") }
        refresh()
    }
}
