package com.anc.ruralhealth.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.anc.ruralhealth.R
import com.anc.ruralhealth.ui.MainActivity

/**
 * Helper class for managing notifications
 */
class NotificationHelper(private val context: Context) {

    companion object {
        private const val TAG = "ANC_NotificationHelper"
        const val CHANNEL_ID_REMINDERS = "anc_reminders"
        const val CHANNEL_ID_ALERTS = "anc_alerts"
        const val CHANNEL_NAME_REMINDERS = "ANC Visit Reminders"
        const val CHANNEL_NAME_ALERTS = "Missed Visit Alerts"
    }

    init {
        createNotificationChannels()
    }
    
    /**
     * Create notification channels for Android O and above
     */
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(TAG, "Creating notification channels (Android O+)")

            // Reminders channel
            val remindersChannel = NotificationChannel(
                CHANNEL_ID_REMINDERS,
                CHANNEL_NAME_REMINDERS,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for upcoming ANC visits"
                enableVibration(true)
            }

            // Alerts channel
            val alertsChannel = NotificationChannel(
                CHANNEL_ID_ALERTS,
                CHANNEL_NAME_ALERTS,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts for missed ANC visits"
                enableVibration(true)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(remindersChannel)
            notificationManager.createNotificationChannel(alertsChannel)

            Log.d(TAG, "Notification channels created successfully")
        } else {
            Log.d(TAG, "Android version < O, notification channels not needed")
        }
    }
    
    /**
     * Show reminder notification
     */
    fun showReminderNotification(
        notificationId: Int,
        title: String,
        message: String,
        pregnancyId: Long
    ) {
        Log.d(TAG, "Showing reminder notification")
        Log.d(TAG, "  ID: $notificationId")
        Log.d(TAG, "  Title: $title")
        Log.d(TAG, "  Message: $message")
        Log.d(TAG, "  Pregnancy ID: $pregnancyId")

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("pregnancyId", pregnancyId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_REMINDERS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .build()

        try {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
            Log.d(TAG, "Reminder notification posted successfully")
        } catch (e: SecurityException) {
            Log.e(TAG, "Failed to post notification - permission denied", e)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to post notification", e)
        }
    }
    
    /**
     * Show missed visit alert
     */
    fun showMissedVisitAlert(
        notificationId: Int,
        title: String,
        message: String,
        pregnancyId: Long
    ) {
        Log.d(TAG, "Showing missed visit alert")
        Log.d(TAG, "  ID: $notificationId")
        Log.d(TAG, "  Title: $title")
        Log.d(TAG, "  Message: $message")
        Log.d(TAG, "  Pregnancy ID: $pregnancyId")

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("pregnancyId", pregnancyId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_ALERTS)
            .setSmallIcon(R.drawable.ic_alert)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .build()

        try {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
            Log.d(TAG, "Missed visit alert posted successfully")
        } catch (e: SecurityException) {
            Log.e(TAG, "Failed to post alert - permission denied", e)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to post alert", e)
        }
    }
    
    /**
     * Cancel notification
     */
    fun cancelNotification(notificationId: Int) {
        NotificationManagerCompat.from(context).cancel(notificationId)
    }
}


