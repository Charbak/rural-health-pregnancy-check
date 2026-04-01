package com.anc.ruralhealth.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.anc.ruralhealth.data.entity.PregnancyEntity
import java.util.Date

/**
 * Data Access Object for Pregnancy operations
 */
@Dao
interface PregnancyDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pregnancy: PregnancyEntity): Long
    
    @Update
    suspend fun update(pregnancy: PregnancyEntity)
    
    @Delete
    suspend fun delete(pregnancy: PregnancyEntity)
    
    @Query("SELECT * FROM pregnancies WHERE id = :id")
    suspend fun getById(id: Long): PregnancyEntity?
    
    @Query("SELECT * FROM pregnancies WHERE pregnancyId = :pregnancyId")
    suspend fun getByPregnancyId(pregnancyId: String): PregnancyEntity?
    
    @Query("SELECT * FROM pregnancies WHERE isActive = 1 ORDER BY registrationDate DESC")
    fun getAllActivePregnancies(): LiveData<List<PregnancyEntity>>
    
    @Query("SELECT * FROM pregnancies WHERE district = :district AND isActive = 1 ORDER BY registrationDate DESC")
    fun getPregnanciesByDistrict(district: String): LiveData<List<PregnancyEntity>>
    
    @Query("SELECT * FROM pregnancies WHERE registeredBy = :userId AND isActive = 1 ORDER BY registrationDate DESC")
    fun getPregnanciesByProvider(userId: String): LiveData<List<PregnancyEntity>>
    
    @Query("SELECT * FROM pregnancies WHERE mobileNumber = :mobileNumber AND isActive = 1")
    suspend fun getByMobileNumber(mobileNumber: String): PregnancyEntity?
    
    @Query("SELECT * FROM pregnancies WHERE hasHighRisk = 1 AND isActive = 1 ORDER BY gestationalAgeWeeks DESC")
    fun getHighRiskPregnancies(): LiveData<List<PregnancyEntity>>
    
    @Query("SELECT COUNT(*) FROM pregnancies WHERE isActive = 1")
    suspend fun getActivePregnancyCount(): Int
    
    @Query("SELECT COUNT(*) FROM pregnancies WHERE district = :district AND isActive = 1")
    suspend fun getActivePregnancyCountByDistrict(district: String): Int
    
    @Query("SELECT * FROM pregnancies WHERE isSynced = 0")
    suspend fun getUnsyncedPregnancies(): List<PregnancyEntity>
    
    @Query("UPDATE pregnancies SET isSynced = 1, lastSyncDate = :syncDate WHERE id = :id")
    suspend fun markAsSynced(id: Long, syncDate: Date)
    
    @Query("UPDATE pregnancies SET isActive = 0, completionDate = :completionDate, outcome = :outcome WHERE id = :id")
    suspend fun completePregnancy(id: Long, completionDate: Date, outcome: String)
}


