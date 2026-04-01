package com.anc.ruralhealth.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.anc.ruralhealth.data.entity.ReminderEntity
import java.util.Date

/**
 * Data Access Object for Reminder operations
 */
@Dao
interface ReminderDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: ReminderEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(reminders: List<ReminderEntity>)
    
    @Update
    suspend fun update(reminder: ReminderEntity)
    
    @Delete
    suspend fun delete(reminder: ReminderEntity)
    
    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun getById(id: Long): ReminderEntity?
    
    @Query("SELECT * FROM reminders WHERE visitId = :visitId ORDER BY scheduledTime ASC")
    fun getRemindersByVisit(visitId: Long): LiveData<List<ReminderEntity>>
    
    @Query("SELECT * FROM reminders WHERE scheduledTime <= :currentTime AND isSent = 0 ORDER BY scheduledTime ASC")
    suspend fun getPendingReminders(currentTime: Date): List<ReminderEntity>
    
    @Query("UPDATE reminders SET isSent = 1, sentTime = :sentTime, deliveryStatus = :status WHERE id = :id")
    suspend fun markAsSent(id: Long, sentTime: Date, status: String)
    
    @Query("DELETE FROM reminders WHERE visitId = :visitId")
    suspend fun deleteRemindersByVisit(visitId: Long)
}


