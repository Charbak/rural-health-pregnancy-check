package com.anc.ruralhealth

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.anc.ruralhealth.worker.ReminderWorker
import java.util.concurrent.TimeUnit

/**
 * Application class for ANC Rural Health
 */
class ANCApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Schedule periodic reminder checks
        scheduleReminderWorker()
    }
    
    private fun scheduleReminderWorker() {
        val reminderWorkRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
            15, TimeUnit.MINUTES // Check every 15 minutes
        ).build()
        
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "ReminderWorker",
            ExistingPeriodicWorkPolicy.KEEP,
            reminderWorkRequest
        )
    }
}

// Made with Bob
