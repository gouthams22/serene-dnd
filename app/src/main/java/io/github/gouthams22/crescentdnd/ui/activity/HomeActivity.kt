package io.github.gouthams22.crescentdnd.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import io.github.gouthams22.crescentdnd.R

class HomeActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private val logTag = "HomeActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        firebaseAuth = FirebaseAuth.getInstance()
        redirectIfNoUser()
        val isVerified = firebaseAuth.currentUser?.isEmailVerified
        findViewById<MaterialTextView>(R.id.user_details).text = isVerified.toString()
        findViewById<MaterialButton>(R.id.logout_button).setOnClickListener {
            firebaseAuth.signOut()
            Log.d(logTag, if (firebaseAuth.currentUser != null) "Still signed in" else "Nope")
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
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