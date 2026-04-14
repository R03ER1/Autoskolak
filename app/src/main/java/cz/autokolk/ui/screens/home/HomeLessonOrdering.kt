package cz.autokolk.ui.screens.home

import cz.autokolk.GlobalLesson

/**
 * Stejné pořadí jako [cz.autokolk.HomeActivity]: prvních 14 lekcí z def podkategorií
 * (`uca`, `aut`, `vec`, `cho`, `poj`), pak zbytek plánu bez těchto def lekcí.
 */
object HomeLessonOrdering {
    private val defSubcategoryCodes = setOf("uca", "aut", "vec", "cho", "poj")

    fun reorderPlan(plan: List<GlobalLesson>): List<GlobalLesson> {
        if (plan.isEmpty()) return emptyList()
        val defLessonsAll = plan.filter { it.subcategory.trim().lowercase() in defSubcategoryCodes }
        val defBlock = defLessonsAll.take(14)
        val nonDefLessons = plan.filter { it.subcategory.trim().lowercase() !in defSubcategoryCodes }
        return defBlock + nonDefLessons
    }
}
