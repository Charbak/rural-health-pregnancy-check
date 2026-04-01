package com.anc.ruralhealth.utils

import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Calculator for ANC schedule based on WHO 2016 guidelines
 * Calculates gestational age and schedules 4 ANC visits
 */
object ANCScheduleCalculator {
    
    /**
     * Calculate gestational age from LMP
     */
    fun calculateGestationalAge(lmp: Date): Pair<Int, Int> {
        val currentDate = Date()
        val diffInMillis = currentDate.time - lmp.time
        val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis).toInt()
        
        val weeks = diffInDays / 7
        val days = diffInDays % 7
        
        return Pair(weeks, days)
    }
    
    /**
     * Calculate EDD from LMP (280 days from LMP)
     */
    fun calculateEDD(lmp: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = lmp
        calendar.add(Calendar.DAY_OF_YEAR, 280)
        return calendar.time
    }
    
    /**
     * Calculate LMP from EDD
     */
    fun calculateLMPFromEDD(edd: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = edd
        calendar.add(Calendar.DAY_OF_YEAR, -280)
        return calendar.time
    }
    
    /**
     * Generate ANC visit schedule based on WHO 2016 guidelines
     * Returns list of visit details: (visitNumber, visitType, minWeek, maxWeek, scheduledWeek)
     */
    fun generateANCSchedule(lmp: Date): List<ANCVisitSchedule> {
        return listOf(
            ANCVisitSchedule(
                visitNumber = 1,
                visitType = "ANC1",
                gestationalWeekMin = 8,
                gestationalWeekMax = 16,
                scheduledWeek = 12,
                description = "Confirmation and baseline screening"
            ),
            ANCVisitSchedule(
                visitNumber = 2,
                visitType = "ANC2",
                gestationalWeekMin = 20,
                gestationalWeekMax = 24,
                scheduledWeek = 22,
                description = "PIH and anemia screening"
            ),
            ANCVisitSchedule(
                visitNumber = 3,
                visitType = "ANC3",
                gestationalWeekMin = 28,
                gestationalWeekMax = 32,
                scheduledWeek = 30,
                description = "Multiple pregnancy exclusion"
            ),
            ANCVisitSchedule(
                visitNumber = 4,
                visitType = "ANC4",
                gestationalWeekMin = 36,
                gestationalWeekMax = 40,
                scheduledWeek = 38,
                description = "Birth preparedness"
            )
        )
    }
    
    /**
     * Calculate scheduled date for a visit based on LMP and scheduled week
     */
    fun calculateScheduledDate(lmp: Date, scheduledWeek: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = lmp
        calendar.add(Calendar.WEEK_OF_YEAR, scheduledWeek)
        return calendar.time
    }
    
    /**
     * Calculate reminder dates (7 days before and 2 days before)
     */
    fun calculateReminderDates(scheduledDate: Date): Pair<Date, Date> {
        val calendar = Calendar.getInstance()
        
        // 7 days before
        calendar.time = scheduledDate
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val reminder7Days = calendar.time
        
        // 2 days before
        calendar.time = scheduledDate
        calendar.add(Calendar.DAY_OF_YEAR, -2)
        val reminder2Days = calendar.time
        
        return Pair(reminder7Days, reminder2Days)
    }
    
    /**
     * Check if a visit is overdue
     */
    fun isVisitOverdue(scheduledDate: Date): Boolean {
        return Date().after(scheduledDate)
    }
    
    /**
     * Calculate days until visit
     */
    fun daysUntilVisit(scheduledDate: Date): Int {
        val diffInMillis = scheduledDate.time - Date().time
        return TimeUnit.MILLISECONDS.toDays(diffInMillis).toInt()
    }
    
    /**
     * Generate unique pregnancy ID
     */
    fun generatePregnancyId(district: String, registrationDate: Date): String {
        val calendar = Calendar.getInstance()
        calendar.time = registrationDate
        val year = calendar.get(Calendar.YEAR)
        val month = String.format("%02d", calendar.get(Calendar.MONTH) + 1)
        val random = (1000..9999).random()
        
        return "ANC-${district.take(3).uppercase()}-$year$month-$random"
    }
}

/**
 * Data class for ANC visit schedule
 */
data class ANCVisitSchedule(
    val visitNumber: Int,
    val visitType: String,
    val gestationalWeekMin: Int,
    val gestationalWeekMax: Int,
    val scheduledWeek: Int,
    val description: String
)

// Made with Bob
