package cz.autokolk

import android.Manifest
import android.app.Notification
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

internal object NotificationPermission {

    fun canPostNotifications(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Posts a notification only when [canPostNotifications] is true; catches [SecurityException]
     * for Lint/runtime edge cases. Returns whether [NotificationManagerCompat.notify] ran successfully.
     */
    fun notifyIfAllowed(context: Context, notificationId: Int, notification: Notification): Boolean {
        if (!canPostNotifications(context)) return false
        return try {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
            true
        } catch (_: SecurityException) {
            false
        }
    }
}
