package com.anc.ruralhealth.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.anc.ruralhealth.worker.ReminderWorker

/**
 * Broadcast receiver for handling notification events
 */
class NotificationReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                // Reschedule reminders after device reboot
                scheduleReminderCheck(context)
            }
        }
    }
    
    private fun scheduleReminderCheck(context: Context) {
        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .build()
        
        WorkManager.getInstance(context).enqueue(workRequest)
    }
}

// Made with Bob
