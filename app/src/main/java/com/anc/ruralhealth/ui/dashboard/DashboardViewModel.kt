package com.anc.ruralhealth.ui.dashboard

import android.util.Log
import androidx.lifecycle.*
import com.anc.ruralhealth.data.database.AppDatabase
import com.anc.ruralhealth.data.entity.ANCVisitEntity
import com.anc.ruralhealth.data.entity.PregnancyEntity
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

    // Store LiveData observers
    private var highRiskObserver: Observer<List<PregnancyEntity>>? = null
    private var upcomingVisitsObserver: Observer<List<ANCVisitEntity>>? = null
    private var missedVisitsObserver: Observer<List<ANCVisitEntity>>? = null

    /**
     * Load dashboard metrics
     */
    fun loadDashboardMetrics(district: String? = null) {
        viewModelScope.launch {
            Log.d(TAG, "Loading dashboard metrics")

            // Total active pregnancies
            val total = if (district != null) {
                pregnancyDao.getActivePregnancyCountByDistrict(district)
            } else {
                pregnancyDao.getActivePregnancyCount()
            }
            _totalPregnancies.postValue(total)
            Log.d(TAG, "Total pregnancies: $total")

            // High risk pregnancies - observe LiveData
            val highRiskLiveData = pregnancyDao.getHighRiskPregnancies()
            highRiskObserver?.let { highRiskLiveData.removeObserver(it) }
            highRiskObserver = Observer { pregnancies ->
                val count = pregnancies.size
                _highRiskCount.postValue(count)
                Log.d(TAG, "High risk count: $count")
            }
            highRiskLiveData.observeForever(highRiskObserver!!)

            // Upcoming visits (next 7 days) - observe LiveData
            val today = Date()
            val nextWeek = Date(today.time + 7 * 24 * 60 * 60 * 1000)
            val upcomingLiveData = ancVisitDao.getUpcomingVisits(today, nextWeek)
            upcomingVisitsObserver?.let { upcomingLiveData.removeObserver(it) }
            upcomingVisitsObserver = Observer { visits ->
                val count = visits.size
                _upcomingVisitsCount.postValue(count)
                Log.d(TAG, "Upcoming visits count: $count")

                // Recalculate compliance when visits change
                calculateCompliance(total, visits.size)
            }
            upcomingLiveData.observeForever(upcomingVisitsObserver!!)

            // Missed visits - observe LiveData
            val missedLiveData = ancVisitDao.getMissedVisits(today)
            missedVisitsObserver?.let { missedLiveData.removeObserver(it) }
            missedVisitsObserver = Observer { visits ->
                val count = visits.size
                _missedVisitsCount.postValue(count)
                Log.d(TAG, "Missed visits count: $count")

                // Recalculate compliance when missed visits change
                calculateCompliance(total, count)
            }
            missedLiveData.observeForever(missedVisitsObserver!!)
        }
    }

    private fun calculateCompliance(totalPregnancies: Int, missedCount: Int) {
        // Calculate compliance rate
        val totalVisits = totalPregnancies * 4 // 4 ANC visits per pregnancy
        val completedVisits = totalVisits - missedCount
        val compliance = if (totalVisits > 0) {
            (completedVisits.toFloat() / totalVisits.toFloat()) * 100
        } else {
            0f
        }
        _complianceRate.postValue(compliance)
        Log.d(TAG, "Compliance rate: $compliance%")
    }

    override fun onCleared() {
        super.onCleared()
        // Clean up observers
        highRiskObserver?.let { pregnancyDao.getHighRiskPregnancies().removeObserver(it) }
        val today = Date()
        val nextWeek = Date(today.time + 7 * 24 * 60 * 60 * 1000)
        upcomingVisitsObserver?.let { ancVisitDao.getUpcomingVisits(today, nextWeek).removeObserver(it) }
        missedVisitsObserver?.let { ancVisitDao.getMissedVisits(today).removeObserver(it) }
    }

    companion object {
        private const val TAG = "ANC_DashboardViewModel"
    }
}


