package io.github.gouthams22.serenednd.ui.activity

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import io.github.gouthams22.serenednd.R

class HomeActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private val logTag = "HomeActivity"

    // ActivityResultContracts for Opening Intent to DND Access Settings
    private val requestDNDSettingsActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d(logTag, "requestDNDPermission: $result")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Resizing window to fit on system window size
        WindowCompat.setDecorFitsSystemWindows(window, false)

//        val color = SurfaceColors.SURFACE_1.getColor(this)
//        window.statusBarColor = color

        val materialToolbar: MaterialToolbar = findViewById(R.id.home_toolbar)
//        materialToolbar.background=color.toDrawable()
        materialToolbar.inflateMenu(R.menu.home_menu)
        materialToolbar.menu.findItem(R.id.logout_menu_item).setOnMenuItemClickListener {
            firebaseAuth.signOut()
            Log.d(logTag, if (firebaseAuth.currentUser != null) "Still signed in" else "Nope")
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            true
        }

        // Get Firebase Auth instance
        firebaseAuth = FirebaseAuth.getInstance()

        // Check and redirect if no user is present
        redirectIfNoUser()
        val isVerified = firebaseAuth.currentUser?.isEmailVerified
        findViewById<MaterialTextView>(R.id.user_details).text = isVerified.toString()
//        findViewById<MaterialButton>(R.id.logout_button).setOnClickListener {
//            firebaseAuth.signOut()
//            Log.d(logTag, if (firebaseAuth.currentUser != null) "Still signed in" else "Nope")
//            startActivity(Intent(this, MainActivity::class.java))
//            finish()
//        }
    }

    private fun checkPermission(): Boolean {
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        Log.d(logTag, "checkPermission: ${notificationManager.isNotificationPolicyAccessGranted}")
        return notificationManager.isNotificationPolicyAccessGranted
    }

    private fun requestRequiredPermission() {
        if (!checkPermission()) {
//            val requestPermissionLauncher =
//                registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
//                    Log.d(logTag, "requestRequiredPermission: $isGranted")
//                }
//            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            requestDNDPermission()
        }
    }

    private fun requestDNDPermission() {
//        Log.d(logTag, "requestDNDPermission Rationale: ${shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_NOTIFICATION_POLICY)}")
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
                    Log.d(logTag, "dialogResult(): Yes")
                    openDNDSettings()
                }
                setNegativeButton("No") { dialog, _ ->
                    // above parameter(dialog,id)
                    // User clicked No
                    dialog.dismiss()
                    Log.d(logTag, "dialogResult(): No")
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

    override fun onResume() {
        super.onResume()
        // If no user present, return to get started page
        redirectIfNoUser()
        requestRequiredPermission()
    }

    private fun redirectIfNoUser() {
        if (firebaseAuth.currentUser == null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}