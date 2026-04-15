package cz.autokolk.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import cz.autokolk.LessonProgress
import cz.autokolk.R

/**
 * Jednoduchý widget: streak + počet splněných denních výzev.
 */
class StreakWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        val lp = LessonProgress(context)
        val streak = lp.getCurrentStreak()
        val dc = lp.snapshotDailyChallenges().count { it.done }
        for (id in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_streak)
            views.setTextViewText(R.id.widget_streak_value, streak.toString())
            views.setTextViewText(R.id.widget_dc_value, "$dc/3")
            appWidgetManager.updateAppWidget(id, views)
        }
    }
}
