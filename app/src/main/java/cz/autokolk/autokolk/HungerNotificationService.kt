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
import androidx.core.app.NotificationManagerCompat

class HungerNotificationService : JobService() {
    override fun onStartJob(params: JobParameters?): Boolean {
        val hungerManager = HungerManager(this)
        val hunger = hungerManager.getCurrentHunger()
        
        // Get lion name from preferences
        val prefs = getSharedPreferences("lesson_progress", MODE_PRIVATE)
        val lionName = prefs.getString("lion_name", "Alex") ?: "Alex"
        
        // Check if we should send a notification for this hunger level
        if (shouldSendHungerNotification(hunger)) {
            showHungerNotification(this, hunger, lionName)
        }
        
        // Schedule next check
        scheduleNext(this, hungerManager)
        
        // No background work; finished
        return false
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        // No reschedule on stop
        return false
    }

    private fun shouldSendHungerNotification(hunger: Int): Boolean {
        // Send notification for every 10% threshold (90%, 80%, 70%, etc.)
        // Only send if hunger is below 100% and at a 10% boundary
        if (hunger >= 100) return false
        
        val hungerLevel = (hunger / 10) * 10 // Round down to nearest 10
        val prefs = getSharedPreferences("hunger_notifications", MODE_PRIVATE)
        val lastNotifiedLevel = prefs.getInt("last_notified_hunger_level", 100)
        
        // Only notify if we've crossed a 10% threshold downward
        return hungerLevel < lastNotifiedLevel && hungerLevel % 10 == 0
    }

    private fun showHungerNotification(context: Context, hunger: Int, lionName: String) {
        createChannel(context)
        
        // Create intent to open Alex page
        val intent = Intent(context, AlexActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 1, intent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val hungerLevel = (hunger / 10) * 10 // Round down to nearest 10
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_alex)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText("$lionName is hungry! He's on ${hungerLevel}%!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
        
        // Remember this notification level to avoid duplicates
        val prefs = getSharedPreferences("hunger_notifications", MODE_PRIVATE)
        prefs.edit().putInt("last_notified_hunger_level", hungerLevel).apply()
    }

    private fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Hunger Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "Notifikace o hladu Alexe"
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "hunger_notification_channel"
        private const val NOTIFICATION_ID = 1002
        private const val JOB_ID = 2002

        fun scheduleNext(context: Context, hungerManager: HungerManager = HungerManager(context)) {
            // Schedule next check aligned with the next 10% boundary crossing
            val boundaryDelay = hungerManager.millisUntilNextTenPercentBoundary()
            // Fallback to next point tick if boundary delay is zero
            val pointDelay = hungerManager.millisUntilNextPoint()
            val delayCandidate = if (boundaryDelay > 0L) boundaryDelay else pointDelay
            val delay = delayCandidate.coerceAtLeast(15L * 60L * 1000L)
            val component = ComponentName(context, HungerNotificationService::class.java)
            val jobInfo = JobInfo.Builder(JOB_ID, component)
                .setMinimumLatency(delay)
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
