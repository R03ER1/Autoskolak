package cz.autokolk.ui.screens.alex

import android.content.Context

/**
 * Vybere první existující PNG v `assets/images/alex/` podle nálady a brýlí (C-prefix jako ve View).
 */
object AlexAssetResolver {

    fun firstExistingAlexFaceFromHunger(context: Context, hungerPercent: Int, sunglassesOn: Boolean): String {
        val mood = hungerPercentToMood(hungerPercent)
        return firstExistingAlexFace(context, mood, sunglassesOn)
    }

    fun firstExistingAlexFace(context: Context, mood: AlexMood, sunglassesOn: Boolean): String {
        for (p in candidatePaths(mood, sunglassesOn)) {
            try {
                context.assets.open("images/alex/$p").close()
                return p
            } catch (_: Exception) {
            }
        }
        return "Alex.png"
    }

    private fun candidatePaths(mood: AlexMood, sun: Boolean): List<String> {
        // Základní obrázek podle nálady (viz historická AlexActivity.getAlexImageName).
        // "Cool" (sluneční brýle) je samostatný vizuál napojený na coins-nákup, ne na hlad.
        val base = when (mood) {
            AlexMood.Happy -> "AlexHappy.png"
            AlexMood.Neutral -> "Alex.png"
            AlexMood.Hungry -> "AlexSad.png"
            AlexMood.Starving -> "AlexHungry.png"
        }
        return if (sun) listOf("C$base", base) else listOf(base)
    }
}
