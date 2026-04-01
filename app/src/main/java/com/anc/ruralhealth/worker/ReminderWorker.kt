package com.anc.ruralhealth.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.anc.ruralhealth.data.database.AppDatabase
import com.anc.ruralhealth.notification.NotificationHelper
import java.util.Date

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
    
    override suspend fun doWork(): Result {
        return try {
            // Get pending reminders
            val pendingReminders = reminderDao.getPendingReminders(Date())
            
            // Send notifications for each pending reminder
            for (reminder in pendingReminders) {
                when (reminder.reminderType) {
                    "7_days_before", "2_days_before" -> {
                        notificationHelper.showReminderNotification(
                            notificationId = reminder.notificationId,
                            title = reminder.title,
                            message = reminder.message,
                            pregnancyId = reminder.pregnancyId
                        )
                    }
                    "missed_visit" -> {
                        notificationHelper.showMissedVisitAlert(
                            notificationId = reminder.notificationId,
                            title = reminder.title,
                            message = reminder.message,
                            pregnancyId = reminder.pregnancyId
                        )
                    }
                }
                
                // Mark reminder as sent
                reminderDao.markAsSent(
                    id = reminder.id,
                    sentTime = Date(),
                    status = "delivered"
                )
            }
            
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}


