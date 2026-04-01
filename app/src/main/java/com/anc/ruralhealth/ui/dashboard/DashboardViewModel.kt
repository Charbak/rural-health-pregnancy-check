package com.anc.ruralhealth.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anc.ruralhealth.data.database.AppDatabase
import kotlinx.coroutines.launch
import java.util.Date

/**
 * ViewModel for Dashboard
 * Provides metrics and statistics for providers and district administrators
 */
class DashboardViewModel(private val database: AppDatabase) : ViewModel() {
    
    private val pregnancyDao = database.pregnancyDao()
    private val ancVisitDao = database.ancVisitDao()
    
    private val _totalPregnancies = MutableLiveData<Int>()
    val totalPregnancies: LiveData<Int> = _totalPregnancies
    
    private val _highRiskCount = MutableLiveData<Int>()
    val highRiskCount: LiveData<Int> = _highRiskCount
    
    private val _upcomingVisitsCount = MutableLiveData<Int>()
    val upcomingVisitsCount: LiveData<Int> = _upcomingVisitsCount
    
    private val _missedVisitsCount = MutableLiveData<Int>()
    val missedVisitsCount: LiveData<Int> = _missedVisitsCount
    
    private val _complianceRate = MutableLiveData<Float>()
    val complianceRate: LiveData<Float> = _complianceRate
    
    /**
     * Load dashboard metrics
     */
    fun loadDashboardMetrics(district: String? = null) {
        viewModelScope.launch {
            // Total active pregnancies
            val total = if (district != null) {
                pregnancyDao.getActivePregnancyCountByDistrict(district)
            } else {
                pregnancyDao.getActivePregnancyCount()
            }
            _totalPregnancies.postValue(total)
            
            // High risk pregnancies
            val highRisk = pregnancyDao.getHighRiskPregnancies().value?.size ?: 0
            _highRiskCount.postValue(highRisk)
            
            // Upcoming visits (next 7 days)
            val today = Date()
            val nextWeek = Date(today.time + 7 * 24 * 60 * 60 * 1000)
            val upcoming = ancVisitDao.getUpcomingVisits(today, nextWeek).value?.size ?: 0
            _upcomingVisitsCount.postValue(upcoming)
            
            // Missed visits
            val missed = ancVisitDao.getMissedVisits(today).value?.size ?: 0
            _missedVisitsCount.postValue(missed)
            
            // Calculate compliance rate
            val totalVisits = total * 4 // 4 ANC visits per pregnancy
            val completedVisits = totalVisits - missed
            val compliance = if (totalVisits > 0) {
                (completedVisits.toFloat() / totalVisits.toFloat()) * 100
            } else {
                0f
            }
            _complianceRate.postValue(compliance)
        }
    }
}

// Made with Bob
