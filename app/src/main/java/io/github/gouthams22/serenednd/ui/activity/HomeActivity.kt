package io.github.gouthams22.serenednd.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.elevation.SurfaceColors
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import io.github.gouthams22.serenednd.R

class HomeActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private val logTag = "HomeActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Resizing window to fit on system window size
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val color = SurfaceColors.SURFACE_1.getColor(this)
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

    override fun onResume() {
        super.onResume()
        // If no user present, return to get started page
        redirectIfNoUser()
    }

    private fun redirectIfNoUser() {
        if (firebaseAuth.currentUser == null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}