package com.anc.ruralhealth.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.anc.ruralhealth.data.entity.ANCVisitEntity
import java.util.Date

/**
 * Data Access Object for ANC Visit operations
 */
@Dao
interface ANCVisitDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(visit: ANCVisitEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(visits: List<ANCVisitEntity>): List<Long>
    
    @Update
    suspend fun update(visit: ANCVisitEntity)
    
    @Delete
    suspend fun delete(visit: ANCVisitEntity)
    
    @Query("SELECT * FROM anc_visits WHERE id = :id")
    suspend fun getById(id: Long): ANCVisitEntity?
    
    @Query("SELECT * FROM anc_visits WHERE pregnancyId = :pregnancyId ORDER BY visitNumber ASC")
    fun getVisitsByPregnancy(pregnancyId: Long): LiveData<List<ANCVisitEntity>>
    
    @Query("SELECT * FROM anc_visits WHERE pregnancyId = :pregnancyId AND isCompleted = 0 ORDER BY scheduledDate ASC")
    fun getPendingVisitsByPregnancy(pregnancyId: Long): LiveData<List<ANCVisitEntity>>
    
    @Query("SELECT * FROM anc_visits WHERE scheduledDate BETWEEN :startDate AND :endDate AND isCompleted = 0 ORDER BY scheduledDate ASC")
    fun getUpcomingVisits(startDate: Date, endDate: Date): LiveData<List<ANCVisitEntity>>
    
    @Query("SELECT * FROM anc_visits WHERE scheduledDate < :currentDate AND isCompleted = 0 ORDER BY scheduledDate ASC")
    fun getMissedVisits(currentDate: Date): LiveData<List<ANCVisitEntity>>
    
    @Query("SELECT COUNT(*) FROM anc_visits WHERE pregnancyId = :pregnancyId AND isCompleted = 1")
    suspend fun getCompletedVisitCount(pregnancyId: Long): Int
    
    @Query("SELECT COUNT(*) FROM anc_visits WHERE pregnancyId = :pregnancyId AND scheduledDate < :currentDate AND isCompleted = 0")
    suspend fun getMissedVisitCount(pregnancyId: Long, currentDate: Date): Int
    
    @Query("UPDATE anc_visits SET isCompleted = 1, completedDate = :completedDate, completedBy = :completedBy, actualGestationalWeek = :gestationalWeek WHERE id = :id")
    suspend fun markAsCompleted(id: Long, completedDate: Date, completedBy: String, gestationalWeek: Int)
    
    @Query("UPDATE anc_visits SET reminderSent7Days = 1 WHERE id = :id")
    suspend fun markReminder7DaysSent(id: Long)
    
    @Query("UPDATE anc_visits SET reminderSent2Days = 1 WHERE id = :id")
    suspend fun markReminder2DaysSent(id: Long)
    
    @Query("UPDATE anc_visits SET missedVisitAlertSent = 1 WHERE id = :id")
    suspend fun markMissedVisitAlertSent(id: Long)
    
    @Query("SELECT * FROM anc_visits WHERE isSynced = 0")
    suspend fun getUnsyncedVisits(): List<ANCVisitEntity>
    
    @Query("UPDATE anc_visits SET isSynced = 1, lastSyncDate = :syncDate WHERE id = :id")
    suspend fun markAsSynced(id: Long, syncDate: Date)
}
