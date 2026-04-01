package com.anc.ruralhealth.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.anc.ruralhealth.data.entity.ComplianceLogEntity
import java.util.Date

/**
 * Data Access Object for Compliance Log operations
 */
@Dao
interface ComplianceLogDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: ComplianceLogEntity): Long
    
    @Update
    suspend fun update(log: ComplianceLogEntity)
    
    @Delete
    suspend fun delete(log: ComplianceLogEntity)
    
    @Query("SELECT * FROM compliance_logs WHERE pregnancyId = :pregnancyId ORDER BY logDate DESC")
    fun getLogsByPregnancy(pregnancyId: Long): LiveData<List<ComplianceLogEntity>>
    
    @Query("SELECT * FROM compliance_logs WHERE complianceStatus = 'defaulter' AND alertSent = 0 ORDER BY logDate DESC")
    suspend fun getPendingDefaulterAlerts(): List<ComplianceLogEntity>
    
    @Query("SELECT * FROM compliance_logs WHERE logType = 'visit_missed' ORDER BY logDate DESC LIMIT 100")
    fun getRecentMissedVisits(): LiveData<List<ComplianceLogEntity>>
    
    @Query("UPDATE compliance_logs SET alertSent = 1, alertSentTo = :sentTo, alertLevel = :level WHERE id = :id")
    suspend fun markAlertSent(id: Long, sentTo: String, level: String)
}

// Made with Bob
