package cz.autokolk

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import cz.autokolk.LessonProgress
import cz.autokolk.ui.AutokolkApp
import cz.autokolk.ui.navigation.ComposeNavIntent
import cz.autokolk.ui.navigation.Route
import cz.autokolk.ui.theme.AutokolkTheme
import cz.autokolk.ui.theme.LocalThemeController
import cz.autokolk.ui.theme.ThemeController
import cz.autokolk.ui.theme.readThemeMode
import cz.autokolk.ui.theme.writeThemeMode
import kotlinx.coroutines.flow.MutableStateFlow

class ComposeMainActivity : FragmentActivity() {
    private val openTabExtra = MutableStateFlow<String?>(null)
    private val resultsRouteExtra = MutableStateFlow<String?>(null)

    /**
     * Fallback za zrušenou legacy [ResultsActivity] (krok 153): pokud intent nese
     * [ComposeNavIntent.OPEN_RESULTS] s reálnými výsledkovými extra, sestaví přesnou
     * [Route.Results] cestu, aby [cz.autokolk.ui.AutokolkApp] mohl otevřít stejnou
     * obrazovku se stejnými daty jako dřív legacy Activity.
     */
    private fun buildResultsRouteFromIntent(intent: Intent): String? {
        if (intent.getStringExtra(ComposeNavIntent.EXTRA_OPEN_TAB) != ComposeNavIntent.OPEN_RESULTS) return null
        if (!intent.hasExtra(ComposeNavIntent.EXTRA_RESULTS_LESSON_ID)) return null
        return Route.Results(
            lessonId = intent.getIntExtra(ComposeNavIntent.EXTRA_RESULTS_LESSON_ID, -1),
            score = intent.getIntExtra(ComposeNavIntent.EXTRA_RESULTS_SCORE, 0),
            total = intent.getIntExtra(ComposeNavIntent.EXTRA_RESULTS_TOTAL, 0),
            firstOfDay = intent.getBooleanExtra(ComposeNavIntent.EXTRA_RESULTS_FIRST_OF_DAY, false),
            pointsAwarded = intent.getIntExtra(ComposeNavIntent.EXTRA_RESULTS_POINTS_AWARDED, 0),
            fromPractice = intent.getBooleanExtra(ComposeNavIntent.EXTRA_RESULTS_FROM_PRACTICE, false),
        ).buildPath()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        openTabExtra.value = intent.getStringExtra(ComposeNavIntent.EXTRA_OPEN_TAB)
        resultsRouteExtra.value = buildResultsRouteFromIntent(intent)
        enableEdgeToEdge()
        setContent {
            val initialOpenTab by openTabExtra.collectAsState()
            val initialResultsRoute by resultsRouteExtra.collectAsState()
            val ctx = LocalContext.current
            val appCtx = ctx.applicationContext
            var themeMode by remember { mutableStateOf(readThemeMode(ctx)) }
            val lessonProgress = remember(appCtx) { LessonProgress(appCtx) }
            var visualStyle by remember {
                mutableStateOf(lessonProgress.getActiveVisualStyle())
            }
            DisposableEffect(lessonProgress) {
                val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
                    visualStyle = lessonProgress.getActiveVisualStyle()
                }
                lessonProgress.registerOnLessonProgressChanged(listener)
                onDispose { lessonProgress.unregisterOnLessonProgressChanged(listener) }
            }
            val controller = ThemeController(
                mode = themeMode,
                setMode = { m ->
                    themeMode = m
                    writeThemeMode(ctx, m)
                },
            )
            CompositionLocalProvider(LocalThemeController provides controller) {
                AutokolkTheme(themeMode = themeMode, visualStyle = visualStyle) {
                    AutokolkApp(
                        initialOpenTab = initialOpenTab,
                        initialResultsRoute = initialResultsRoute,
                        onConsumeInitialTab = {
                            openTabExtra.value = null
                            resultsRouteExtra.value = null
                        },
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        openTabExtra.value = intent.getStringExtra(ComposeNavIntent.EXTRA_OPEN_TAB)
        resultsRouteExtra.value = buildResultsRouteFromIntent(intent)
    }
}
