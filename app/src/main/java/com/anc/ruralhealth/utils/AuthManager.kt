package com.anc.ruralhealth.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.MessageDigest

/**
 * Authentication and authorization manager
 * Handles role-based access control
 */
class AuthManager(context: Context) {
    
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    
    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "anc_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    
    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_DISTRICT = "district"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        
        // User roles
        const val ROLE_PREGNANT_WOMAN = "PREGNANT_WOMAN"
        const val ROLE_ASHA = "ASHA"
        const val ROLE_ANM = "ANM"
        const val ROLE_NURSE = "NURSE"
        const val ROLE_MEDICAL_OFFICER = "MEDICAL_OFFICER"
        const val ROLE_DISTRICT_ADMIN = "DISTRICT_ADMIN"
    }
    
    /**
     * Hash password using SHA-256
     */
    fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
    
    /**
     * Login user
     */
    fun login(userId: String, role: String, userName: String, district: String) {
        sharedPreferences.edit().apply {
            putString(KEY_USER_ID, userId)
            putString(KEY_USER_ROLE, role)
            putString(KEY_USER_NAME, userName)
            putString(KEY_DISTRICT, district)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }
    
    /**
     * Logout user
     */
    fun logout() {
        sharedPreferences.edit().clear().apply()
    }
    
    /**
     * Check if user is logged in
     */
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }
    
    /**
     * Get current user ID
     */
    fun getUserId(): String? {
        return sharedPreferences.getString(KEY_USER_ID, null)
    }
    
    /**
     * Get current user role
     */
    fun getUserRole(): String? {
        return sharedPreferences.getString(KEY_USER_ROLE, null)
    }
    
    /**
     * Get current user name
     */
    fun getUserName(): String? {
        return sharedPreferences.getString(KEY_USER_NAME, null)
    }
    
    /**
     * Get current user district
     */
    fun getDistrict(): String? {
        return sharedPreferences.getString(KEY_DISTRICT, null)
    }
    
    /**
     * Check if user has permission for an action
     */
    fun hasPermission(permission: Permission): Boolean {
        val role = getUserRole() ?: return false
        
        return when (permission) {
            Permission.VIEW_OWN_PREGNANCY -> {
                role == ROLE_PREGNANT_WOMAN
            }
            Permission.REGISTER_PREGNANCY -> {
                role in listOf(ROLE_ASHA, ROLE_ANM, ROLE_NURSE, ROLE_MEDICAL_OFFICER)
            }
            Permission.RECORD_VISIT -> {
                role in listOf(ROLE_ASHA, ROLE_ANM, ROLE_NURSE, ROLE_MEDICAL_OFFICER)
            }
            Permission.VIEW_ALL_PREGNANCIES -> {
                role in listOf(ROLE_MEDICAL_OFFICER, ROLE_DISTRICT_ADMIN)
            }
            Permission.VIEW_DISTRICT_DASHBOARD -> {
                role == ROLE_DISTRICT_ADMIN
            }
            Permission.EXPORT_REPORTS -> {
                role in listOf(ROLE_MEDICAL_OFFICER, ROLE_DISTRICT_ADMIN)
            }
        }
    }
    
    /**
     * Check if user is provider (ASHA/ANM/Nurse)
     */
    fun isProvider(): Boolean {
        val role = getUserRole()
        return role in listOf(ROLE_ASHA, ROLE_ANM, ROLE_NURSE)
    }
    
    /**
     * Check if user is medical officer
     */
    fun isMedicalOfficer(): Boolean {
        return getUserRole() == ROLE_MEDICAL_OFFICER
    }
    
    /**
     * Check if user is district admin
     */
    fun isDistrictAdmin(): Boolean {
        return getUserRole() == ROLE_DISTRICT_ADMIN
    }
}

/**
 * Enum for permissions
 */
enum class Permission {
    VIEW_OWN_PREGNANCY,
    REGISTER_PREGNANCY,
    RECORD_VISIT,
    VIEW_ALL_PREGNANCIES,
    VIEW_DISTRICT_DASHBOARD,
    EXPORT_REPORTS
}

// Made with Bob
