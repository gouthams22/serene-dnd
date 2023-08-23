package io.github.gouthams22.serenednd.ui.activity

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import io.github.gouthams22.serenednd.R
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
    }

    override fun onResume() {
        super.onResume()
        // If no user present, return to get started page
        redirectIfNoUser()
        requestRequiredPermission()

    }

    private fun checkPermission(): Boolean {
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return notificationManager.isNotificationPolicyAccessGranted
    }

    private fun requestRequiredPermission() {
        if (!checkPermission())
            requestDNDPermission()
    }

    private fun requestDNDPermission() {
        val alertDialog: AlertDialog = let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setTitle("This application requires DND access to provide you appropriate services.")
                setPositiveButton(
                    "Give DND Access"
                ) { dialog, _ ->
                    // above parameter(dialog,id)
                    // User clicked OK button
                    dialog.dismiss()
                    openDNDSettings()
                }
                setNegativeButton("No") { dialog, _ ->
                    // above parameter(dialog,id)
                    // User clicked No
                    dialog.dismiss()
                    finish()
                }
                setCancelable(false)
            }
            // Create the AlertDialog
            builder.create()
        }

        // Displaying alert dialog
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