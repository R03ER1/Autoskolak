package cz.autokolk

import android.content.Context

/**
 * Načte řádky z [assets/driving_fun_facts.txt] a stabilně přiřadí jeden řádek ke každému ID otázky.
 */
object DrivingFunFacts {
    private val lock = Any()
    private var cached: List<String> = emptyList()

    fun pickForQuestionId(context: Context, questionId: String): String? {
        synchronized(lock) {
            if (cached.isEmpty()) {
                cached = try {
                    context.assets.open("driving_fun_facts.txt").bufferedReader().use { r ->
                        r.readLines().map { it.trim() }.filter { it.isNotEmpty() }
                    }
                } catch (_: Exception) {
                    emptyList()
                }
            }
        }
        if (cached.isEmpty()) return null
        val idx = kotlin.math.abs(questionId.hashCode()) % cached.size
        return cached[idx]
    }
}
