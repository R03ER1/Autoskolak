package cz.autokolk

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import cz.autokolk.ui.screens.onboarding.OnboardingPreferences

data class LessonState(
    val lessonNumber: Int,
    val completed: Boolean = false,
    // Store incorrect questions by their stable IDs to avoid shuffle/ordering issues
    val incorrectQuestionIds: Set<String> = setOf()
)

data class CategoryGroup(
    val category: String,
    val subcategories: List<SubcategoryGroup>
)

data class SubcategoryGroup(
    val subcategory: String,
    val lessonNumber: Int,
    val questionCount: Int
)

data class GlobalLesson(
    val lessonNumber: Int,
    val category: String,      // Display category name (record[1])
    val subcategory: String,   // Subcategory code/name (record[2])
    val startIndex: Int,       // Slice start within this subcategory's questions (0-based)
    val count: Int             // Slice length (<= 10)
)

/**
 * Central user progress, economy, streak, and practice persistence.
 * Refactor direction (audit A2): split by domain (persistence vs streak vs economy) instead of growing this file further.
 */
class LessonProgress(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("lesson_progress", Context.MODE_PRIVATE)
    private val gson = Gson()

    private val _prefsRevision = MutableStateFlow(0L)
    /** Inkrementuje se při jakékoli změně [prefs] — pro Compose [androidx.compose.runtime.collectAsState]. */
    val prefsRevision: StateFlow<Long> = _prefsRevision.asStateFlow()

    private val prefsRevisionListener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
        _prefsRevision.value = _prefsRevision.value + 1L
    }

    init {
        prefs.registerOnSharedPreferenceChangeListener(prefsRevisionListener)
    }

    companion object {
        /** Virtual practice category: mistakes from anywhere in the app (not in Questions.csv). */
        const val CATEGORY_USER_MISTAKES = "user_mistakes"
        const val QUESTIONS_PER_LESSON = 10
        private const val MAX_HEARTS = 15

        private const val KEY_LESSONS_TODAY_DATE = "lessons_completed_today_date"
        private const val KEY_LESSONS_TODAY_COUNT = "lessons_completed_today_count"
        private const val RECHARGE_INTERVAL_MS = 30L * 60L * 1000L
        private const val KEY_HEARTS_FULL_SINCE = "hearts_full_since"
    }

    // --- Accessories ownership flags ---
    fun hasSunglasses(): Boolean {
        return prefs.getBoolean("accessory_sunglasses", false)
    }

    fun buySunglassesIfAffordable(cost: Int): Boolean {
        if (hasSunglasses()) return true
        if (!spendPoints(cost)) return false
        // Mark as owned and default to enabled on first purchase
        prefs.edit()
            .putBoolean("accessory_sunglasses", true)
            .putBoolean("accessory_sunglasses_enabled", true)
            .apply()
        return true
    }

    fun isSunglassesEnabled(): Boolean {
        // Enabled only if owned; default disabled when not owned
        if (!hasSunglasses()) return false
        return prefs.getBoolean("accessory_sunglasses_enabled", true)
    }

    fun setSunglassesEnabled(enabled: Boolean) {
        if (!hasSunglasses()) return
        prefs.edit().putBoolean("accessory_sunglasses_enabled", enabled).apply()
    }

    // --- Rename feature unlock ---
    fun hasRenameUnlocked(): Boolean {
        return prefs.getBoolean("feature_rename", false)
    }

    fun buyRenameIfAffordable(cost: Int): Boolean {
        if (hasRenameUnlocked()) return true
        if (!spendPoints(cost)) return false
        prefs.edit().putBoolean("feature_rename", true).apply()
        return true
    }

    private fun resolveImagePath(questionNumber: String): String? {
        val paddedBase = "images/${questionNumber}"
        val nonPadded = questionNumber.trimStart('0').ifEmpty { "0" }
        val nonPaddedBase = "images/${nonPadded}"
        // Prefer PNG, fallback to JPG/JPEG; try padded then non-padded filenames
        return when {
            assetExists("${paddedBase}.png") -> "${paddedBase}.png"
            assetExists("${paddedBase}.jpg") -> "${paddedBase}.jpg"
            assetExists("${paddedBase}.jpeg") -> "${paddedBase}.jpeg"
            assetExists("${nonPaddedBase}.png") -> "${nonPaddedBase}.png"
            assetExists("${nonPaddedBase}.jpg") -> "${nonPaddedBase}.jpg"
            assetExists("${nonPaddedBase}.jpeg") -> "${nonPaddedBase}.jpeg"
            else -> null
        }
    }

    private fun assetExists(path: String): Boolean {
        return try {
            context.assets.open(path).use { }
            true
        } catch (_: Exception) {
            false
        }
    }

    // Backward-compatible JSON adapter to safely handle legacy null sets
    private data class LessonStateJson(
        val lessonNumber: Int,
        val completed: Boolean = false,
        val incorrectQuestionIds: Set<String>? = null
    )

    // -------------------- Consecutive wrong answers (app-wide) --------------------
    private fun readMistakeStreakMap(): MutableMap<String, Int> {
        val json = prefs.getString("mistake_consecutive_wrong", null)
        if (json.isNullOrEmpty()) return mutableMapOf()
        return try {
            val type = object : TypeToken<MutableMap<String, Int>>() {}.type
            gson.fromJson<MutableMap<String, Int>>(json, type) ?: mutableMapOf()
        } catch (_: Exception) {
            mutableMapOf()
        }
    }

    private fun writeMistakeStreakMap(map: Map<String, Int>) {
        prefs.edit().putString("mistake_consecutive_wrong", gson.toJson(map)).apply()
    }

    /**
     * Tracks how many wrong answers in a row the user gave for this question anywhere in the app.
     * Any correct answer resets the streak to zero for that question.
     */
    fun recordMistakeStreak(questionId: String, isCorrect: Boolean) {
        val id = questionId.trim()
        if (id.isEmpty()) return
        val map = readMistakeStreakMap()
        if (isCorrect) {
            map.remove(id)
        } else {
            map[id] = (map[id] ?: 0) + 1
        }
        writeMistakeStreakMap(map)
    }

    fun getMistakeConsecutiveCount(questionId: String): Int {
        return readMistakeStreakMap()[questionId.trim()] ?: 0
    }

    // -------------------- Practice mode storage --------------------
    private data class PracticeStore(
        val answersByCategory: MutableMap<String, MutableMap<String, Boolean>> = mutableMapOf()
    )

    private fun readPracticeStore(): PracticeStore {
        val json = prefs.getString("practice_store", null)
        return if (json.isNullOrEmpty()) PracticeStore() else try {
            val type = object : TypeToken<PracticeStore>() {}.type
            gson.fromJson<PracticeStore>(json, type) ?: PracticeStore()
        } catch (_: Exception) {
            PracticeStore()
        }
    }

    private fun writePracticeStore(store: PracticeStore) {
        prefs.edit().putString("practice_store", gson.toJson(store)).apply()
    }

    fun savePracticeAnswer(category: String, questionId: String, isCorrect: Boolean) {
        val store = readPracticeStore()
        val byCategory = store.answersByCategory.getOrPut(category.lowercase()) { mutableMapOf() }
        val was = byCategory[questionId]
        byCategory[questionId] = isCorrect
        writePracticeStore(store)
        try { AchievementsManager(context).onPracticeFix(wasPreviouslyWrong = (was == false), nowCorrect = isCorrect) } catch (_: Throwable) { }
    }

    fun getPracticeStatus(category: String): Pair<Set<String>, Set<String>> {
        val cat = category.lowercase()
        if (cat == CATEGORY_USER_MISTAKES.lowercase()) {
            val store = readPracticeStore()
            val byCategory = store.answersByCategory[cat] ?: emptyMap()
            val streaks = readMistakeStreakMap()
            val correct = byCategory.filter { (id, ok) ->
                ok && (streaks[id] ?: 0) == 0
            }.keys
            val wrong = mutableSetOf<String>()
            streaks.forEach { (id, n) -> if (n > 0) wrong.add(id) }
            byCategory.forEach { (id, ok) -> if (!ok) wrong.add(id) }
            return Pair(correct, wrong)
        }
        val store = readPracticeStore()
        val byCategory = store.answersByCategory[cat] ?: emptyMap()
        val correct = byCategory.filterValues { it }.keys
        val wrong = byCategory.filterValues { !it }.keys
        return Pair(correct, wrong)
    }

    fun getQuestionsForCategory(category: String): List<Question> {
        return try {
            val inputStream = context.assets.open("Questions.csv")
            val reader = BufferedReader(InputStreamReader(inputStream))
            val csvParser = CSVParser(reader, CSVFormat.DEFAULT
                .withDelimiter(';')
                .withQuote('"')
                .withIgnoreEmptyLines(true)
                .withTrim()
            )

            fun normalizeCategory(rawCategory: String, rawSub: String): Pair<String, String> {
                val catTrim = rawCategory.trim()
                return when (catTrim.lowercase()) {
                    "c" -> "cdt" to "C"
                    "d" -> "cdt" to "D"
                    "t" -> "cdt" to "T"
                    else -> catTrim to rawSub.trim()
                }
            }

            val target = category.trim().lowercase()
            val questions = csvParser.records.drop(1)
                .filter { rec ->
                    val (cat, _) = normalizeCategory(rec[1], rec[2])
                    cat.equals(target, ignoreCase = true)
                }
                .sortedBy { it[0].toInt() }
                .map { record ->
                    val questionNumber = record[0].padStart(4, '0')
                    val notes = record[9]
                    val hasImage = notes.contains("obrázek", ignoreCase = true)
                    val hasVideo = notes.contains("video", ignoreCase = true)

                    Question(
                        id = record[0],
                        questionText = record[4],
                        optionA = record[5],
                        optionB = record[6],
                        optionC = record[7],
                        correctAnswer = record[8].lowercase(),
                        category = normalizeCategory(record[1], record[2]).first,
                        imagePath = if (hasImage) resolveImagePath(questionNumber) else null,
                        videoPath = if (hasVideo) "videos/$questionNumber.mp4" else null,
                        funFact = DrivingFunFacts.pickForQuestionId(context, record[0]),
                    )
                }

            csvParser.close()
            reader.close()
            questions
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /** Load questions for given CSV ids in one pass (order not preserved). */
    fun getQuestionsForIds(ids: Set<String>): List<Question> {
        if (ids.isEmpty()) return emptyList()
        val wanted = ids.map { it.trim() }.filter { it.isNotEmpty() }.toSet()
        if (wanted.isEmpty()) return emptyList()
        return try {
            val inputStream = context.assets.open("Questions.csv")
            val reader = BufferedReader(InputStreamReader(inputStream))
            val csvParser = CSVParser(reader, CSVFormat.DEFAULT
                .withDelimiter(';')
                .withQuote('"')
                .withIgnoreEmptyLines(true)
                .withTrim()
            )

            fun normalizeCategory(rawCategory: String, rawSub: String): Pair<String, String> {
                val catTrim = rawCategory.trim()
                return when (catTrim.lowercase()) {
                    "c" -> "cdt" to "C"
                    "d" -> "cdt" to "D"
                    "t" -> "cdt" to "T"
                    else -> catTrim to rawSub.trim()
                }
            }

            val out = mutableListOf<Question>()
            for (record in csvParser.records.drop(1)) {
                val qid = record[0].trim()
                if (qid !in wanted) continue
                val questionNumber = record[0].padStart(4, '0')
                val notes = record[9]
                val hasImage = notes.contains("obrázek", ignoreCase = true)
                val hasVideo = notes.contains("video", ignoreCase = true)
                out.add(
                    Question(
                        id = record[0],
                        questionText = record[4],
                        optionA = record[5],
                        optionB = record[6],
                        optionC = record[7],
                        correctAnswer = record[8].lowercase(),
                        category = normalizeCategory(record[1], record[2]).first,
                        imagePath = if (hasImage) resolveImagePath(questionNumber) else null,
                        videoPath = if (hasVideo) "videos/$questionNumber.mp4" else null,
                        funFact = DrivingFunFacts.pickForQuestionId(context, record[0]),
                    ),
                )
            }
            csvParser.close()
            reader.close()
            out
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Build a global lesson plan interleaving categories level-by-level.
     * For each category (record[1]), we split its questions into chunks of 10,
     * then take first chunk of all categories, then second chunk of all categories, etc.
     */
    fun getGlobalLessonPlan(): List<GlobalLesson> {
        return try {
            val inputStream = context.assets.open("Questions.csv")
            val reader = BufferedReader(InputStreamReader(inputStream))
            val csvParser = CSVParser(reader, CSVFormat.DEFAULT
                .withDelimiter(';')
                .withQuote('"')
                .withIgnoreEmptyLines(true)
                .withTrim()
            )

            fun normalizeCategory(rawCategory: String, rawSub: String): Pair<String, String> {
                val catTrim = rawCategory.trim()
                return when (catTrim.lowercase()) {
                    "c" -> "cdt" to "C"
                    "d" -> "cdt" to "D"
                    "t" -> "cdt" to "T"
                    else -> catTrim to rawSub.trim()
                }
            }

            val records = csvParser.records.drop(1)
                .sortedBy { it[0].toInt() }

            csvParser.close()
            reader.close()

            // Group questions by (category, subcategory); keep sorted by id
            val bySubcategory: Map<Pair<String, String>, List<Int>> = records.groupBy { rec ->
                normalizeCategory(rec[1], rec[2])
            }
                .mapValues { (_, recs) -> recs.map { it[0].toInt() }.sorted() }

            // For each (category, subcategory), split into chunks of 10
            val chunksBySubcategory: Map<Pair<String, String>, List<Pair<Int, Int>>> = bySubcategory.mapValues { (_, ids) ->
                val chunks = mutableListOf<Pair<Int, Int>>()
                var start = 0
                while (start < ids.size) {
                    val endExclusive = minOf(start + QUESTIONS_PER_LESSON, ids.size)
                    chunks.add(start to (endExclusive - start))
                    start = endExclusive
                }
                chunks
            }

            // Interleave: take first chunk from each subcategory, then second, etc.
            val result = mutableListOf<GlobalLesson>()
            var lessonNumber = 1
            var levelIndex = 0
            while (true) {
                var addedInRound = false
                for ((catSub, chunks) in chunksBySubcategory) {
                    if (levelIndex < chunks.size) {
                        val (startIdx, count) = chunks[levelIndex]
                        result.add(GlobalLesson(
                            lessonNumber = lessonNumber,
                            category = catSub.first,
                            subcategory = catSub.second,
                            startIndex = startIdx,
                            count = count
                        ))
                        lessonNumber += 1
                        addedInRound = true
                    }
                }
                if (!addedInRound) break
                levelIndex += 1
            }

            result
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun getCategoryGroups(): List<CategoryGroup> {
        try {
            val inputStream = context.assets.open("Questions.csv")
            val reader = BufferedReader(InputStreamReader(inputStream))
            val csvParser = CSVParser(reader, CSVFormat.DEFAULT
                .withDelimiter(';')
                .withQuote('"')
                .withIgnoreEmptyLines(true)
                .withTrim()
            )

            val records = csvParser.records.drop(1) // Skip header
            csvParser.close()
            reader.close()

            fun normalizeCategory(rawCategory: String, rawSub: String): Pair<String, String> {
                val catTrim = rawCategory.trim()
                return when (catTrim.lowercase()) {
                    "c" -> "cdt" to "C"
                    "d" -> "cdt" to "D"
                    "t" -> "cdt" to "T"
                    else -> catTrim to rawSub.trim()
                }
            }

            // Group by category and subcategory (with C/D/T normalized under cdt)
            val categoryMap = records.groupBy { normalizeCategory(it[1], it[2]).first } // Category is at index 1
                .mapValues { (_, questions) ->
                    questions.groupBy { q -> normalizeCategory(q[1], q[2]).second } // normalized subcategory
                        .mapValues { (_, subQuestions) ->
                            subQuestions.size // Get actual question count
                        }
                }

            // Convert to CategoryGroup objects
            var currentLessonNumber = 1
            return categoryMap.map { (category, subcategoryMap) ->
                val subcategories = subcategoryMap.map { (subcategory, questionCount) ->
                    val lessonsInSubcategory = (questionCount + QUESTIONS_PER_LESSON - 1) / QUESTIONS_PER_LESSON
                    val group = SubcategoryGroup(subcategory, currentLessonNumber, questionCount)
                    currentLessonNumber += lessonsInSubcategory
                    group
                }
                CategoryGroup(category, subcategories)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }

    fun getQuestionsForLesson(lessonNumber: Int): List<Question> {
        try {
            val plan = getGlobalLessonPlan()
            val entry = plan.find { it.lessonNumber == lessonNumber } ?: return emptyList()

            val inputStream = context.assets.open("Questions.csv")
            val reader = BufferedReader(InputStreamReader(inputStream))
            val csvParser = CSVParser(reader, CSVFormat.DEFAULT
                .withDelimiter(';')
                .withQuote('"')
                .withIgnoreEmptyLines(true)
                .withTrim()
            )

            fun normalizeCategory(rawCategory: String, rawSub: String): Pair<String, String> {
                val catTrim = rawCategory.trim()
                return when (catTrim.lowercase()) {
                    "c" -> "cdt" to "C"
                    "d" -> "cdt" to "D"
                    "t" -> "cdt" to "T"
                    else -> catTrim to rawSub.trim()
                }
            }

            val categoryQuestions = csvParser.records.drop(1)
                .filter { record ->
                    val (cat, sub) = normalizeCategory(record[1], record[2])
                    cat == entry.category && sub == entry.subcategory
                }
                .sortedBy { it[0].toInt() }
                .map { record ->
                    val questionNumber = record[0].padStart(4, '0')
                    val notes = record[9]
                    val hasImage = notes.contains("obrázek", ignoreCase = true)
                    val hasVideo = notes.contains("video", ignoreCase = true)

                    Question(
                        id = record[0],
                        questionText = record[4],
                        optionA = record[5],
                        optionB = record[6],
                        optionC = record[7],
                        correctAnswer = record[8].lowercase(),
                        category = normalizeCategory(record[1], record[2]).first,
                        imagePath = if (hasImage) resolveImagePath(questionNumber) else null,
                        videoPath = if (hasVideo) "videos/$questionNumber.mp4" else null,
                        funFact = DrivingFunFacts.pickForQuestionId(context, record[0]),
                    )
                }

            csvParser.close()
            reader.close()

            val startIndex = entry.startIndex
            val endIndexExclusive = minOf(startIndex + entry.count, categoryQuestions.size)
            if (startIndex >= categoryQuestions.size) return emptyList()
            val lessonQuestions = categoryQuestions.subList(startIndex, endIndexExclusive).toMutableList()
            lessonQuestions.shuffle()
            return lessonQuestions
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }

    /** Returns a random list of questions across all categories, shuffled and limited by count. */
    fun getRandomQuestions(count: Int): List<Question> {
        return try {
            val plan = getGlobalLessonPlan()
            if (plan.isEmpty()) return emptyList()

            val pool = mutableListOf<Question>()
            for (entry in plan) {
                pool.addAll(getQuestionsForLesson(entry.lessonNumber))
                if (pool.size >= count * 3) break
            }

            if (pool.isEmpty()) return emptyList()
            val unique = pool.distinctBy { it.id }.toMutableList()
            unique.shuffle()
            unique.take(count)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

		fun saveLessonProgress(lessonNumber: Int, incorrectQuestionIds: Set<String>) {
        val states = getAllLessonStates().toMutableList()
        val existingIndex = states.indexOfFirst { it.lessonNumber == lessonNumber }
        val newState = LessonState(lessonNumber, true, incorrectQuestionIds)
        
        if (existingIndex != -1) {
            states[existingIndex] = newState
        } else {
            states.add(newState)
        }

			prefs.edit().putString("lesson_states", gson.toJson(states)).apply()

			// Advance the next-lesson pointer following the Home screen displayed order
			val desiredNext = getNextIncompleteAfter(lessonNumber)
			prefs.edit().putInt("next_lesson", desiredNext).apply()
    }

    fun getLessonState(lessonNumber: Int): LessonState {
        val states = getAllLessonStates()
        return states.find { it.lessonNumber == lessonNumber }
            ?: LessonState(lessonNumber)
    }

    fun getAllLessonStates(): List<LessonState> {
        val json = prefs.getString("lesson_states", "[]")
        // Parse into a nullable-friendly DTO to survive legacy nulls
        val type = object : TypeToken<List<LessonStateJson>>() {}.type
        val parsed: List<LessonStateJson> = try {
            gson.fromJson(json, type)
        } catch (_: Exception) {
            emptyList()
        } ?: emptyList()
        return parsed.map { dto ->
            LessonState(
                lessonNumber = dto.lessonNumber,
                completed = dto.completed,
                incorrectQuestionIds = dto.incorrectQuestionIds ?: emptySet()
            )
        }
    }

		fun getNextAvailableLesson(): Int {
			// Primary source: explicit pointer persisted independently of practice
			var next = prefs.getInt("next_lesson", -1)
			if (next <= 0) {
				// Migration/fallback: derive from first incomplete in displayed order
				next = getFirstIncompleteInDisplayOrder()
				prefs.edit().putInt("next_lesson", next).apply()
			}
			val totalLessons = getGlobalLessonPlan().size
			return next.coerceIn(1, maxOf(1, totalLessons))
		}

		private fun getDisplayedLessonOrder(): List<Int> {
			val plan = getGlobalLessonPlan()
			if (plan.isEmpty()) return emptyList()
			val defSubcategoryCodes = setOf("uca", "aut", "vec", "cho", "poj")
			val defLessonsAll = plan.filter { it.subcategory.trim().lowercase() in defSubcategoryCodes }
			val defBlock = defLessonsAll.take(14)
			val nonDefLessons = plan.filter { it.subcategory.trim().lowercase() !in defSubcategoryCodes }
			val reorderedLessons = defBlock + nonDefLessons
			return reorderedLessons.map { it.lessonNumber }
		}

		private fun getFirstIncompleteInDisplayOrder(): Int {
			val order = getDisplayedLessonOrder()
			if (order.isEmpty()) return 1
			val completed = getAllLessonStates().filter { it.completed }.map { it.lessonNumber }.toSet()
			for (ln in order) {
				if (ln !in completed) return ln
			}
			// All completed: return the last lesson as a sane cap
			return order.last()
		}

		private fun getNextIncompleteAfter(lessonNumber: Int): Int {
			val order = getDisplayedLessonOrder()
			if (order.isEmpty()) return 1
			val completed = getAllLessonStates().filter { it.completed }.map { it.lessonNumber }.toSet()
			val idx = order.indexOf(lessonNumber).let { if (it < 0) 0 else it }
			for (i in (idx + 1) until order.size) {
				val ln = order[i]
				if (ln !in completed) return ln
			}
			// No later incomplete, return last lesson
			return order.last()
		}

		fun clearProgress() {
			prefs.edit().clear().apply()
			// After deleting all data, reset baseline values
			prefs.edit()
				.putInt("hearts_count", MAX_HEARTS)
				.putLong("hearts_updated_at", java.lang.System.currentTimeMillis())
				.putInt("next_lesson", 1)
				.apply()
		}

    // --- Points (Body) tracking ---
    fun getTotalPoints(): Int {
        return prefs.getInt("total_points", 0)
    }

    fun addPoints(pointsToAdd: Int) {
        if (pointsToAdd <= 0) return
        val current = getTotalPoints()
        prefs.edit().putInt("total_points", current + pointsToAdd).apply()
        try { AchievementsManager(context).onPointsEarned(pointsToAdd) } catch (_: Throwable) { }
    }

    /** Testing helper: sets total points to an exact value (>= 0). */
    fun setTotalPoints(points: Int) {
        val value = points.coerceAtLeast(0)
        prefs.edit().putInt("total_points", value).apply()
    }

    /** Spends points if available. Returns true on success, false if insufficient. */
    fun spendPoints(pointsToSpend: Int): Boolean {
        if (pointsToSpend <= 0) return true
        val current = getTotalPoints()
        if (current < pointsToSpend) return false
        prefs.edit().putInt("total_points", current - pointsToSpend).apply()
        try { AchievementsManager(context).onPointsSpent(pointsToSpend) } catch (_: Throwable) { }
        return true
    }

    // --- Test scores tracking ---
    fun addTestScore(points: Int, maxPoints: Int = 50) {
        val clampedPoints = points.coerceIn(0, maxPoints)
        val sumKey = "test_scores_sum_${maxPoints}"
        val countKey = "test_scores_count_${maxPoints}"
        val currentSum = prefs.getInt(sumKey, 0)
        val currentCount = prefs.getInt(countKey, 0)
        prefs.edit()
            .putInt(sumKey, currentSum + clampedPoints)
            .putInt(countKey, currentCount + 1)
            .apply()

        // Append to attempts list JSON for charting
        val listKey = "test_scores_list_${maxPoints}"
        val existingJson = prefs.getString(listKey, null)
        val type = object : TypeToken<MutableList<Int>>() {}.type
        val list: MutableList<Int> = try {
            if (existingJson.isNullOrEmpty()) mutableListOf() else (gson.fromJson(existingJson, type) ?: mutableListOf())
        } catch (_: Exception) {
            mutableListOf()
        }
        list.add(clampedPoints)
        prefs.edit().putString(listKey, gson.toJson(list)).apply()
    }

    fun getAverageTestScore(maxPoints: Int = 50): Double {
        val sumKey = "test_scores_sum_${maxPoints}"
        val countKey = "test_scores_count_${maxPoints}"
        val sum = prefs.getInt(sumKey, 0)
        val count = prefs.getInt(countKey, 0)
        if (count <= 0) return 0.0
        return sum.toDouble() / count.toDouble()
    }

    fun getAllTestScores(maxPoints: Int = 50): List<Int> {
        val listKey = "test_scores_list_${maxPoints}"
        val existingJson = prefs.getString(listKey, null) ?: return emptyList()
        val type = object : TypeToken<List<Int>>() {}.type
        return try {
            gson.fromJson<List<Int>>(existingJson, type) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    // --- Streak tracking ---
    fun updateStreakOnLessonCompleted(): Boolean {
        val today = todayString()
        val last = prefs.getString("last_completion_date", null)
        var streak = prefs.getInt("streak_count", 0)
        var isFirstOfDay = false

        if (last == today) {
            // Already counted today's streak
        } else if (last == yesterdayString()) {
            streak += 1
            isFirstOfDay = true
        } else {
            // Missed at least one full day, reset to 1 for today's completion
            streak = 1
            isFirstOfDay = true
        }

        prefs.edit()
            .putInt("streak_count", streak)
            .putString("last_completion_date", today)
            .apply()
        recordCompletionDate(today)
        incrementLessonsCompletedToday()
        try { AchievementsManager(context).onStreakUpdated(streak) } catch (_: Throwable) { }
        return isFirstOfDay
    }

    private fun incrementLessonsCompletedToday() {
        val today = todayString()
        val storedDate = prefs.getString(KEY_LESSONS_TODAY_DATE, null)
        val count = prefs.getInt(KEY_LESSONS_TODAY_COUNT, 0)
        val editor = prefs.edit()
        if (storedDate != today) {
            editor.putString(KEY_LESSONS_TODAY_DATE, today).putInt(KEY_LESSONS_TODAY_COUNT, 1)
        } else {
            editor.putInt(KEY_LESSONS_TODAY_COUNT, count + 1)
        }
        editor.apply()
    }

    /** Denní cíl lekcí (nastavení z onboardingu), výchozí 3. */
    fun getDailyGoal(): Int {
        val p = context.getSharedPreferences(OnboardingPreferences.PREFS_NAME, Context.MODE_PRIVATE)
        return p.getInt(OnboardingPreferences.KEY_DAILY_GOAL, OnboardingPreferences.DEFAULT_DAILY_GOAL).coerceIn(1, 10)
    }

    /** Počet dokončených lekcí dnes (pro denní cíl). */
    fun getLessonsCompletedToday(): Int {
        val today = todayString()
        if (prefs.getString(KEY_LESSONS_TODAY_DATE, null) != today) return 0
        return prefs.getInt(KEY_LESSONS_TODAY_COUNT, 0)
    }

    fun isDailyGoalMet(): Boolean = getLessonsCompletedToday() >= getDailyGoal()

    private fun recordCompletionDate(date: String) {
        val raw = prefs.getStringSet("completion_dates", emptySet()) ?: emptySet()
        val dates = raw.toMutableSet()
        dates.add(date)
        // Keep only the last 14 days to avoid unbounded growth
        val cutoff = run {
            val cal = java.util.Calendar.getInstance()
            cal.add(java.util.Calendar.DATE, -14)
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            sdf.format(cal.time)
        }
        dates.removeAll { it < cutoff }
        prefs.edit().putStringSet("completion_dates", dates).apply()
    }

    /**
     * Returns a list of 7 booleans representing the last 7 days (index 0 = today, 6 = 6 days ago).
     * `true` means a lesson was completed on that day.
     */
    fun getStreakHistory(): List<Boolean> {
        val dates = prefs.getStringSet("completion_dates", emptySet()) ?: emptySet()
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val cal = java.util.Calendar.getInstance()
        return (0..6).map { offset ->
            val c = java.util.Calendar.getInstance()
            c.add(java.util.Calendar.DATE, -offset)
            dates.contains(sdf.format(c.time))
        }
    }

    fun normalizeStreakForToday() {
        val today = todayString()
        val last = prefs.getString("last_completion_date", null)
        val streak = prefs.getInt("streak_count", 0)

        // If last completion was neither today nor yesterday, streak should be 0
        if (last != null && last != today && last != yesterdayString()) {
            if (streak != 0) {
                prefs.edit().putInt("streak_count", 0).apply()
            }
        }
        val current = getCurrentStreak()
        try { AchievementsManager(context).onStreakUpdated(current) } catch (_: Throwable) { }
    }

    fun getCurrentStreak(): Int {
        val today = todayString()
        val last = prefs.getString("last_completion_date", null)
        val streak = prefs.getInt("streak_count", 0)
        return if (last == today || last == yesterdayString()) streak else 0
    }

    /** Testing helper: sets streak for today to a specific non-negative value. */
    fun setStreakForToday(streak: Int) {
        val today = todayString()
        val value = streak.coerceAtLeast(0)
        prefs.edit()
            .putInt("streak_count", value)
            .putString("last_completion_date", today)
            .apply()
    }

    private fun todayString(): String {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return sdf.format(java.util.Date())
    }

    private fun yesterdayString(): String {
        val cal = java.util.Calendar.getInstance()
        cal.add(java.util.Calendar.DATE, -1)
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return sdf.format(cal.time)
    }

    // --- Hearts (Lives) tracking ---
    fun hasInfiniteLives(): Boolean = prefs.getBoolean("infinite_lives", false)

    /**
     * Returns current hearts after applying time-based recharge. Also persists any regeneration.
     */
    fun getCurrentHearts(): Int {
        val now = java.lang.System.currentTimeMillis()
        var hearts = prefs.getInt("hearts_count", MAX_HEARTS)
        var updatedAt = prefs.getLong("hearts_updated_at", now)

        if (hearts >= MAX_HEARTS) {
            // Ensure clamped value only; preserve timestamps to keep original "full since" moment
            if (hearts != MAX_HEARTS) {
                prefs.edit().putInt("hearts_count", MAX_HEARTS).apply()
            }
            return MAX_HEARTS
        }

        if (now > updatedAt) {
            val elapsed = now - updatedAt
            val regen = (elapsed / RECHARGE_INTERVAL_MS).toInt()
            if (regen > 0) {
                val newHearts = (hearts + regen).coerceAtMost(MAX_HEARTS)
                // Move updatedAt forward by the exact number of full intervals consumed
                val consumedMs = regen.toLong() * RECHARGE_INTERVAL_MS
                val newUpdatedAt = updatedAt + consumedMs
                val edit = prefs.edit()
                    .putInt("hearts_count", newHearts)
                    .putLong("hearts_updated_at", newUpdatedAt)
                // If we just reached full, remember when it happened
                if (newHearts >= MAX_HEARTS) {
                    edit.putLong(KEY_HEARTS_FULL_SINCE, newUpdatedAt)
                }
                edit.apply()
                hearts = newHearts
                updatedAt = newUpdatedAt
            }
        }
        return hearts
    }

    /**
     * Consumes one heart if available. Applies regeneration first. Returns true if a heart was consumed.
     */
    fun consumeHeart(): Boolean {
        // Apply regen and fetch current values
        val now = java.lang.System.currentTimeMillis()
        val current = getCurrentHearts()
        if (current <= 0) return false
        val newHearts = (current - 1).coerceAtLeast(0)
        // If we consumed from full, start a new countdown from now.
        // Otherwise, preserve the existing countdown (do not reset timestamp).
        val editor = prefs.edit().putInt("hearts_count", newHearts)
        if (current >= MAX_HEARTS) {
            editor.putLong("hearts_updated_at", now)
            // Leaving full state; clear full-since marker
            editor.remove(KEY_HEARTS_FULL_SINCE)
        }
        editor.apply()
        return true
    }

    /** Fully refills hearts to MAX_HEARTS and resets timer reference. */
    fun resetHeartsToFull() {
        val now = java.lang.System.currentTimeMillis()
        prefs.edit()
            .putInt("hearts_count", MAX_HEARTS)
            .putLong("hearts_updated_at", now)
            .putLong(KEY_HEARTS_FULL_SINCE, now)
            .apply()
    }

    /** Testing helper: sets hearts to exact value in range [0, MAX_HEARTS]. */
    fun setHearts(hearts: Int) {
        val now = java.lang.System.currentTimeMillis()
        val clamped = hearts.coerceIn(0, MAX_HEARTS)
        val editor = prefs.edit().putInt("hearts_count", clamped)
        // If set to full, reset timer reference to now; otherwise keep existing timestamp
        if (clamped >= MAX_HEARTS) {
            editor.putLong("hearts_updated_at", now)
            editor.putLong(KEY_HEARTS_FULL_SINCE, now)
        } else {
            editor.remove(KEY_HEARTS_FULL_SINCE)
        }
        editor.apply()
    }

    /**
     * Returns milliseconds remaining until the next heart regenerates, or 0 if a heart is available now,
     * or -1 if hearts are full.
     */
    fun millisUntilNextHeart(): Long {
        val now = java.lang.System.currentTimeMillis()
        val hearts = getCurrentHearts()
        if (hearts >= MAX_HEARTS) return -1
        val updatedAt = prefs.getLong("hearts_updated_at", now)
        val elapsed = now - updatedAt
        val remaining = RECHARGE_INTERVAL_MS - (elapsed % RECHARGE_INTERVAL_MS)
        return remaining.coerceAtLeast(0L)
    }

    /** Returns timestamp when hearts became full, or 0 if not known/not full. */
    fun getHeartsFullSince(): Long {
        val hearts = prefs.getInt("hearts_count", MAX_HEARTS)
        if (hearts < MAX_HEARTS) return 0L
        return prefs.getLong(KEY_HEARTS_FULL_SINCE, 0L)
    }

    fun registerOnLessonProgressChanged(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        prefs.registerOnSharedPreferenceChangeListener(listener)
    }

    fun unregisterOnLessonProgressChanged(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        prefs.unregisterOnSharedPreferenceChangeListener(listener)
    }
} 