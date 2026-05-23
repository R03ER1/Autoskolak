package cz.autokolk.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import cz.autokolk.ComposeMainActivity
import cz.autokolk.LessonProgress
import cz.autokolk.NotificationPermission
import cz.autokolk.R
import cz.autokolk.ui.navigation.ComposeNavIntent
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Periodicky (1× týdně, neděle ~18:00) pošle uživateli notifikaci se souhrnem za posledních
 * 7 dní (XP, počet lekcí, aktivní dny, streak). Po klepnutí otevře `WeeklyXpScreen` přes
 * deep-link [ComposeNavIntent.OPEN_TAB_WEEKLY_XP].
 */
class WeeklySummaryWorker(
    ctx: Context,
    params: WorkerParameters,
) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        val ctx = applicationContext
        val lp = LessonProgress(ctx)
        val xp = lp.getXpSumLast7Days()
        val lessons = lp.getLessonsCompletedLast7Days()
        // Pokud uživatel za celý týden nic neudělal, neotravujeme.
        if (xp <= 0 && lessons == 0) return Result.success()
        postSummaryNotification(ctx, lp, xp, lessons)
        return Result.success()
    }

    private fun postSummaryNotification(
        context: Context,
        lp: LessonProgress,
        xp: Int,
        lessons: Int,
    ) {
        createChannel(context)
        val activeDays = lp.getActiveDaysLast7()
        val streak = lp.getCurrentStreak()

        val body = if (streak > 0) {
            context.getString(R.string.weekly_summary_body, xp, lessons, activeDays, streak)
        } else {
            context.getString(R.string.weekly_summary_body_no_streak, xp, lessons, activeDays)
        }

        val openIntent = Intent(context, ComposeMainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(ComposeNavIntent.EXTRA_OPEN_TAB, ComposeNavIntent.OPEN_TAB_WEEKLY_XP)
        }
        val contentPi = PendingIntent.getActivity(
            context,
            REQUEST_OPEN,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_streak)
            .setContentTitle(context.getString(R.string.weekly_summary_title))
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(contentPi)
            .build()

        NotificationPermission.notifyIfAllowed(context, NOTIFICATION_ID, notification)
    }

    private fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.weekly_summary_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            channel.description = "Týdenní souhrn pokroku v Autoškolákovi"
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }

    companion object {
        const val UNIQUE_WORK_NAME = "weekly_summary"
        private const val CHANNEL_ID = "weekly_summary_channel"
        private const val NOTIFICATION_ID = 1003
        private const val REQUEST_OPEN = 20

        /**
         * Naplánuje periodický worker. Idempotentní díky [ExistingPeriodicWorkPolicy.KEEP] —
         * lze volat při každém startu aplikace.
         */
        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<WeeklySummaryWorker>(7, TimeUnit.DAYS)
                .setInitialDelay(initialDelayToNextSundayEveningMs(), TimeUnit.MILLISECONDS)
                .build()
            WorkManager.getInstance(context.applicationContext)
                .enqueueUniquePeriodicWork(UNIQUE_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request)
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context.applicationContext)
                .cancelUniqueWork(UNIQUE_WORK_NAME)
        }

        /**
         * Odhad zpoždění do nejbližší neděle 18:00. Pokud je už neděle po 18:00, plánujeme
         * za 7 dní. Minimální zpoždění je 15 minut (WorkManager flex).
         */
        internal fun initialDelayToNextSundayEveningMs(now: Long = System.currentTimeMillis()): Long {
            val cal = Calendar.getInstance().apply { timeInMillis = now }
            val target = Calendar.getInstance().apply {
                timeInMillis = now
                set(Calendar.HOUR_OF_DAY, 18)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val daysToAdd = ((Calendar.SUNDAY - cal.get(Calendar.DAY_OF_WEEK)) + 7) % 7
            target.add(Calendar.DATE, daysToAdd)
            if (target.timeInMillis <= now) {
                target.add(Calendar.DATE, 7)
            }
            val delta = target.timeInMillis - now
            return delta.coerceAtLeast(15L * 60L * 1000L)
        }
    }
}
