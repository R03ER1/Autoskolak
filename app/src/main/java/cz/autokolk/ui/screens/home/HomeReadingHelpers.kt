package cz.autokolk.ui.screens.home

import android.content.Context
import cz.autokolk.GlobalLesson
import cz.autokolk.LessonProgress

/**
 * Kód kategorie pro čtenáckou lekci — zarovnáno s [cz.autokolk.HomeActivity.getCategoryForLesson].
 */
fun categoryCodeForReadingLesson(lessonNumber: Int, lessonProgress: LessonProgress): String? {
    val plan = lessonProgress.getGlobalLessonPlan()
    val entry = plan.find { it.lessonNumber == lessonNumber } ?: return null
    val categoryName = entry.category
    val sub = entry.subcategory
    return when (sub) {
        "pru", "neb", "kri", "upr", "inf", "pri", "zak", "vys", "vod", "slo", "pok",
        "cho", "uca", "aut", "pra", "mhd", "sta", "sme", "pol", "neh",
        -> sub
        else -> when (categoryName) {
            "Úřady" -> "pru"
            "Nebezpečí na vozovce" -> "neb"
            "Křižovatky" -> "kri"
            "Značky upravující přednost" -> "upr"
            "Informativní dopravní značky" -> "inf"
            "Příkazové dopravní značky" -> "pri"
            "Zákazové dopravní značky" -> "zak"
            "Výstražné dopravní značky" -> "vys"
            "Vodorovné značky" -> "vod"
            "Sloupky" -> "slo"
            "Policisté na křižovatce" -> "pok"
            "Stání a zastavení" -> "cho"
            "Účastníci provozu" -> "uca"
            "Typy vozidel" -> "aut"
            "Pruhy a zóny" -> "pra"
            "MHD" -> "mhd"
            "Stání a parkování" -> "sta"
            "Změny směru" -> "sme"
            "Policie" -> "pol"
            "Nehody" -> "neh"
            else -> null
        }
    }
}

fun shouldShowTopicIntro(
    context: Context,
    lessonNumber: Int,
    entry: GlobalLesson?,
    lessonProgress: LessonProgress,
): Boolean {
    if (entry == null) return false
    if (categoryCodeForReadingLesson(lessonNumber, lessonProgress) == null) return false
    val plan = lessonProgress.getGlobalLessonPlan()
    val groupKey = if (entry.subcategory.trim().isNotEmpty()) {
        entry.subcategory.trim().lowercase()
    } else {
        (entry.category.trim().lowercase()).ifEmpty { "general" }
    }
    val sameGroup = if (entry.subcategory.trim().isNotEmpty()) {
        plan.filter { it.subcategory.trim().equals(entry.subcategory.trim(), ignoreCase = true) }
    } else {
        plan.filter { it.category.trim().equals(entry.category.trim(), ignoreCase = true) }
    }
    val firstLessonInGroup = sameGroup.minByOrNull { it.lessonNumber }?.lessonNumber ?: lessonNumber
    val prefs = context.getSharedPreferences("topic_intros", Context.MODE_PRIVATE)
    val hasShownKey = "intro_shown_$groupKey"
    return lessonNumber == firstLessonInGroup && !prefs.getBoolean(hasShownKey, false)
}

fun markTopicIntroShown(context: Context, entry: GlobalLesson) {
    val groupKey = if (entry.subcategory.trim().isNotEmpty()) {
        entry.subcategory.trim().lowercase()
    } else {
        entry.category.trim().lowercase().ifEmpty { "general" }
    }
    context.getSharedPreferences("topic_intros", Context.MODE_PRIVATE)
        .edit()
        .putBoolean("intro_shown_$groupKey", true)
        .apply()
}
