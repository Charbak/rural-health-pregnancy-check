package com.anc.ruralhealth.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Entity for tracking reminders and notifications
 */
@Entity(
    tableName = "reminders",
    foreignKeys = [
        ForeignKey(
            entity = ANCVisitEntity::class,
            parentColumns = ["id"],
            childColumns = ["visitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("visitId"), Index("scheduledTime")]
)
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val visitId: Long,
    val pregnancyId: Long,
    
    // Reminder Details
    val reminderType: String, // "7_days_before", "2_days_before", "missed_visit"
    val scheduledTime: Date,
    
    // Status
    val isSent: Boolean = false,
    val sentTime: Date? = null,
    val deliveryStatus: String? = null, // "delivered", "failed", "pending"
    
    // Notification Details
    val title: String,
    val message: String,
    val notificationId: Int,
    
    // Timestamps
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)


