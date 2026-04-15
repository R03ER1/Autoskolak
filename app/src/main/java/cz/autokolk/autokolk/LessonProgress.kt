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

        private const val KEY_TOTAL_XP = "total_xp_v1"
        private const val KEY_XP_BY_DAY_JSON = "xp_by_day_json"
        private const val KEY_DOUBLE_XP_UNTIL_MS = "double_xp_until_ms"
        private const val KEY_PENDING_LEVEL_UP = "pending_level_up_level"
        private const val KEY_PENDING_LEVEL_TITLE = "pending_level_up_title"
        private const val KEY_PENDING_LEVEL_BONUS_COINS = "pending_level_up_bonus_coins"
        private const val KEY_WEEKLY_XP_BEST = "weekly_xp_personal_best"
        private const val KEY_STREAK_MILESTONE_CLAIMED = "streak_milestone_claimed"
        private const val KEY_PENDING_STREAK_CELEBRATION = "pending_streak_celebration"
        private const val KEY_DC_ANSWERS_DATE = "dc_answers_date"
        private const val KEY_DC_ANSWERS_COUNT = "dc_answers_count"
        private const val KEY_DC_FEED_DATE = "dc_feed_date"
        private const val KEY_DC_FEED_COUNT = "dc_feed_count"
        private const val KEY_DAILY_LOGIN_GRANT = "daily_login_grant_date"
        private const val KEY_WHEEL_PITY = "bonus_wheel_pity"
        private const val KEY_WHEEL_DAY = "bonus_wheel_day"
        private const val KEY_WHEEL_COUNT_DAY = "bonus_wheel_count_day"
        private const val KEY_BOX_DAY = "mystery_box_day"
        private const val KEY_BOX_COUNT_DAY = "mystery_box_count_day"
        private const val KEY_BOX_PITY = "mystery_box_pity"

        /** XP navíc při otevření mystery boxu — musí odpovídat [openMysteryBox]. */
        private const val MYSTERY_BOX_BONUS_XP = 8
    }

    /** Zbývající točení bonusovým kolem dnes (max. 3). */
    fun getBonusWheelRollsRemainingToday(): Int {
        val today = todayString()
        if (prefs.getString(KEY_WHEEL_DAY, null) != today) {
            return 3
        }
        return (3 - prefs.getInt(KEY_WHEEL_COUNT_DAY, 0)).coerceAtLeast(0)
    }

    /** Zbývající otevření mystery boxu dnes (max. 2). */
    fun getMysteryBoxOpensRemainingToday(): Int {
        val today = todayString()
        if (prefs.getString(KEY_BOX_DAY, null) != today) {
            return 2
        }
        return (2 - prefs.getInt(KEY_BOX_COUNT_DAY, 0)).coerceAtLeast(0)
    }

    fun getMysteryBoxBonusXp(): Int = MYSTERY_BOX_BONUS_XP

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

    fun getLionName(): String = prefs.getString("lion_name", "Alex") ?: "Alex"

    fun setLionName(name: String) {
        val clean = name.trim().ifEmpty { "Alex" }.take(20)
        prefs.edit().putString("lion_name", clean).apply()
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

    /**
     * @param subcategoryFilter normalizovaný kód podkategorie (např. "neh", "C"); null / prázdné / "ALL" = celá kategorie
     */
    fun getQuestionsForCategory(category: String, subcategoryFilter: String? = null): List<Question> {
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
            val subFilter = subcategoryFilter?.trim()?.takeIf {
                it.isNotEmpty() && !it.equals("ALL", ignoreCase = true)
            }
            val questions = csvParser.records.drop(1)
                .filter { rec ->
                    val (cat, sub) = normalizeCategory(rec[1], rec[2])
                    if (!cat.equals(target, ignoreCase = true)) return@filter false
                    if (subFilter == null) return@filter true
                    sub.equals(subFilter, ignoreCase = true)
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

    data class QuestionSearchHit(
        val id: String,
        val categoryCode: String,
        val subcategoryCode: String,
        val questionText: String,
        val imagePath: String?,
    )

    /** Jednoduché fulltext vyhledávání v textu otázky (CSV sloupec otázky). */
    fun searchQuestions(query: String, limit: Int = 40): List<QuestionSearchHit> {
        val q = query.trim().lowercase()
        if (q.length < 2) return emptyList()
        return try {
            val inputStream = context.assets.open("Questions.csv")
            val reader = BufferedReader(InputStreamReader(inputStream))
            val csvParser = CSVParser(reader, CSVFormat.DEFAULT
                .withDelimiter(';')
                .withQuote('"')
                .withIgnoreEmptyLines(true)
                .withTrim(),
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

            val out = mutableListOf<QuestionSearchHit>()
            for (record in csvParser.records.drop(1)) {
                if (out.size >= limit) break
                if (record.size() < 5) continue
                val text = record[4].lowercase()
                if (!text.contains(q)) continue
                val (cat, sub) = normalizeCategory(record[1], record[2])
                val qid = record[0].trim()
                val questionNumber = record[0].padStart(4, '0')
                val notes = if (record.size() > 9) record[9] else ""
                val hasImage = notes.contains("obrázek", ignoreCase = true)
                out.add(
                    QuestionSearchHit(
                        id = qid,
                        categoryCode = cat,
                        subcategoryCode = sub,
                        questionText = record[4],
                        imagePath = if (hasImage) resolveImagePath(questionNumber) else null,
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
        try { checkStreakMilestones(streak) } catch (_: Throwable) { }
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
        tryApplyStreakFreezeForMissedDay()
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

    private fun dayBeforeYesterdayString(): String {
        val cal = java.util.Calendar.getInstance()
        cal.add(java.util.Calendar.DATE, -2)
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return sdf.format(cal.time)
    }

    /**
     * Pokud uživatel minul právě jeden den a má aktivní zmrazení,
     * doplní virtuální dokončení včerejška bez navýšení streaku.
     */
    private fun tryApplyStreakFreezeForMissedDay() {
        if (!prefs.getBoolean("streak_freeze_pending", false)) return
        val last = prefs.getString("last_completion_date", null) ?: return
        val yest = yesterdayString()
        val today = todayString()
        if (last == today || last == yest) return
        if (last != dayBeforeYesterdayString()) {
            prefs.edit().putBoolean("streak_freeze_pending", false).apply()
            return
        }
        val frozenSet = prefs.getStringSet("frozen_streak_dates", emptySet())?.toMutableSet() ?: mutableSetOf()
        frozenSet.add(yest)
        prefs.edit()
            .putString("last_completion_date", yest)
            .putBoolean("streak_freeze_pending", false)
            .putStringSet("frozen_streak_dates", frozenSet)
            .apply()
    }

    /** 20 mincí — ochrana před ztrátou streaku při vynechání jednoho dne (viz [tryApplyStreakFreezeForMissedDay]). */
    fun buyStreakFreezeWithCoins(): Boolean {
        if (!spendPoints(20)) return false
        prefs.edit().putBoolean("streak_freeze_pending", true).apply()
        return true
    }

    fun hasPendingStreakFreeze(): Boolean = prefs.getBoolean("streak_freeze_pending", false)

    /** Včera bylo „zmrazené“ (spotřebovaný štít). */
    fun wasYesterdayStreakFrozen(): Boolean {
        val set = prefs.getStringSet("frozen_streak_dates", emptySet()) ?: emptySet()
        return yesterdayString() in set
    }

    fun consumeFrozenLabelForYesterday() {
        val yest = yesterdayString()
        val set = prefs.getStringSet("frozen_streak_dates", emptySet())?.toMutableSet() ?: return
        if (set.remove(yest)) {
            prefs.edit().putStringSet("frozen_streak_dates", set).apply()
        }
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

    // region --- XP, double XP, weekly XP chart ---

    fun getTotalXp(): Int = prefs.getInt(KEY_TOTAL_XP, 0)

    fun getDoubleXpRemainingMs(): Long {
        val until = prefs.getLong(KEY_DOUBLE_XP_UNTIL_MS, 0L)
        val now = System.currentTimeMillis()
        return (until - now).coerceAtLeast(0L)
    }

    /** 30 minut 2× XP (např. po rewarded reklamě). */
    fun activateDoubleXpForMinutes(minutes: Int) {
        val addMs = minutes.coerceIn(1, 120) * 60_000L
        val now = System.currentTimeMillis()
        val cur = prefs.getLong(KEY_DOUBLE_XP_UNTIL_MS, 0L).coerceAtLeast(now)
        val base = maxOf(cur, now)
        prefs.edit().putLong(KEY_DOUBLE_XP_UNTIL_MS, base + addMs).apply()
    }

    private fun xpMultiplierInternal(): Float {
        val until = prefs.getLong(KEY_DOUBLE_XP_UNTIL_MS, 0L)
        return if (System.currentTimeMillis() < until) 2f else 1f
    }

    /**
     * Přičte XP (s kombinovaným násobičem: double XP + [sessionComboMultiplier] z aktivní session).
     * @param sessionComboMultiplier např. 1.1f při combo ≥ 5 v lekci
     */
    fun addXp(
        baseAmount: Int,
        applyDoubleXpFromAds: Boolean = true,
        sessionComboMultiplier: Float = 1f,
    ): XpGrantResult {
        if (baseAmount <= 0) return XpGrantResult.none(getTotalXp())
        val mult = (if (applyDoubleXpFromAds) xpMultiplierInternal() else 1f) *
            sessionComboMultiplier.coerceIn(1f, 1.25f)
        val gained = (baseAmount * mult).toInt().coerceAtLeast(1)
        val beforeXp = getTotalXp()
        val beforeLevel = XpSystem.levelForTotalXp(beforeXp)
        val afterXp = beforeXp + gained
        prefs.edit().putInt(KEY_TOTAL_XP, afterXp).apply()
        recordXpForDay(gained)
        updateWeeklyXpRecord()
        val afterLevel = XpSystem.levelForTotalXp(afterXp)
        var bonus = 0
        if (afterLevel.level > beforeLevel.level) {
            bonus = XpSystem.bonusCoinsForLevelUp(afterLevel.level)
            if (bonus > 0) {
                try {
                    addPoints(bonus)
                } catch (_: Throwable) {
                }
            }
            prefs.edit()
                .putInt(KEY_PENDING_LEVEL_UP, afterLevel.level)
                .putString(KEY_PENDING_LEVEL_TITLE, afterLevel.title)
                .putInt(KEY_PENDING_LEVEL_BONUS_COINS, bonus)
                .apply()
        }
        return XpGrantResult(
            xpAdded = gained,
            totalXpAfter = afterXp,
            levelBefore = beforeLevel,
            levelAfter = afterLevel,
            leveledUp = afterLevel.level > beforeLevel.level,
            levelUpBonusCoins = if (afterLevel.level > beforeLevel.level) bonus else 0,
        )
    }

    fun consumePendingLevelUp(): LevelUpPending? {
        val lv = prefs.getInt(KEY_PENDING_LEVEL_UP, 0)
        if (lv <= 0) return null
        val title = prefs.getString(KEY_PENDING_LEVEL_TITLE, "") ?: ""
        val bonus = prefs.getInt(KEY_PENDING_LEVEL_BONUS_COINS, 0)
        prefs.edit()
            .remove(KEY_PENDING_LEVEL_UP)
            .remove(KEY_PENDING_LEVEL_TITLE)
            .remove(KEY_PENDING_LEVEL_BONUS_COINS)
            .apply()
        return LevelUpPending(level = lv, title = title, bonusCoins = bonus)
    }

    /** XP za posledních 7 kalendářních dní (včetně dnes), index 0 = nejstarší den v okně. */
    fun getXpLast7Days(): List<Int> {
        val map = readXpByDayMap()
        val cal = java.util.Calendar.getInstance()
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return (6 downTo 0).map { offset ->
            val c = java.util.Calendar.getInstance()
            c.add(java.util.Calendar.DATE, -offset)
            val key = sdf.format(c.time)
            map[key] ?: 0
        }
    }

    fun getXpSumLast7Days(): Int = getXpLast7Days().sum()

    fun getXpPrevious7DaysSum(): Int {
        val map = readXpByDayMap()
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        var sum = 0
        for (off in 13 downTo 7) {
            val c = java.util.Calendar.getInstance()
            c.add(java.util.Calendar.DATE, -off)
            sum += map[sdf.format(c.time)] ?: 0
        }
        return sum
    }

    fun getWeeklyXpPersonalBest(): Int = prefs.getInt(KEY_WEEKLY_XP_BEST, 0)

    private fun updateWeeklyXpRecord() {
        val sum = getXpSumLast7Days()
        val best = prefs.getInt(KEY_WEEKLY_XP_BEST, 0)
        if (sum > best) {
            prefs.edit().putInt(KEY_WEEKLY_XP_BEST, sum).apply()
        }
    }

    private fun recordXpForDay(amount: Int) {
        if (amount <= 0) return
        val today = todayString()
        val map = readXpByDayMap().toMutableMap()
        map[today] = (map[today] ?: 0) + amount
        val cutoffCal = java.util.Calendar.getInstance()
        cutoffCal.add(java.util.Calendar.DATE, -21)
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val cutoff = sdf.format(cutoffCal.time)
        map.keys.filter { it < cutoff }.forEach { map.remove(it) }
        prefs.edit().putString(KEY_XP_BY_DAY_JSON, gson.toJson(map)).apply()
    }

    private fun readXpByDayMap(): Map<String, Int> {
        val json = prefs.getString(KEY_XP_BY_DAY_JSON, null) ?: return emptyMap()
        return try {
            val type = object : TypeToken<MutableMap<String, Int>>() {}.type
            gson.fromJson<MutableMap<String, Int>>(json, type) ?: emptyMap()
        } catch (_: Exception) {
            emptyMap()
        }
    }

    private fun checkStreakMilestones(streak: Int) {
        val claimed = prefs.getStringSet(KEY_STREAK_MILESTONE_CLAIMED, emptySet())?.toMutableSet() ?: mutableSetOf()
        val milestones = listOf(
            7 to 50,
            30 to 200,
            100 to 1000,
            365 to 2500,
        )
        var changed = false
        var lastCelebration: Pair<Int, Int>? = null
        for ((day, coins) in milestones) {
            val key = "m$day"
            if (streak < day || key in claimed) continue
            claimed.add(key)
            changed = true
            try {
                addPoints(coins)
            } catch (_: Throwable) {
            }
            lastCelebration = day to coins
        }
        if (changed) {
            val ed = prefs.edit().putStringSet(KEY_STREAK_MILESTONE_CLAIMED, claimed)
            if (lastCelebration?.first == 30) {
                ed.putBoolean("accessory_streak30_crown", true)
            }
            if (lastCelebration != null) {
                val (d, c) = lastCelebration
                ed.putString(KEY_PENDING_STREAK_CELEBRATION, "$d|$c")
            }
            ed.apply()
        }
    }

    fun consumePendingStreakCelebration(): Pair<Int, Int>? {
        val raw = prefs.getString(KEY_PENDING_STREAK_CELEBRATION, null) ?: return null
        prefs.edit().remove(KEY_PENDING_STREAK_CELEBRATION).apply()
        val parts = raw.split('|')
        if (parts.size != 2) return null
        val day = parts[0].toIntOrNull() ?: return null
        val coins = parts[1].toIntOrNull() ?: return null
        return day to coins
    }

    fun hasStreak30CrownUnlocked(): Boolean = prefs.getBoolean("accessory_streak30_crown", false)

    fun isStreakMilestoneClaimed(day: Int): Boolean {
        val claimed = prefs.getStringSet(KEY_STREAK_MILESTONE_CLAIMED, emptySet()) ?: emptySet()
        return "m$day" in claimed
    }

    // region Daily challenges

    fun incrementDailyCorrectAnswers(): Int {
        val today = todayString()
        if (prefs.getString(KEY_DC_ANSWERS_DATE, null) != today) {
            prefs.edit().putString(KEY_DC_ANSWERS_DATE, today).putInt(KEY_DC_ANSWERS_COUNT, 0).apply()
        }
        val n = prefs.getInt(KEY_DC_ANSWERS_COUNT, 0) + 1
        prefs.edit().putInt(KEY_DC_ANSWERS_COUNT, n).apply()
        tryGrantDailyChallengeXp("ans10", 10, 10)
        return n
    }

    fun incrementDailyFeedCount(): Int {
        val today = todayString()
        if (prefs.getString(KEY_DC_FEED_DATE, null) != today) {
            prefs.edit().putString(KEY_DC_FEED_DATE, today).putInt(KEY_DC_FEED_COUNT, 0).apply()
        }
        val n = prefs.getInt(KEY_DC_FEED_COUNT, 0) + 1
        prefs.edit().putInt(KEY_DC_FEED_COUNT, n).apply()
        tryGrantDailyChallengeXp("feed1", 1, 5)
        return n
    }

    private fun tryGrantDailyChallengeXp(id: String, target: Int, rewardXp: Int) {
        val today = todayString()
        val keyDone = "dc_${today}_${id}_done"
        if (prefs.getBoolean(keyDone, false)) return
        val prog = when (id) {
            "ans10" -> prefs.getInt(KEY_DC_ANSWERS_COUNT, 0)
            "less2" -> getLessonsCompletedToday()
            "feed1" -> prefs.getInt(KEY_DC_FEED_COUNT, 0)
            else -> return
        }
        if (prog < target) return
        prefs.edit().putBoolean(keyDone, true).apply()
        try {
            addXp(rewardXp, applyDoubleXpFromAds = true, sessionComboMultiplier = 1f)
        } catch (_: Throwable) {
        }
    }

    fun onDailyChallengeLessonsProgress() {
        tryGrantDailyChallengeXp("less2", 2, 20)
    }

    /** Jednou denně: malý bonus XP + mince. */
    fun grantDailyLoginIfNeeded(): Boolean {
        val today = todayString()
        if (prefs.getString(KEY_DAILY_LOGIN_GRANT, null) == today) return false
        prefs.edit().putString(KEY_DAILY_LOGIN_GRANT, today).apply()
        try {
            addXp(15, applyDoubleXpFromAds = true, sessionComboMultiplier = 1f)
            addPoints(12)
        } catch (_: Throwable) {
        }
        return true
    }

    /**
     * Bonusové kolo (max 3× denně), odměna v mincích; pity přidává bonus po slabých tocích.
     */
    fun rollBonusWheel(): Int {
        val today = todayString()
        if (prefs.getString(KEY_WHEEL_DAY, null) != today) {
            prefs.edit().putString(KEY_WHEEL_DAY, today).putInt(KEY_WHEEL_COUNT_DAY, 0).apply()
        }
        val cnt = prefs.getInt(KEY_WHEEL_COUNT_DAY, 0)
        if (cnt >= 3) return 0
        prefs.edit().putInt(KEY_WHEEL_COUNT_DAY, cnt + 1).apply()
        val pity = prefs.getInt(KEY_WHEEL_PITY, 0)
        val rng = java.util.Random()
        val r = rng.nextInt(100)
        val base = when {
            r < 40 -> 8 + rng.nextInt(12)
            r < 78 -> 18 + rng.nextInt(17)
            else -> 32 + rng.nextInt(28)
        }
        val bonus = pity * 8
        val coins = (base + bonus).coerceIn(5, 130)
        val newPity = if (coins < 22) (pity + 1).coerceAtMost(6) else 0
        prefs.edit().putInt(KEY_WHEEL_PITY, newPity).apply()
        addPoints(coins)
        return coins
    }

    fun openMysteryBox(): Int {
        val today = todayString()
        if (prefs.getString(KEY_BOX_DAY, null) != today) {
            prefs.edit().putString(KEY_BOX_DAY, today).putInt(KEY_BOX_COUNT_DAY, 0).apply()
        }
        val cnt = prefs.getInt(KEY_BOX_COUNT_DAY, 0)
        if (cnt >= 2) return 0
        prefs.edit().putInt(KEY_BOX_COUNT_DAY, cnt + 1).apply()
        val pity = prefs.getInt(KEY_BOX_PITY, 0)
        val rng = java.util.Random()
        val roll = rng.nextInt(100)
        val base = when {
            pity >= 4 -> 55 + rng.nextInt(40)
            roll < 45 -> 10 + rng.nextInt(18)
            roll < 82 -> 24 + rng.nextInt(26)
            else -> 45 + rng.nextInt(35)
        }
        val newPity = if (base < 35) pity + 1 else 0
        prefs.edit().putInt(KEY_BOX_PITY, newPity.coerceAtMost(6)).apply()
        addPoints(base)
        try {
            addXp(MYSTERY_BOX_BONUS_XP, applyDoubleXpFromAds = true, sessionComboMultiplier = 1f)
        } catch (_: Throwable) {
        }
        return base
    }

    fun snapshotDailyChallenges(): List<DailyChallengeUi> {
        val today = todayString()
        fun done(id: String) = prefs.getBoolean("dc_${today}_${id}_done", false)
        val ans = if (prefs.getString(KEY_DC_ANSWERS_DATE, null) != today) 0 else prefs.getInt(KEY_DC_ANSWERS_COUNT, 0)
        val lessons = getLessonsCompletedToday()
        val feed = if (prefs.getString(KEY_DC_FEED_DATE, null) != today) 0 else prefs.getInt(KEY_DC_FEED_COUNT, 0)
        return listOf(
            DailyChallengeUi(
                id = "ans10",
                title = "10 správných odpovědí",
                progress = (ans / 10f).coerceIn(0f, 1f),
                done = done("ans10"),
                rewardXp = 10,
            ),
            DailyChallengeUi(
                id = "less2",
                title = "Dokonči 2 lekce",
                progress = (lessons / 2f).coerceIn(0f, 1f),
                done = done("less2"),
                rewardXp = 20,
            ),
            DailyChallengeUi(
                id = "feed1",
                title = "Nakrm Alexe",
                progress = (feed / 1f).coerceIn(0f, 1f),
                done = done("feed1"),
                rewardXp = 5,
            ),
        )
    }

    // endregion

    // endregion XP

    fun registerOnLessonProgressChanged(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        prefs.registerOnSharedPreferenceChangeListener(listener)
    }

    fun unregisterOnLessonProgressChanged(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        prefs.unregisterOnSharedPreferenceChangeListener(listener)
    }
} 