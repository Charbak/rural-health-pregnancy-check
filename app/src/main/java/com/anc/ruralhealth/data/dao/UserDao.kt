package com.anc.ruralhealth.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.anc.ruralhealth.data.entity.UserEntity
import java.util.Date

/**
 * Data Access Object for User operations
 */
@Dao
interface UserDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity): Long
    
    @Update
    suspend fun update(user: UserEntity)
    
    @Delete
    suspend fun delete(user: UserEntity)
    
    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getById(id: Long): UserEntity?
    
    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getByUserId(userId: String): UserEntity?
    
    @Query("SELECT * FROM users WHERE mobileNumber = :mobileNumber")
    suspend fun getByMobileNumber(mobileNumber: String): UserEntity?
    
    @Query("SELECT * FROM users WHERE role = :role AND isActive = 1")
    fun getUsersByRole(role: String): LiveData<List<UserEntity>>
    
    @Query("SELECT * FROM users WHERE district = :district AND role IN (:roles) AND isActive = 1")
    fun getUsersByDistrictAndRoles(district: String, roles: List<String>): LiveData<List<UserEntity>>
    
    @Query("UPDATE users SET lastLoginDate = :loginDate WHERE id = :id")
    suspend fun updateLastLogin(id: Long, loginDate: Date)
    
    @Query("UPDATE users SET preferredLanguage = :language WHERE id = :id")
    suspend fun updateLanguagePreference(id: Long, language: String)
    
    @Query("UPDATE users SET notificationsEnabled = :enabled WHERE id = :id")
    suspend fun updateNotificationPreference(id: Long, enabled: Boolean)
}


