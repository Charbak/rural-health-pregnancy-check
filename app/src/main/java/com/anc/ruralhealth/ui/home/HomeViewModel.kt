package com.anc.ruralhealth.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.anc.ruralhealth.data.database.AppDatabase
import com.anc.ruralhealth.data.entity.ANCVisitEntity
import com.anc.ruralhealth.utils.AuthManager
import kotlinx.coroutines.launch
import java.util.Date

/**
 * ViewModel for Home Fragment
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {
    
    private val database = AppDatabase.getDatabase(application)
    private val ancVisitDao = database.ancVisitDao()
    private val authManager = AuthManager(application)
    
    private val _upcomingVisits = MutableLiveData<List<ANCVisitEntity>>()
    val upcomingVisits: LiveData<List<ANCVisitEntity>> = _upcomingVisits
    
    private val _missedVisitsCount = MutableLiveData<Int>()
    val missedVisitsCount: LiveData<Int> = _missedVisitsCount
    
    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> = _userName
    
    init {
        loadData()
    }
    
    private fun loadData() {
        viewModelScope.launch {
            // Load user name
            _userName.postValue(authManager.getUserName() ?: "User")
            
            // Load upcoming visits (next 7 days)
            val today = Date()
            val nextWeek = Date(today.time + 7 * 24 * 60 * 60 * 1000)
            ancVisitDao.getUpcomingVisits(today, nextWeek).observeForever { visits ->
                _upcomingVisits.postValue(visits)
            }
            
            // Load missed visits count
            ancVisitDao.getMissedVisits(today).observeForever { visits ->
                _missedVisitsCount.postValue(visits.size)
            }
        }
    }
    
    fun refreshData() {
        loadData()
    }
}


