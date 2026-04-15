package cz.autokolk

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import cz.autokolk.ui.navigation.ComposeNavIntent
import cz.autokolk.ui.screens.alex.AlexAssetResolver

class HungerNotificationService : JobService() {
    override fun onStartJob(params: JobParameters?): Boolean {
        val hungerManager = HungerManager(this)
        val hunger = hungerManager.getCurrentHunger()

        val prefsLesson = getSharedPreferences("lesson_progress", MODE_PRIVATE)
        val lionName = prefsLesson.getString("lion_name", "Alex") ?: "Alex"

        val tier = nextTierToNotify(hunger, getSharedPreferences(PREFS_NOTIF, MODE_PRIVATE))
        if (tier != null) {
            showHungerNotification(this, hunger, lionName, tier)
        }

        scheduleNext(this, hungerManager)
        return false
    }

    override fun onStopJob(params: JobParameters?): Boolean = false

    private fun nextTierToNotify(hunger: Int, prefs: android.content.SharedPreferences): Int? {
        if (hunger >= 100) return null
        if (hunger <= 5 && !prefs.getBoolean(KEY_SENT_5, false)) return 5
        if (hunger <= 20 && !prefs.getBoolean(KEY_SENT_20, false)) return 20
        if (hunger <= 50 && !prefs.getBoolean(KEY_SENT_50, false)) return 50
        return null
    }

    private fun markTiersHandled(prefs: android.content.SharedPreferences, tier: Int) {
        val ed = prefs.edit()
        when (tier) {
            5 -> ed.putBoolean(KEY_SENT_5, true)
                .putBoolean(KEY_SENT_20, true)
                .putBoolean(KEY_SENT_50, true)
            20 -> ed.putBoolean(KEY_SENT_20, true).putBoolean(KEY_SENT_50, true)
            50 -> ed.putBoolean(KEY_SENT_50, true)
        }
        ed.apply()
    }

    private fun notificationBody(context: Context, lionName: String, tier: Int): String = when (tier) {
        5 -> context.getString(R.string.hunger_notification_body_5, lionName)
        20 -> context.getString(R.string.hunger_notification_body_20, lionName)
        else -> context.getString(R.string.hunger_notification_body_50, lionName)
    }

    private fun loadAlexBigPicture(context: Context, hunger: Int): Bitmap? {
        val sun = LessonProgress(context).isSunglassesEnabled()
        val file = AlexAssetResolver.firstExistingAlexFaceFromHunger(context, hunger, sun)
        return try {
            context.assets.open("images/alex/$file").use { BitmapFactory.decodeStream(it) }
        } catch (_: Exception) {
            null
        }
    }

    private fun showHungerNotification(context: Context, hunger: Int, lionName: String, tier: Int) {
        createChannel(context)

        val openAlex = Intent(context, ComposeMainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(ComposeNavIntent.EXTRA_OPEN_TAB, ComposeNavIntent.OPEN_TAB_ALEX)
        }

        val contentPi = PendingIntent.getActivity(
            context,
            10,
            openAlex,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val feedPi = PendingIntent.getActivity(
            context,
            11,
            openAlex,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val bigPic = loadAlexBigPicture(context, hunger)
        val text = notificationBody(context, lionName, tier)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_alex)
            .setContentTitle(context.getString(R.string.hunger_notification_title))
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(contentPi)
            .addAction(0, context.getString(R.string.hunger_notification_action_feed), feedPi)

        if (bigPic != null) {
            builder.setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(bigPic)
                    .setSummaryText(text),
            )
        }

        val notification = builder.build()

        if (NotificationPermission.notifyIfAllowed(context, NOTIFICATION_ID, notification)) {
            markTiersHandled(getSharedPreferences(PREFS_NOTIF, MODE_PRIVATE), tier)
        }
    }

    private fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.hunger_notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT,
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
        private const val PREFS_NOTIF = "hunger_notifications"
        private const val KEY_SENT_50 = "sent_warn_50"
        private const val KEY_SENT_20 = "sent_warn_20"
        private const val KEY_SENT_5 = "sent_warn_5"

        fun resetTierFlags(context: Context) {
            context.getSharedPreferences(PREFS_NOTIF, Context.MODE_PRIVATE).edit()
                .putBoolean(KEY_SENT_50, false)
                .putBoolean(KEY_SENT_20, false)
                .putBoolean(KEY_SENT_5, false)
                .apply()
        }

        fun scheduleNext(context: Context, hungerManager: HungerManager = HungerManager(context)) {
            val boundaryDelay = hungerManager.millisUntilNextNotificationBandEdge()
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
