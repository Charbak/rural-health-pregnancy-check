package com.anc.ruralhealth.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.anc.ruralhealth.data.dao.*
import com.anc.ruralhealth.data.entity.*

/**
 * Main database for ANC Rural Health application
 * Provides offline capability with Room persistence library
 */
@Database(
    entities = [
        PregnancyEntity::class,
        ANCVisitEntity::class,
        ReminderEntity::class,
        UserEntity::class,
        ComplianceLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun pregnancyDao(): PregnancyDao
    abstract fun ancVisitDao(): ANCVisitDao
    abstract fun reminderDao(): ReminderDao
    abstract fun userDao(): UserDao
    abstract fun complianceLogDao(): ComplianceLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "anc_rural_health_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// Made with Bob
