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
            reminderDao = database.reminderDao()
        )
    }
    
    fun registerPregnancy(
        patientName: String,
        patientId: String,
        lmpDate: Date,
        phoneNumber: String,
        state: String,
        district: String,
        village: String,
        pincode: String,
        address: String,
        age: Int,
        hemoglobin: Double?
    ) {
        viewModelScope.launch {
            try {
                android.util.Log.d("RegisterPregnancy", "Starting registration for: $patientName")
                android.util.Log.d("RegisterPregnancy", "Location: $village, $district, $state - $pincode")
                android.util.Log.d("RegisterPregnancy", "LMP Date: $lmpDate")
                android.util.Log.d("RegisterPregnancy", "Age: $age, Phone: $phoneNumber")
                
                // Combine full address
                val fullAddress = "$address, $village, $district, $state - $pincode"
                
                val pregnancyId = repository.registerPregnancy(
                    womanName = patientName,
                    age = age,
                    mobileNumber = phoneNumber,
                    address = fullAddress,
                    village = village,
                    district = district,
                    state = state,
                    lmp = lmpDate,
                    edd = null, // Will be calculated from LMP
                    registeredBy = "Provider", // Default value - can be enhanced with actual user
                    hemoglobin = hemoglobin?.toFloat()
                )
                
                android.util.Log.d("RegisterPregnancy", "Registration successful! ID: $pregnancyId")
                _registrationResult.value = Result.success(pregnancyId)
            } catch (e: Exception) {
                android.util.Log.e("RegisterPregnancy", "Registration failed", e)
                android.util.Log.e("RegisterPregnancy", "Error message: ${e.message}")
                android.util.Log.e("RegisterPregnancy", "Error cause: ${e.cause}")
                e.printStackTrace()
                _registrationResult.value = Result.failure(e)
            }
        }
    }
}

// Made with Bob