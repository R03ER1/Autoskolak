package cz.autokolk

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Krok 142 — unit testy pro spaced-repetition algoritmus revize chybných otázek.
 * Čistě funkční logika ([MistakeReviewScheduler]) bez Android/Context závislostí.
 */
class MistakeReviewSchedulerTest {

    private val now = 1_000_000_000_000L // libovolný pevný referenční čas (ms)

    @Test
    fun `novy zaznam bez rozvrhu je vzdy due`() {
        assertTrue(MistakeReviewScheduler.isDue(null, now))
    }

    @Test
    fun `zaznam s nextReviewAt v minulosti je due`() {
        val entry = ReviewScheduleEntry(stage = 1, nextReviewAtMs = now - 1)
        assertTrue(MistakeReviewScheduler.isDue(entry, now))
    }

    @Test
    fun `zaznam s nextReviewAt v budoucnu neni due`() {
        val entry = ReviewScheduleEntry(stage = 1, nextReviewAtMs = now + 1)
        assertFalse(MistakeReviewScheduler.isDue(entry, now))
    }

    @Test
    fun `prvni spravna odpoved nastavi stage 1 a interval 1 den`() {
        val next = MistakeReviewScheduler.onCorrectAnswer(null, now)
        assertEquals(1, next?.stage)
        assertEquals(now + 1L * MistakeReviewScheduler.DAY_MS, next?.nextReviewAtMs)
    }

    @Test
    fun `druha spravna odpoved posune na stage 2 a interval 3 dny`() {
        val stage1 = ReviewScheduleEntry(stage = 1, nextReviewAtMs = now)
        val next = MistakeReviewScheduler.onCorrectAnswer(stage1, now)
        assertEquals(2, next?.stage)
        assertEquals(now + 3L * MistakeReviewScheduler.DAY_MS, next?.nextReviewAtMs)
    }

    @Test
    fun `treti spravna odpoved posune na stage 3 a interval 7 dni`() {
        val stage2 = ReviewScheduleEntry(stage = 2, nextReviewAtMs = now)
        val next = MistakeReviewScheduler.onCorrectAnswer(stage2, now)
        assertEquals(3, next?.stage)
        assertEquals(now + 7L * MistakeReviewScheduler.DAY_MS, next?.nextReviewAtMs)
    }

    @Test
    fun `ctvrta spravna odpoved graduuje otazku (null = odstranit ze seznamu)`() {
        val stage3 = ReviewScheduleEntry(stage = 3, nextReviewAtMs = now)
        val next = MistakeReviewScheduler.onCorrectAnswer(stage3, now)
        assertNull(next)
    }

    @Test
    fun `spatna odpoved vraci na stage 0 (bez zaznamu, okamzite due)`() {
        val result = MistakeReviewScheduler.onWrongAnswer()
        assertNull(result)
        assertTrue(MistakeReviewScheduler.isDue(result, now))
    }

    @Test
    fun `stara ulozena data bez schedule pole se deserializuji jako due ihned`() {
        // Simuluje legacy uložený JSON bez nových polí (stage/nextReviewAtMs úplně chybí).
        val legacyJson = """{"q1": {}}"""
        val type = object : TypeToken<Map<String, ReviewScheduleEntry>>() {}.type
        val parsed: Map<String, ReviewScheduleEntry> = Gson().fromJson(legacyJson, type)
        val entry = parsed["q1"]
        assertEquals(0, entry?.stage)
        assertTrue(MistakeReviewScheduler.isDue(entry, now))
    }

    @Test
    fun `chybejici klic v mape (uplne stara data pred krokem 142) je take due`() {
        val emptyMap: Map<String, ReviewScheduleEntry> = emptyMap()
        assertTrue(MistakeReviewScheduler.isDue(emptyMap["nejaka_otazka"], now))
    }
}
