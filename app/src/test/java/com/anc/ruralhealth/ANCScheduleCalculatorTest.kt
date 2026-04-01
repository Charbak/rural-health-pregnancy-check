package com.anc.ruralhealth

import com.anc.ruralhealth.utils.ANCScheduleCalculator
import org.junit.Assert.*
import org.junit.Test
import java.util.*

/**
 * Unit tests for ANC Schedule Calculator
 */
class ANCScheduleCalculatorTest {
    
    @Test
    fun testCalculateGestationalAge() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.WEEK_OF_YEAR, -10) // 10 weeks ago
        val lmp = calendar.time
        
        val (weeks, days) = ANCScheduleCalculator.calculateGestationalAge(lmp)
        
        assertTrue("Gestational age should be around 10 weeks", weeks in 9..11)
    }
    
    @Test
    fun testCalculateEDD() {
        val calendar = Calendar.getInstance()
        val lmp = calendar.time
        
        val edd = ANCScheduleCalculator.calculateEDD(lmp)
        
        calendar.add(Calendar.DAY_OF_YEAR, 280)
        val expectedEdd = calendar.time
        
        // Allow 1 day difference due to time calculations
        val diff = Math.abs(edd.time - expectedEdd.time)
        assertTrue("EDD should be 280 days from LMP", diff < 24 * 60 * 60 * 1000)
    }
    
    @Test
    fun testGenerateANCSchedule() {
        val lmp = Date()
        val schedule = ANCScheduleCalculator.generateANCSchedule(lmp)
        
        assertEquals("Should have 4 ANC visits", 4, schedule.size)
        
        // Verify visit numbers
        assertEquals(1, schedule[0].visitNumber)
        assertEquals(2, schedule[1].visitNumber)
        assertEquals(3, schedule[2].visitNumber)
        assertEquals(4, schedule[3].visitNumber)
        
        // Verify visit types
        assertEquals("ANC1", schedule[0].visitType)
        assertEquals("ANC2", schedule[1].visitType)
        assertEquals("ANC3", schedule[2].visitType)
        assertEquals("ANC4", schedule[3].visitType)
        
        // Verify gestational weeks
        assertEquals(12, schedule[0].scheduledWeek)
        assertEquals(22, schedule[1].scheduledWeek)
        assertEquals(30, schedule[2].scheduledWeek)
        assertEquals(38, schedule[3].scheduledWeek)
    }
    
    @Test
    fun testCalculateReminderDates() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 10) // 10 days from now
        val scheduledDate = calendar.time
        
        val (reminder7Days, reminder2Days) = ANCScheduleCalculator.calculateReminderDates(scheduledDate)
        
        // Verify 7 days before
        val diff7Days = (scheduledDate.time - reminder7Days.time) / (24 * 60 * 60 * 1000)
        assertEquals("Reminder should be 7 days before", 7, diff7Days)
        
        // Verify 2 days before
        val diff2Days = (scheduledDate.time - reminder2Days.time) / (24 * 60 * 60 * 1000)
        assertEquals("Reminder should be 2 days before", 2, diff2Days)
    }
    
    @Test
    fun testIsVisitOverdue() {
        val calendar = Calendar.getInstance()
        
        // Past date
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val pastDate = calendar.time
        assertTrue("Past date should be overdue", ANCScheduleCalculator.isVisitOverdue(pastDate))
        
        // Future date
        calendar.time = Date()
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val futureDate = calendar.time
        assertFalse("Future date should not be overdue", ANCScheduleCalculator.isVisitOverdue(futureDate))
    }
    
    @Test
    fun testGeneratePregnancyId() {
        val district = "Mumbai"
        val date = Date()
        
        val pregnancyId = ANCScheduleCalculator.generatePregnancyId(district, date)
        
        assertTrue("Pregnancy ID should start with ANC-", pregnancyId.startsWith("ANC-"))
        assertTrue("Pregnancy ID should contain district code", pregnancyId.contains("MUM"))
        assertTrue("Pregnancy ID should have correct format", pregnancyId.matches(Regex("ANC-[A-Z]{3}-\\d{6}-\\d{4}")))
    }
}

// Made with Bob
