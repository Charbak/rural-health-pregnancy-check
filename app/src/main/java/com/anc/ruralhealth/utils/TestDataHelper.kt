package com.anc.ruralhealth.utils

import android.content.Context
import android.util.Log
import com.anc.ruralhealth.data.database.AppDatabase
import com.anc.ruralhealth.repository.PregnancyRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

/**
 * Helper utility for creating test data
 * Creates pregnancies with near-term ANC visits for immediate testing
 */
object TestDataHelper {

    private const val TAG = "ANC_TestDataHelper"

    /**
     * Create test pregnancy with ANC1 visit scheduled in 2-3 days
     * This ensures reminders (7-day and 2-day) trigger immediately for testing
     *
     * Logic: ANC1 is at week 12, so we calculate LMP that makes week 12 happen in 2-3 days
     */
    suspend fun createTestPregnancy(context: Context): Result<Long> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Creating test pregnancy with near-term ANC1 visit")

                val database = AppDatabase.getDatabase(context)
                val repository = PregnancyRepository(
                    pregnancyDao = database.pregnancyDao(),
                    ancVisitDao = database.ancVisitDao(),
                    reminderDao = database.reminderDao()
                )

                // Calculate LMP for ANC1 to be in 3 days
                // ANC1 is scheduled at week 12 (84 days from LMP)
                // So LMP should be 81 days ago (84 - 3 = 81)
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_YEAR, -81)
                val lmp = calendar.time

                Log.d(TAG, "Calculated LMP: $lmp")
                Log.d(TAG, "This makes ANC1 (week 12) scheduled in approximately 3 days")

                // Create test pregnancy
                val pregnancyId = repository.registerPregnancy(
                    womanName = "Test Patient ${System.currentTimeMillis() % 1000}",
                    age = 25,
                    mobileNumber = "9999999999",
                    address = "Test Address, Test Village",
                    village = "Test Village",
                    district = "TEST",
                    state = "Test State",
                    lmp = lmp,
                    edd = null, // Will be calculated
                    registeredBy = "test_provider_001",
                    hemoglobin = 11.5f
                )

                Log.d(TAG, "Test pregnancy created successfully with ID: $pregnancyId")
                Log.d(TAG, "ANC visits scheduled:")
                Log.d(TAG, "  - ANC1 (week 12): ~3 days from now")
                Log.d(TAG, "  - 7-day reminder: should trigger immediately")
                Log.d(TAG, "  - 2-day reminder: ~1 day from now")

                // Verify reminders were created
                val reminders = database.reminderDao().getPendingReminders(Date())
                Log.d(TAG, "Pending reminders created: ${reminders.size}")
                reminders.forEach { reminder ->
                    Log.d(TAG, "  - ${reminder.reminderType}: ${reminder.scheduledTime}")
                }

                Result.success(pregnancyId)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to create test pregnancy", e)
                Result.failure(e)
            }
        }
    }

    /**
     * Create test pregnancy with callback
     * Convenience method for calling from UI
     */
    fun createTestPregnancyAsync(
        context: Context,
        scope: CoroutineScope,
        onSuccess: (Long) -> Unit,
        onError: (Exception) -> Unit
    ) {
        scope.launch {
            val result = createTestPregnancy(context)
            result.fold(
                onSuccess = { pregnancyId ->
                    withContext(Dispatchers.Main) {
                        onSuccess(pregnancyId)
                    }
                },
                onFailure = { error ->
                    withContext(Dispatchers.Main) {
                        onError(error as? Exception ?: Exception(error.message))
                    }
                }
            )
        }
    }
}
