package cz.autokolk.ui.screens.home

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cz.autokolk.DailyChallengeUi
import cz.autokolk.GlobalLesson
import cz.autokolk.LessonProgress
import cz.autokolk.RandomEventManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    val lessonProgress = LessonProgress(application)
    private val randomEventManager = RandomEventManager(application.applicationContext)

    private val _pathRows = MutableStateFlow<List<HomePathRow>>(emptyList())
    val pathRows: StateFlow<List<HomePathRow>> = _pathRows.asStateFlow()

    private val _reorderedPlan = MutableStateFlow<List<GlobalLesson>>(emptyList())
    val reorderedPlan: StateFlow<List<GlobalLesson>> = _reorderedPlan.asStateFlow()

    private val _randomEvent = MutableStateFlow<RandomEventManager.RandomEventPresentation?>(null)
    val randomEvent: StateFlow<RandomEventManager.RandomEventPresentation?> = _randomEvent.asStateFlow()

    private val _dailyChallenges = MutableStateFlow<List<DailyChallengeUi>>(emptyList())
    val dailyChallenges: StateFlow<List<DailyChallengeUi>> = _dailyChallenges.asStateFlow()

    private val prefsListener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
        refresh()
    }

    init {
        lessonProgress.registerOnLessonProgressChanged(prefsListener)
        refresh()
    }

    override fun onCleared() {
        lessonProgress.unregisterOnLessonProgressChanged(prefsListener)
        super.onCleared()
    }

    fun refresh() {
        viewModelScope.launch {
            lessonProgress.normalizeStreakForToday()
            try {
                lessonProgress.grantDailyLoginIfNeeded()
            } catch (_: Throwable) {
            }
            val plan = lessonProgress.getGlobalLessonPlan()
            val reordered = HomeLessonOrdering.reorderPlan(plan)
            _reorderedPlan.value = reordered
            _pathRows.value = HomePathListBuilder.buildRows(lessonProgress, reordered)
            _dailyChallenges.value = lessonProgress.snapshotDailyChallenges()
        }
    }

    fun displayNumberForLesson(lessonNumber: Int): Int {
        val reordered = _reorderedPlan.value
        val idx = reordered.indexOfFirst { it.lessonNumber == lessonNumber }
        return if (idx >= 0) idx + 1 else lessonNumber
    }

    fun canStartLesson(lessonNumber: Int): Boolean {
        val tried = lessonProgress.getLessonState(lessonNumber).completed
        val hasAnyProgress = lessonProgress.getAllLessonStates().isNotEmpty()
        val nextAllowed = if (hasAnyProgress) {
            lessonProgress.getNextAvailableLesson()
        } else {
            _reorderedPlan.value.firstOrNull()?.lessonNumber ?: 1
        }
        return tried || lessonNumber == nextAllowed
    }

    fun hasHeartsOrInfinite(): Boolean {
        val prefs = getApplication<Application>().getSharedPreferences("lesson_progress", android.content.Context.MODE_PRIVATE)
        val infinite = prefs.getBoolean("infinite_lives", false)
        return infinite || lessonProgress.getCurrentHearts() > 0
    }

    fun tryShowRandomEventIfDue() {
        if (_randomEvent.value != null) return
        val presentation = randomEventManager.consumeDueRandomEventForCompose(lessonProgress) ?: return
        _randomEvent.value = presentation
    }

    fun dismissRandomEvent() {
        if (_randomEvent.value == null) return
        randomEventManager.markComposeEventDismissed()
        _randomEvent.value = null
    }
}
