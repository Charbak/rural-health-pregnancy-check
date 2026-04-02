package com.anc.ruralhealth.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.anc.ruralhealth.R
import com.anc.ruralhealth.utils.AuthManager
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * Main Activity - Entry point for the application
 * Simple navigation with bottom navigation bar for rural users
 */
class MainActivity : AppCompatActivity() {

    private lateinit var authManager: AuthManager

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d(TAG, "Notification permission granted")
        } else {
            Log.w(TAG, "Notification permission denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        authManager = AuthManager(this)

        // Auto-login for testing if no user exists
        setupTestUser()

        // Request notification permission
        requestNotificationPermission()

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        // Setup bottom navigation with top-level destinations
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_dashboard
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    /**
     * Setup test user for development/testing
     * Auto-login as Provider if no user is logged in
     */
    private fun setupTestUser() {
        if (!authManager.isLoggedIn()) {
            Log.d(TAG, "No user logged in - creating test user")
            authManager.login(
                userId = "test_provider_001",
                role = AuthManager.ROLE_ANM,
                userName = "Test Provider",
                district = "TEST"
            )
            Log.d(TAG, "Test user logged in: ${authManager.getUserName()}, Role: ${authManager.getUserRole()}")
        } else {
            Log.d(TAG, "User already logged in: ${authManager.getUserName()}, Role: ${authManager.getUserRole()}")
        }
    }

    /**
     * Request notification permission for Android 13+
     */
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    Log.d(TAG, "Notification permission already granted")
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    // Show rationale dialog
                    AlertDialog.Builder(this)
                        .setTitle("Notification Permission Required")
                        .setMessage("This app needs notification permission to remind you about upcoming ANC visits and alert you about missed appointments.")
                        .setPositiveButton("OK") { _, _ ->
                            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                        .setNegativeButton("Cancel") { dialog, _ ->
                            Log.w(TAG, "User cancelled notification permission request")
                            dialog.dismiss()
                        }
                        .show()
                }
                else -> {
                    // Directly request permission
                    Log.d(TAG, "Requesting notification permission")
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            Log.d(TAG, "Android version < 13, notification permission not required")
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    companion object {
        private const val TAG = "ANC_MainActivity"
    }
}


