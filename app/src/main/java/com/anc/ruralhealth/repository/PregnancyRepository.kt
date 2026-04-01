package com.anc.ruralhealth.repository

import androidx.lifecycle.LiveData
import com.anc.ruralhealth.data.dao.PregnancyDao
import com.anc.ruralhealth.data.dao.ANCVisitDao
import com.anc.ruralhealth.data.dao.ReminderDao
import com.anc.ruralhealth.data.entity.PregnancyEntity
import com.anc.ruralhealth.data.entity.ANCVisitEntity
import com.anc.ruralhealth.data.entity.ReminderEntity
import com.anc.ruralhealth.utils.ANCScheduleCalculator
import java.util.Date

/**
 * Repository for pregnancy-related operations
 * Handles business logic and data operations
 */
class PregnancyRepository(
    private val pregnancyDao: PregnancyDao,
    private val ancVisitDao: ANCVisitDao,
    private val reminderDao: ReminderDao
) {
    
    /**
     * Register a new pregnancy
     * Creates pregnancy record, schedules ANC visits, and sets up reminders
     */
    suspend fun registerPregnancy(
        womanName: String,
        age: Int,
        mobileNumber: String,
        address: String,
        village: String,
        district: String,
        state: String,
        lmp: Date?,
        edd: Date?,
        registeredBy: String,
        bloodGroup: String? = null,
        height: Float? = null,
        weight: Float? = null,
        hemoglobin: Float? = null
    ): Long {
        // Calculate LMP and EDD
        val finalLmp = lmp ?: (edd?.let { ANCScheduleCalculator.calculateLMPFromEDD(it) })
        val finalEdd = edd ?: (finalLmp?.let { ANCScheduleCalculator.calculateEDD(it) })
        
        if (finalLmp == null || finalEdd == null) {
            throw IllegalArgumentException("Either LMP or EDD must be provided")
        }
        
        // Calculate gestational age
        val (weeks, days) = ANCScheduleCalculator.calculateGestationalAge(finalLmp)
        
        // Generate pregnancy ID
        val pregnancyId = ANCScheduleCalculator.generatePregnancyId(district, Date())
        
        // Create pregnancy entity
        val pregnancy = PregnancyEntity(
            pregnancyId = pregnancyId,
            womanName = womanName,
            age = age,
            mobileNumber = mobileNumber,
            address = address,
            village = village,
            district = district,
            state = state,
            lmp = finalLmp,
            edd = finalEdd,
            gestationalAgeWeeks = weeks,
            gestationalAgeDays = days,
            registrationDate = Date(),
            registeredBy = registeredBy,
            bloodGroup = bloodGroup,
            height = height,
            weight = weight,
            hemoglobin = hemoglobin,
            riskFactors = null
        )
        
        // Insert pregnancy
        val pregnancyDbId = pregnancyDao.insert(pregnancy)
        
        // Schedule ANC visits
        scheduleANCVisits(pregnancyDbId, finalLmp)
        
        return pregnancyDbId
    }
    
    /**
     * Schedule ANC visits based on WHO guidelines
     */
    private suspend fun scheduleANCVisits(pregnancyId: Long, lmp: Date) {
        val schedule = ANCScheduleCalculator.generateANCSchedule(lmp)
        val visits = mutableListOf<ANCVisitEntity>()
        
        for (visitSchedule in schedule) {
            val scheduledDate = ANCScheduleCalculator.calculateScheduledDate(lmp, visitSchedule.scheduledWeek)
            
            val visit = ANCVisitEntity(
                pregnancyId = pregnancyId,
                visitNumber = visitSchedule.visitNumber,
                visitType = visitSchedule.visitType,
                gestationalWeekMin = visitSchedule.gestationalWeekMin,
                gestationalWeekMax = visitSchedule.gestationalWeekMax,
                scheduledDate = scheduledDate,
                scheduledWeek = visitSchedule.scheduledWeek
            )
            
            visits.add(visit)
        }
        
        // Insert all visits and get their IDs
        val visitIds = ancVisitDao.insertAll(visits)
        
        // Schedule reminders for each visit using the returned IDs
        scheduleReminders(pregnancyId, visits, visitIds)
    }
    
    /**
     * Schedule reminders for ANC visits
     */
    private suspend fun scheduleReminders(pregnancyId: Long, visits: List<ANCVisitEntity>, visitIds: List<Long>) {
        val reminders = mutableListOf<ReminderEntity>()
        var notificationId = 1000
        
        for ((index, visit) in visits.withIndex()) {
            val visitId = visitIds[index]
            val (reminder7Days, reminder2Days) = ANCScheduleCalculator.calculateReminderDates(visit.scheduledDate)
            
            // 7 days before reminder
            reminders.add(
                ReminderEntity(
                    visitId = visitId,
                    pregnancyId = pregnancyId,
                    reminderType = "7_days_before",
                    scheduledTime = reminder7Days,
                    title = "ANC Visit Reminder",
                    message = "Your ${visit.visitType} visit is scheduled in 7 days",
                    notificationId = notificationId++
                )
            )
            
            // 2 days before reminder
            reminders.add(
                ReminderEntity(
                    visitId = visitId,
                    pregnancyId = pregnancyId,
                    reminderType = "2_days_before",
                    scheduledTime = reminder2Days,
                    title = "ANC Visit Reminder",
                    message = "Your ${visit.visitType} visit is scheduled in 2 days",
                    notificationId = notificationId++
                )
            )
        }
        
        reminderDao.insertAll(reminders)
    }
    
    /**
     * Get all active pregnancies
     */
    fun getAllActivePregnancies(): LiveData<List<PregnancyEntity>> {
        return pregnancyDao.getAllActivePregnancies()
    }
    
    /**
     * Get pregnancies by provider
     */
    fun getPregnanciesByProvider(userId: String): LiveData<List<PregnancyEntity>> {
        return pregnancyDao.getPregnanciesByProvider(userId)
    }
    
    /**
     * Get pregnancy by ID
     */
    suspend fun getPregnancyById(id: Long): PregnancyEntity? {
        return pregnancyDao.getById(id)
    }
    
    /**
     * Update pregnancy details
     */
    suspend fun updatePregnancy(pregnancy: PregnancyEntity) {
        pregnancyDao.update(pregnancy)
    }
    
    /**
     * Complete pregnancy
     */
    suspend fun completePregnancy(id: Long, outcome: String) {
        pregnancyDao.completePregnancy(id, Date(), outcome)
    }
}


