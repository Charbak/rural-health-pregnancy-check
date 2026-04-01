package com.anc.ruralhealth.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.anc.ruralhealth.data.database.AppDatabase
import com.anc.ruralhealth.data.entity.ComplianceLogEntity
import com.anc.ruralhealth.data.entity.ReminderEntity
import java.util.Date

/**
 * Background worker for compliance tracking and alerts
 */
class ComplianceWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    private val database = AppDatabase.getDatabase(context)
    private val ancVisitDao = database.ancVisitDao()
    private val complianceLogDao = database.complianceLogDao()
    private val reminderDao = database.reminderDao()
    
    override suspend fun doWork(): Result {
        return try {
            // Check for missed visits
            val missedVisits = ancVisitDao.getMissedVisits(Date()).value ?: emptyList()
            
            for (visit in missedVisits) {
                // Check if alert already sent
                if (!visit.missedVisitAlertSent) {
                    // Create compliance log
                    val complianceLog = ComplianceLogEntity(
                        pregnancyId = visit.pregnancyId,
                        visitId = visit.id,
                        logType = "visit_missed",
                        logDate = Date(),
                        complianceStatus = "defaulter",
                        missedVisitsCount = ancVisitDao.getMissedVisitCount(visit.pregnancyId, Date()),
                        alertSent = false,
                        alertSentTo = null,
                        alertLevel = "provider",
                        actionTaken = null,
                        actionBy = null,
                        actionDate = null,
                        notes = "Visit ${visit.visitType} was missed on ${visit.scheduledDate}"
                    )
                    
                    complianceLogDao.insert(complianceLog)
                    
                    // Create missed visit reminder
                    val missedReminder = ReminderEntity(
                        visitId = visit.id,
                        pregnancyId = visit.pregnancyId,
                        reminderType = "missed_visit",
                        scheduledTime = Date(),
                        title = "Missed ANC Visit",
                        message = "You missed your ${visit.visitType} visit. Please schedule it as soon as possible.",
                        notificationId = (2000 + visit.id).toInt()
                    )
                    
                    reminderDao.insert(missedReminder)
                    
                    // Mark alert as sent
                    ancVisitDao.markMissedVisitAlertSent(visit.id)
                }
            }
            
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}

// Made with Bob
