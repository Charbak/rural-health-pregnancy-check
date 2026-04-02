package com.anc.ruralhealth.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.anc.ruralhealth.data.database.AppDatabase
import com.anc.ruralhealth.notification.NotificationHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Background worker for checking and sending reminders
 */
class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val database = AppDatabase.getDatabase(context)
    private val reminderDao = database.reminderDao()
    private val notificationHelper = NotificationHelper(context)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    override suspend fun doWork(): Result {
        return try {
            val currentTime = Date()
            Log.d(TAG, "========================================")
            Log.d(TAG, "ReminderWorker started at ${dateFormat.format(currentTime)}")

            // Get pending reminders
            val pendingReminders = reminderDao.getPendingReminders(currentTime)
            Log.d(TAG, "Found ${pendingReminders.size} pending reminders")

            if (pendingReminders.isEmpty()) {
                Log.d(TAG, "No reminders to process at this time")
            }

            // Send notifications for each pending reminder
            for (reminder in pendingReminders) {
                Log.d(TAG, "Processing reminder ID: ${reminder.id}")
                Log.d(TAG, "  Type: ${reminder.reminderType}")
                Log.d(TAG, "  Scheduled: ${dateFormat.format(reminder.scheduledTime)}")
                Log.d(TAG, "  Title: ${reminder.title}")
                Log.d(TAG, "  Message: ${reminder.message}")

                when (reminder.reminderType) {
                    "7_days_before", "2_days_before" -> {
                        Log.d(TAG, "  Sending visit reminder notification...")
                        notificationHelper.showReminderNotification(
                            notificationId = reminder.notificationId,
                            title = reminder.title,
                            message = reminder.message,
                            pregnancyId = reminder.pregnancyId
                        )
                    }
                    "missed_visit" -> {
                        Log.d(TAG, "  Sending missed visit alert...")
                        notificationHelper.showMissedVisitAlert(
                            notificationId = reminder.notificationId,
                            title = reminder.title,
                            message = reminder.message,
                            pregnancyId = reminder.pregnancyId
                        )
                    }
                    else -> {
                        Log.w(TAG, "  Unknown reminder type: ${reminder.reminderType}")
                    }
                }

                // Mark reminder as sent
                reminderDao.markAsSent(
                    id = reminder.id,
                    sentTime = Date(),
                    status = "delivered"
                )
                Log.d(TAG, "  Reminder marked as sent")
            }

            Log.d(TAG, "ReminderWorker completed successfully")
            Log.d(TAG, "========================================")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "ReminderWorker failed with error", e)
            Log.e(TAG, "Error message: ${e.message}")
            Log.e(TAG, "========================================")
            Result.retry()
        }
    }

    companion object {
        private const val TAG = "ANC_ReminderWorker"
    }
}


