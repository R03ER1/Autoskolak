package cz.autokolk.ui.navigation

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.compositionLocalOf

/**
 * [SharedTransitionScope] poskytnuté z [AutokolkNavGraph] přes `SharedTransitionLayout`, které
 * obaluje celý `NavHost`. Obrazovky/komponenty hluboko ve stromu (např. `LessonNode`, `QuizTopBar`)
 * si ho takto mohou přečíst bez nutnosti protahovat parametr přes všechny mezivrstvy.
 *
 * Mimo navigační graf (např. Compose preview) zůstává `null` a shared-element modifiery se prostě
 * nepoužijí — žádná funkčnost tím není ovlivněna.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }

/**
 * [AnimatedVisibilityScope] aktuální navigační destinace — poskytnuté uvnitř příslušného
 * `composable { }` bloku v [AutokolkNavGraph] (přijímač `AnimatedContentScope` z Navigation Compose
 * implementuje `AnimatedVisibilityScope`). Vyžadováno pro `sharedElement()`/`sharedBounds()`.
 */
val LocalNavAnimatedVisibilityScope = compositionLocalOf<AnimatedVisibilityScope?> { null }

/**
 * Klíč sdíleného přechodu mezi kolečkem lekce na Home cestě ([cz.autokolk.ui.screens.home.LessonNode])
 * a "hero" prvkem v hlavičce Quiz obrazovky ([cz.autokolk.ui.screens.quiz.QuizTopBar]). Stejný klíč
 * musí použít obě strany, jinak Compose přechod jen zkřížovým fade místo shared-bounds morphu.
 */
fun lessonHeroTransitionKey(lessonId: Int): String = "lesson_hero_$lessonId"
