package cz.autokolk.ui.screens.reading

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cz.autokolk.LessonProgress
import cz.autokolk.reading.ReadingLessonsCatalog
import cz.autokolk.ui.components.buttons.PrimaryGradientButton
import cz.autokolk.ui.components.media.AssetImageFromPath
import cz.autokolk.ui.navigation.Route
import cz.autokolk.ui.screens.home.categoryCodeForReadingLesson
import cz.autokolk.ui.theme.AccentCyan
import cz.autokolk.ui.theme.AccentTeal
import cz.autokolk.ui.theme.TextPrimary
import cz.autokolk.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReadingLessonComposeScreen(
    navController: NavHostController,
    lessonId: Int,
    isReview: Boolean,
    externalExit: ReadingLessonExternalExit? = null,
    displayLessonNumberForMain: Int? = null,
) {
    val context = LocalContext.current
    val lessonProgress = remember(context) { LessonProgress(context) }
    val code = categoryCodeForReadingLesson(lessonId, lessonProgress).orEmpty()
    val pages = remember(code) { ReadingLessonsCatalog.readingLessonsForSubcategory(code) }
    val pagerState = rememberPagerState(pageCount = { maxOf(1, pages.size) })
    val scope = rememberCoroutineScope()

    fun goToQuizOrExit() {
        val ext = externalExit
        when {
            ext?.onOpenMainActivity != null -> ext.onOpenMainActivity.invoke(lessonId, isReview, displayLessonNumberForMain)
            ext?.onDismissEmbedded != null -> ext.onDismissEmbedded.invoke()
            else -> {
                navController.navigate(Route.Quiz(lessonId, false, -1, isReview).buildPath()) {
                    popUpTo(Route.Home.route) { inclusive = false }
                }
            }
        }
    }

    LaunchedEffect(pages.size) {
        if (pages.isEmpty()) {
            goToQuizOrExit()
        }
    }

    if (pages.isEmpty()) {
        return
    }

    val progress = (pagerState.currentPage + 1f) / pages.size.toFloat()

    Column(Modifier.fillMaxSize()) {
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth(),
            color = AccentCyan,
            trackColor = AccentTeal.copy(alpha = 0.25f),
        )
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) { page ->
            val lesson = pages[page]
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            ) {
                Text(
                    text = "Čtení (${page + 1}/${pages.size})",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary,
                )
                Spacer(Modifier.height(8.dp))
                if (!lesson.imagePath.isNullOrBlank()) {
                    AssetImageFromPath(
                        assetPath = lesson.imagePath,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                    )
                    Spacer(Modifier.height(12.dp))
                }
                Text(
                    text = lesson.text,
                    color = TextPrimary,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
        PrimaryGradientButton(
            text = if (pagerState.currentPage >= pages.lastIndex) {
                if (externalExit?.onDismissEmbedded != null) "Hotovo" else "Přejít na kvíz"
            } else {
                "Další"
            },
            onClick = {
                if (pagerState.currentPage < pages.lastIndex) {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                } else {
                    goToQuizOrExit()
                }
            },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        )
    }
}
