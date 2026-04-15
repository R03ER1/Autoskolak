package cz.autokolk.ui.screens.practice

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cz.autokolk.CategoryGroup
import cz.autokolk.LessonProgress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class PracticeSubcategoryRow(
    val code: String,
    val title: String,
    val questionCount: Int,
)

data class PracticeCategoryUi(
    val code: String,
    val title: String,
    val subcategories: List<PracticeSubcategoryRow>,
    val progress: Float,
    val totalCount: Int,
    val answeredCount: Int,
    val completed: Boolean,
    val percentCorrect: Int,
    val attemptCount: Int,
    val worstQuestionIds: List<String>,
)

data class PracticeMistakesUi(
    val wrongCount: Int,
    val correctCount: Int,
)

data class PracticeSearchHitUi(
    val id: String,
    val categoryCode: String,
    val categoryTitle: String,
    val preview: String,
    val imagePath: String?,
)

@OptIn(FlowPreview::class)
class PracticeViewModel(application: Application) : AndroidViewModel(application) {

    val lessonProgress = LessonProgress(application)

    private val _categories = MutableStateFlow<List<PracticeCategoryUi>>(emptyList())
    val categories: StateFlow<List<PracticeCategoryUi>> = _categories.asStateFlow()

    private val _mistakes = MutableStateFlow(PracticeMistakesUi(0, 0))
    val mistakes: StateFlow<PracticeMistakesUi> = _mistakes.asStateFlow()

    private val _filterMode = MutableStateFlow(PracticeMode.ALL)
    val filterMode: StateFlow<Int> = _filterMode.asStateFlow()

    private val _expanded = MutableStateFlow<Set<String>>(emptySet())
    val expanded: StateFlow<Set<String>> = _expanded.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchHits = MutableStateFlow<List<PracticeSearchHitUi>>(emptyList())
    val searchHits: StateFlow<List<PracticeSearchHitUi>> = _searchHits.asStateFlow()

    private val prefsListener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
        refresh()
    }

    init {
        lessonProgress.registerOnLessonProgressChanged(prefsListener)
        refresh()
        _searchQuery
            .debounce(280)
            .distinctUntilChanged()
            .onEach { q -> runSearch(q) }
            .launchIn(viewModelScope)
    }

    override fun onCleared() {
        lessonProgress.unregisterOnLessonProgressChanged(prefsListener)
        super.onCleared()
    }

    fun setFilter(mode: Int) {
        _filterMode.value = mode
    }

    fun setSearchQuery(q: String) {
        _searchQuery.value = q
    }

    fun toggleExpanded(categoryCode: String) {
        _expanded.update { cur ->
            if (categoryCode in cur) cur - categoryCode else cur + categoryCode
        }
    }

    fun refresh() {
        viewModelScope.launch {
            lessonProgress.normalizeStreakForToday()
            val groups = loadOrderedGroups()
            _categories.value = groups.map { g -> toUi(g) }
            val (c, w) = lessonProgress.getPracticeStatus(LessonProgress.CATEGORY_USER_MISTAKES)
            _mistakes.value = PracticeMistakesUi(wrongCount = w.size, correctCount = c.size)
        }
    }

    private fun desiredOrder(): List<String> =
        listOf("def", "bez", "prav", "znak", "res", "voz", "souv", "cdt")

    private suspend fun loadOrderedGroups(): List<CategoryGroup> = withContext(Dispatchers.Default) {
        val order = desiredOrder()
        lessonProgress.getCategoryGroups()
            .sortedBy { g ->
                val idx = order.indexOf(g.category.lowercase())
                if (idx >= 0) idx else Int.MAX_VALUE
            }
    }

    private fun toUi(group: CategoryGroup): PracticeCategoryUi {
        val cat = group.category
        val total = group.subcategories.sumOf { it.questionCount }
        val (correctIds, wrongIds) = lessonProgress.getPracticeStatus(cat)
        val answered = (correctIds + wrongIds).distinct().size
        val progress = if (total > 0) answered.toFloat() / total.toFloat() else 0f
        val completed = total > 0 && wrongIds.isEmpty() && correctIds.size >= total
        val attemptCount = correctIds.size + wrongIds.size
        val percentCorrect = when {
            attemptCount == 0 -> 0
            else -> ((correctIds.size * 100) / attemptCount).coerceIn(0, 100)
        }
        val allQs = lessonProgress.getQuestionsForCategory(cat, null)
        val worst = allQs
            .map { it.id to lessonProgress.getMistakeConsecutiveCount(it.id) }
            .filter { it.second > 0 }
            .sortedByDescending { it.second }
            .take(5)
            .map { it.first }
        return PracticeCategoryUi(
            code = cat,
            title = PracticeDisplayNames.categoryTitle(cat),
            subcategories = group.subcategories.map { s ->
                PracticeSubcategoryRow(
                    code = s.subcategory,
                    title = PracticeDisplayNames.subcategoryTitle(s.subcategory),
                    questionCount = s.questionCount,
                )
            },
            progress = progress,
            totalCount = total,
            answeredCount = answered,
            completed = completed,
            percentCorrect = percentCorrect,
            attemptCount = attemptCount,
            worstQuestionIds = worst,
        )
    }

    private suspend fun runSearch(raw: String) {
        val q = raw.trim()
        if (q.length < 2) {
            _searchHits.value = emptyList()
            return
        }
        val hits = withContext(Dispatchers.Default) {
            lessonProgress.searchQuestions(q, limit = 30).map { h ->
                PracticeSearchHitUi(
                    id = h.id,
                    categoryCode = h.categoryCode,
                    categoryTitle = PracticeDisplayNames.categoryTitle(h.categoryCode),
                    preview = h.questionText.trim().lines().firstOrNull().orEmpty()
                        .let { t -> if (t.length > 140) t.take(137) + "…" else t },
                    imagePath = h.imagePath,
                )
            }
        }
        _searchHits.value = hits
    }
}
