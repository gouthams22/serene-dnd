package io.github.gouthams22.crescentdnd.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import io.github.gouthams22.crescentdnd.R

class MainActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        // Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance()

        //check if User is logged in

        if (isUserLoggedIn()) {
            startActivity(Intent(this, HomeActivity::class.java))
        } else {
            //set onclick to login/register page if no user present
            findViewById<MaterialButton>(R.id.btn_get_started).setOnClickListener {
                it.isEnabled = false
                startActivity(Intent(this, LoginRegisterActivity::class.java))
                it.isEnabled = true
            }
        }
    }

    private fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }
}