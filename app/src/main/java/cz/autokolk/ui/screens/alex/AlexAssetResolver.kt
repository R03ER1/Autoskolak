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
        return when (mood) {
            AlexMood.Happy ->
                if (sun) {
                    listOf(
                        "CAlexCool.png",
                        "AlexCool.png",
                        "CAlex.png",
                        "Alex.png",
                    )
                } else {
                    listOf("Alex.png", "AlexHappy.png")
                }
            AlexMood.Neutral ->
                if (sun) listOf("CAlexSad.png", "AlexSad.png")
                else listOf("AlexSad.png")
            AlexMood.Hungry ->
                if (sun) {
                    listOf(
                        "CAlexSadC.png",
                        "AlexSadC.png",
                        "CAlexHungry.png",
                        "AlexHungry.png",
                        "CAlexSad.png",
                        "AlexSad.png",
                    )
                } else {
                    listOf(
                        "AlexSadC.png",
                        "AlexHungry.png",
                        "AlexSad.png",
                    )
                }
            AlexMood.Starving ->
                if (sun) {
                    listOf(
                        "CAlexFamine.png",
                        "AlexFamine.png",
                        "CAlexHungry.png",
                        "AlexHungry.png",
                    )
                } else {
                    listOf(
                        "AlexFamine.png",
                        "AlexHungry.png",
                        "AlexSad.png",
                    )
                }
        }
    }
}
