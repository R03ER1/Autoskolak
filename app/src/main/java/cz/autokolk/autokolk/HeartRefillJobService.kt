package cz.autokolk

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.app.job.JobInfo
import android.app.PendingIntent
import androidx.core.app.NotificationCompat

class HeartRefillJobService : JobService() {
    override fun onStartJob(params: JobParameters?): Boolean {
        val progress = LessonProgress(this)
        val hearts = progress.getCurrentHearts()

        if (hearts == 15) {
            if (shouldSendFullLivesNotification()) {
                showFullLivesNotification(this)
            }
        } else if (hearts < 15) {
            // Reset notification tracking when hearts drop below 15
            val prefs = getSharedPreferences("heart_notifications", MODE_PRIVATE)
            prefs.edit().putInt("last_notified_hearts", 0).apply()
        }

        // Always schedule next check to detect when hearts drop below 15 or reach 15 again
        scheduleNext(this, progress)

        // No background work; finished
        return false
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        // No reschedule on stop
        return false
    }

    private fun shouldSendFullLivesNotification(): Boolean {
        val prefs = getSharedPreferences("heart_notifications", MODE_PRIVATE)
        val lastNotifiedHearts = prefs.getInt("last_notified_hearts", 0)
        if (lastNotifiedHearts == 15) return false

        // Only notify shortly after hearts first became full to avoid stale alerts on app open
        val lp = LessonProgress(this)
        val fullSince = lp.getHeartsFullSince()
        if (fullSince <= 0L) return false
        val ageMs = java.lang.System.currentTimeMillis() - fullSince
        val MAX_AGE_FOR_NOTIFICATION_MS = 30L * 60L * 1000L // 30 minutes window
        return ageMs in 1..MAX_AGE_FOR_NOTIFICATION_MS
    }

    private fun showFullLivesNotification(context: Context) {
        createChannel(context)
        
        // Create intent to open home screen
        val intent = Intent(context, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_alex)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText("Životy jsou plné!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        if (NotificationPermission.notifyIfAllowed(context, NOTIFICATION_ID, notification)) {
            val prefs = getSharedPreferences("heart_notifications", MODE_PRIVATE)
            prefs.edit().putInt("last_notified_hearts", 15).apply()
        }
    }

    private fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Heart Refill",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "Notifikace při doplnění srdcí"
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "heart_refill_channel"
        private const val NOTIFICATION_ID = 1001
        private const val JOB_ID = 2001

        fun scheduleNext(context: Context, progress: LessonProgress = LessonProgress(context)) {
            val remainingMs = progress.millisUntilNextHeart()
            if (remainingMs < 0) {
                cancel(context)
                return
            }
            val component = ComponentName(context, HeartRefillJobService::class.java)
            val jobInfo = JobInfo.Builder(JOB_ID, component)
                .setMinimumLatency((remainingMs + 2000L).coerceAtLeast(0L))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE)
                .build()
            val scheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            scheduler.schedule(jobInfo)
        }

        fun cancel(context: Context) {
            val scheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            scheduler.cancel(JOB_ID)
        }
    }
}


