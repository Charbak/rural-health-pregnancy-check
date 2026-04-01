package com.anc.ruralhealth.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Entity for user management and role-based access control
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // User Details
    val userId: String, // Unique user ID
    val name: String,
    val mobileNumber: String,
    val email: String?,
    
    // Role-based Access
    val role: String, // "PREGNANT_WOMAN", "ASHA", "ANM", "NURSE", "MEDICAL_OFFICER", "DISTRICT_ADMIN"
    val designation: String?,
    
    // Location
    val village: String?,
    val block: String?,
    val district: String,
    val state: String,
    
    // Authentication
    val passwordHash: String,
    val pin: String?, // Simple 4-digit PIN for rural users
    
    // Status
    val isActive: Boolean = true,
    val isVerified: Boolean = false,
    val lastLoginDate: Date? = null,
    
    // Preferences
    val preferredLanguage: String = "hi", // "hi" for Hindi, "en" for English
    val notificationsEnabled: Boolean = true,
    
    // Timestamps
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)


