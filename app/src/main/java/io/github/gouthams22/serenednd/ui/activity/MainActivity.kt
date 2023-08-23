package io.github.gouthams22.serenednd.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.firebase.auth.FirebaseAuth
import io.github.gouthams22.serenednd.R

class MainActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private val handleBack =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Resizing window to fit on system window size
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

    /**
     * checks if user is logged in
     * @return returns true if user is logged in otherwise false
     */
    private fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }
}