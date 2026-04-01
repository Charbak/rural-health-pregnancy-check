package com.anc.ruralhealth.ui.register

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.anc.ruralhealth.data.database.AppDatabase
import com.anc.ruralhealth.repository.PregnancyRepository
import kotlinx.coroutines.launch
import java.util.*

/**
 * ViewModel for pregnancy registration
 */
class RegisterPregnancyViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: PregnancyRepository
    
    private val _registrationResult = MutableLiveData<Result<Long>>()
    val registrationResult: LiveData<Result<Long>> = _registrationResult
    
    init {
        val database = AppDatabase.getDatabase(application)
        repository = PregnancyRepository(
            pregnancyDao = database.pregnancyDao(),
            ancVisitDao = database.ancVisitDao(),
            reminderDao = database.reminderDao(),
            userDao = database.userDao(),
            riskFactors = database.riskFactorDao()
        )
    }
    
    fun registerPregnancy(
        patientName: String,
        patientId: String,
        lmpDate: Date,
        phoneNumber: String,
        address: String,
        age: Int,
        hemoglobin: Double?
    ) {
        viewModelScope.launch {
            try {
                val pregnancyId = repository.registerPregnancy(
                    patientName = patientName,
                    patientId = patientId,
                    lmpDate = lmpDate,
                    phoneNumber = phoneNumber,
                    address = address,
                    age = age,
                    hemoglobin = hemoglobin
                )
                _registrationResult.value = Result.success(pregnancyId)
            } catch (e: Exception) {
                _registrationResult.value = Result.failure(e)
            }
        }
    }
}

// Made with Bob