package com.subhashana.spendwise

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import com.subhashana.spendwise.data.TransactionManager
import com.subhashana.spendwise.fragments.HomeFragment
import com.subhashana.spendwise.fragments.SettingsFragment
import com.subhashana.spendwise.fragments.TransactionsFragment
import com.subhashana.spendwise.notification.NotificationManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var transactionManager: TransactionManager
    private lateinit var notificationManager: NotificationManager
    private lateinit var bottomNavigation: BottomNavigationView

    companion object {
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Configure window to handle system bars
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = getColor(R.color.primary)
        
        try {
            setContentView(R.layout.activity_main)
            initializeComponents()
            if (savedInstanceState == null) {
                loadFragment(HomeFragment())
            }
            checkNotificationPermission()
        } catch (e: Exception) {
            Log.e("MainActivity", "Error initializing app", e)
            Toast.makeText(this, "Error initializing app: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
            finish()
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, notifications will work
                Log.d("MainActivity", "Notification permission granted")
            } else {
                // Permission denied, show a message
                Toast.makeText(
                    this,
                    "Please enable notifications to receive budget alerts",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun initializeComponents() {
        try {
            // Initialize managers first
            initializeManagers()
            
            // Then initialize views
            initializeViews()
            
            // Setup components
            setupBottomNavigation()
        } catch (e: Exception) {
            Log.e("MainActivity", "Error in initializeComponents", e)
            throw e
        }
    }

    private fun initializeManagers() {
        try {
            transactionManager = TransactionManager(applicationContext)
            notificationManager = NotificationManager(applicationContext)
        } catch (e: Exception) {
            Log.e("MainActivity", "Error initializing managers", e)
            throw Exception("Error initializing managers: ${e.message}")
        }
    }

    private fun initializeViews() {
        try {
            bottomNavigation = findViewById(R.id.bottom_navigation)
            
            if (!::bottomNavigation.isInitialized) {
                throw Exception("Failed to initialize views")
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error initializing views", e)
            throw Exception("Error initializing views: ${e.message}")
        }
    }

    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.navigation_transactions -> {
                    loadFragment(TransactionsFragment())
                    true
                }
                R.id.navigation_settings -> {
                    loadFragment(SettingsFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }

    fun getTransactionManager(): TransactionManager {
        return transactionManager
    }

    fun updateUI() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        when (currentFragment) {
            is HomeFragment -> currentFragment.onResume()
            is TransactionsFragment -> currentFragment.onResume()
        }
    }
}