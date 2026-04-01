package com.anc.ruralhealth.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Entity representing a pregnancy registration
 * Core data model for tracking pregnant women
 */
@Entity(tableName = "pregnancies")
data class PregnancyEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // Unique Pregnancy ID
    val pregnancyId: String,
    
    // Demographic Details
    val womanName: String,
    val age: Int,
    val mobileNumber: String,
    val address: String,
    val village: String,
    val district: String,
    val state: String,
    
    // Pregnancy Details
    val lmp: Date?, // Last Menstrual Period
    val edd: Date,  // Expected Date of Delivery (corrected)
    val gestationalAgeWeeks: Int,
    val gestationalAgeDays: Int,
    
    // Registration Details
    val registrationDate: Date,
    val registeredBy: String, // User ID of ASHA/ANM
    
    // Health Details
    val bloodGroup: String?,
    val height: Float?,
    val weight: Float?,
    val hemoglobin: Float?,
    val hasHighRisk: Boolean = false,
    val riskFactors: String?, // JSON array of risk factors
    
    // Status
    val isActive: Boolean = true,
    val completionDate: Date? = null,
    val outcome: String? = null, // "live_birth", "stillbirth", "miscarriage", etc.
    
    // Sync Status
    val isSynced: Boolean = false,
    val lastSyncDate: Date? = null,
    
    // Timestamps
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

// Made with Bob
