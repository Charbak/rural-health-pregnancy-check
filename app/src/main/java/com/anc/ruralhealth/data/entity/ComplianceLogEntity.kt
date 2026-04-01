package com.anc.ruralhealth.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Entity for tracking compliance and generating alerts
 */
@Entity(
    tableName = "compliance_logs",
    foreignKeys = [
        ForeignKey(
            entity = PregnancyEntity::class,
            parentColumns = ["id"],
            childColumns = ["pregnancyId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("pregnancyId"), Index("logDate")]
)
data class ComplianceLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val pregnancyId: Long,
    val visitId: Long?,
    
    // Compliance Details
    val logType: String, // "visit_completed", "visit_missed", "alert_generated", "escalation"
    val logDate: Date,
    
    // Status
    val complianceStatus: String, // "compliant", "defaulter", "high_risk"
    val missedVisitsCount: Int = 0,
    
    // Alert Details
    val alertSent: Boolean = false,
    val alertSentTo: String?, // User IDs (comma-separated)
    val alertLevel: String?, // "provider", "medical_officer", "district"
    
    // Action Taken
    val actionTaken: String?,
    val actionBy: String?,
    val actionDate: Date?,
    
    // Notes
    val notes: String?,
    
    // Timestamps
    val createdAt: Date = Date()
)


