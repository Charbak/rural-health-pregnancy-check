package com.anc.ruralhealth.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Entity representing ANC visit schedule and completion
 * Tracks scheduled and completed visits based on WHO 2016 guidelines
 */
@Entity(
    tableName = "anc_visits",
    foreignKeys = [
        ForeignKey(
            entity = PregnancyEntity::class,
            parentColumns = ["id"],
            childColumns = ["pregnancyId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("pregnancyId"), Index("scheduledDate")]
)
data class ANCVisitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // Foreign Key
    val pregnancyId: Long,
    
    // Visit Details
    val visitNumber: Int, // 1, 2, 3, 4
    val visitType: String, // "ANC1", "ANC2", "ANC3", "ANC4"
    val gestationalWeekMin: Int, // Minimum week for this visit
    val gestationalWeekMax: Int, // Maximum week for this visit
    
    // Schedule
    val scheduledDate: Date,
    val scheduledWeek: Int,
    
    // Completion Status
    val isCompleted: Boolean = false,
    val completedDate: Date? = null,
    val completedBy: String? = null, // User ID of provider
    val actualGestationalWeek: Int? = null,
    
    // Visit Details
    val weight: Float? = null,
    val bloodPressureSystolic: Int? = null,
    val bloodPressureDiastolic: Int? = null,
    val hemoglobin: Float? = null,
    val fundalHeight: Float? = null,
    val fetalHeartRate: Int? = null,
    
    // Screening Results
    val pihScreening: String? = null, // "normal", "suspected", "confirmed"
    val anemiaScreening: String? = null,
    val gestationalDiabetesScreening: String? = null,
    
    // Services Provided
    val ttInjectionGiven: Boolean = false,
    val ifaTabletsGiven: Boolean = false,
    val calciumGiven: Boolean = false,
    val dewormingDone: Boolean = false,
    
    // Notes
    val notes: String? = null,
    val complications: String? = null,
    val referralRequired: Boolean = false,
    val referralDetails: String? = null,
    
    // Reminders
    val reminderSent7Days: Boolean = false,
    val reminderSent2Days: Boolean = false,
    val missedVisitAlertSent: Boolean = false,
    
    // Sync Status
    val isSynced: Boolean = false,
    val lastSyncDate: Date? = null,
    
    // Timestamps
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

// Made with Bob
