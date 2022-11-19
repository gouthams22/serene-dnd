package io.github.gouthams22.crescentdnd.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.firebase.auth.FirebaseAuth
import io.github.gouthams22.crescentdnd.R

class MainActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private val logTag = "MainActivity"
    private val handleBack =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            Log.d(logTag, "MainActivity: Inside Activity Result is " + result.resultCode.toString())
            Log.d(logTag, "Default value of Result Ok is " + Activity.RESULT_OK.toString())
            if (result.resultCode == Activity.RESULT_OK) {
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        // Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance()

        val progressIndicator: LinearProgressIndicator = findViewById(R.id.progress_get_start)
        progressIndicator.visibility = View.VISIBLE

        //check if User is logged in
        if (isUserLoggedIn()) {
            progressIndicator.visibility = View.INVISIBLE
            startActivity(Intent(this, HomeActivity::class.java))
        } else {
            progressIndicator.visibility = View.INVISIBLE

            //set onclick to login/register page if no user present
            findViewById<MaterialButton>(R.id.btn_get_started).setOnClickListener {
                it.isEnabled = false
                progressIndicator.visibility = View.VISIBLE

                handleBack.launch(Intent(this, LoginRegisterActivity::class.java))

                progressIndicator.visibility = View.INVISIBLE
                it.isEnabled = true
            }
        }
    }

    private fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }
}