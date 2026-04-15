package cz.autokolk.ui.screens.practice

import androidx.annotation.DrawableRes
import cz.autokolk.R
import cz.autokolk.LessonProgress

object PracticeCategoryIcons {

    @DrawableRes
    fun drawableResForCategory(code: String): Int = when (code.lowercase()) {
        LessonProgress.CATEGORY_USER_MISTAKES.lowercase() -> R.drawable.ic_live
        "def" -> R.drawable.ic_star
        "bez" -> R.drawable.ic_heart
        "prav" -> R.drawable.ic_info
        "znak" -> R.drawable.ic_arrow_drop_down
        "res" -> R.drawable.ic_shop
        "voz" -> R.drawable.ic_settings
        "souv" -> R.drawable.ic_fingerprint
        "cdt" -> R.drawable.ic_test
        "med" -> R.drawable.ic_coin
        else -> R.drawable.ic_practice
    }
}
