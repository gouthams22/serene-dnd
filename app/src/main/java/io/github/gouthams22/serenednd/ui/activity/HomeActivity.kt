package io.github.gouthams22.serenednd.ui.activity

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import io.github.gouthams22.serenednd.R
import io.github.gouthams22.serenednd.preferences.DNDPreference
import io.github.gouthams22.serenednd.preferences.SettingsPreferences
import io.github.gouthams22.serenednd.ui.fragment.HomeFragment
import io.github.gouthams22.serenednd.ui.fragment.LocationFragment
import io.github.gouthams22.serenednd.ui.fragment.PriorityFragment
import kotlinx.coroutines.launch

/*
Notes:

        //Surface color with right elevation matches the bottom navigation color with the system navigation
        window.navigationBarColor= SurfaceColors.getColorForElevation(this,bottomNavigationView.elevation)
        window.navigationBarColor= SurfaceColors.getColorForElevation(this,7f)
        window.navigationBarColor= SurfaceColors.SURFACE_2.getColor(this)
 */
class HomeActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    // ActivityResultContracts for Opening Intent to DND Access Settings
    private val requestDNDSettingsActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        // Calls Location permissions after DND permission call
        requestLocationPermission()
    }

//    // ActivityResultContracts for Opening Intent to Background Location Access Settings
//    private val requestLocationSettingsActivityResultLauncher = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult()
//    ) { _ -> }

    private val requestLocationSettingsActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { result ->

    }

    private val locationPermissionResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val dndPreference = DNDPreference(this)
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // Precise location access granted.
                    lifecycleScope.launch {
                        dndPreference.storeLocationStatusPreference(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                }

                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // TODO: block location feature
                    lifecycleScope.launch {
                        val prevStatus = dndPreference.getLocationStatus()
                        if (prevStatus.isBlank()) {
                            Snackbar.make(
                                findViewById(R.id.fragment_container_view_home),
                                getString(R.string.requires_precision_msg),
                                Snackbar.LENGTH_SHORT
                            ).show()
                            dndPreference.storeLocationStatusPreference(Manifest.permission.ACCESS_COARSE_LOCATION)
                        } else if (prevStatus == Manifest.permission.ACCESS_COARSE_LOCATION) {
                            Snackbar.make(
                                findViewById(R.id.fragment_container_view_home),
                                getString(R.string.location_denied_msg),
                                Snackbar.LENGTH_SHORT
                            ).show()
                            dndPreference.storeLocationStatusPreference("None")
                        }
                    }
                }

                else -> {
                    // No location access granted.
                    // TODO: block location feature
                    Snackbar.make(
                        findViewById(R.id.fragment_container_view_home),
                        getString(R.string.location_denied_msg),
                        Snackbar.LENGTH_LONG
                    ).show()
                    lifecycleScope.launch {
                        dndPreference.storeLocationStatusPreference("None")
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Resizing window to fit on system window size
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Top app bar Menu layout
        val materialToolbar: MaterialToolbar = findViewById(R.id.toolbar_home)
        materialToolbar.inflateMenu(R.menu.home_menu)
        // Log out Menu Button
        materialToolbar.menu.findItem(R.id.logout_menu_item).setOnMenuItemClickListener {
            it.isEnabled = false
            firebaseAuth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            it.isEnabled = true
            true
        }
        // Settings Menu Button
        materialToolbar.menu.findItem(R.id.settings_menu_item).setOnMenuItemClickListener {
            it.isEnabled = false
            startActivity(Intent(applicationContext, SettingsActivity::class.java))
            it.isEnabled = true
            true
        }
        //About Menu Button
        materialToolbar.menu.findItem(R.id.about_menu_item).setOnMenuItemClickListener {
            it.isEnabled = false
            startActivity(Intent(applicationContext, AboutActivity::class.java))
            it.isEnabled = true
            true
        }

        // Get Firebase Auth instance
        firebaseAuth = FirebaseAuth.getInstance()

        // Check and redirect if no user is present
        redirectIfNoUser()

        // Bottom Navigation
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.home_navbar)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container_view_home, HomeFragment.newInstance())
                        .commit()
                    true
                }

                R.id.priority -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container_view_home, PriorityFragment.newInstance())
                        .commit()
                    true
                }

                R.id.location -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container_view_home, LocationFragment.newInstance())
                        .commit()
                    true
                }

                else -> false

            }
        }
        // Setting default view when activity is opened
        if (savedInstanceState == null)
            bottomNavigationView.selectedItemId = R.id.home

        // App Night mode
        setNightMode()

        // LocationStatus Preference live update
        val dndPreference = DNDPreference(this)
        dndPreference.locationStatusPreference.asLiveData().observe(this) { status ->
            if (status.isBlank() || status == Manifest.permission.ACCESS_COARSE_LOCATION) {
                requestLocationPermission()
            } else if (status == Manifest.permission.ACCESS_FINE_LOCATION && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                requestBackgroundPermission()
            }
        }

    }

    override fun onResume() {
        super.onResume()
        // If no user present, return to get started page
        redirectIfNoUser()
        requestRequiredPermission()
    }

    private fun requestRequiredPermission() {
        when {
            // DND permission
            !(getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).isNotificationPolicyAccessGranted -> requestDNDPermission()
        }
    }

    private fun requestDNDPermission() {
        val alertDialog = MaterialAlertDialogBuilder(this).apply {
            setTitle(getString(R.string.dnd_permission_title))
            setMessage(getString(R.string.dnd_permission_message))
            setPositiveButton(getString(R.string.permission_give_access)) { dialog, _ ->
                // above parameter(dialog,id)
                // User clicked OK button
                dialog.dismiss()
                openDNDSettings()
            }
            setNegativeButton(getString(R.string.no)) { dialog, _ ->
                // above parameter(dialog,id)
                // User clicked No
                dialog.dismiss()
                finish()
            }
            setCancelable(false)
        }
            //Create Alert Dialog before displaying
            .create()

        // Displaying alert dialog
        alertDialog.show()
    }

    private fun requestLocationPermission() {

        if ((getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).isNotificationPolicyAccessGranted
            && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission_group.LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionResultLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun requestBackgroundPermission() {
        val dndPreference = DNDPreference(this)
        val alertDialog = MaterialAlertDialogBuilder(this).apply {
            setTitle("Background Location")
            setMessage("To use the location based DND we require you to access background location to provide you timely DND updates. You can still use Serene with limited features.\nSelect \"${packageManager.backgroundPermissionOptionLabel}\" in the settings to provide access.")
            setNegativeButton(getString(R.string.no)) { dialog, _ ->
                Snackbar.make(
                    findViewById(R.id.fragment_container_view_home),
                    getString(R.string.location_denied_msg),
                    Snackbar.LENGTH_LONG
                ).show()
                lifecycleScope.launch {
                    dndPreference.storeLocationStatusPreference("None")
                }
                dialog.dismiss()
            }
            setPositiveButton("Open Settings") { dialog, _ ->
                requestLocationSettingsActivityResultLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                dialog.dismiss()
            }
        }
            .create()
        alertDialog.show()
    }

    private fun openDNDSettings() {
        // Launch intent to DND access settings
        requestDNDSettingsActivityResultLauncher.launch(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS))
    }

    private fun redirectIfNoUser() {
        if (firebaseAuth.currentUser == null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun setNightMode() {
        val settingsPreferences = SettingsPreferences(lifecycleScope, applicationContext)
        lifecycleScope
            .launch {
                AppCompatDelegate.setDefaultNightMode(settingsPreferences.getTheme().toInt())
            }
            .invokeOnCompletion {
            }
    }
}