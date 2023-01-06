package io.github.gouthams22.serenednd.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import io.github.gouthams22.serenednd.R

class ForgotPasswordActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "ForgotPasswordActivity"
    }

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        // Resizing window to fit on system window size
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Retrieving the Firebase Auth instance
        firebaseAuth = FirebaseAuth.getInstance()

        val forgotEmailEditText: TextInputEditText = findViewById(R.id.forgot_email_field)
        val forgotPasswordButton: MaterialButton = findViewById(R.id.forgot_password_button)

        forgotPasswordButton.setOnClickListener {
            // Locking email field and button to not be able to interact
            disableInput()
            if (isEmailValid(forgotEmailEditText.text?.trim().toString())) {
                Log.d(TAG, "isEmailValid: true")
                firebaseAuth.sendPasswordResetEmail(forgotEmailEditText.text?.trim().toString())
                    .addOnCompleteListener { task ->
                        Log.d(
                            TAG,
                            "FirebaseAuth password reset task: " + task.isSuccessful.toString()
                        )

                        // Releasing email field and button to be able to interact
                        enableInput()

                        // Displaying if task is successful
                        Toast.makeText(
                            applicationContext,
                            if (task.isSuccessful) "Email sent to the registered mail successfully" else "Couldn't send reset password link to the above email",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Close activity if reset link is sent to mail
                        if (task.isSuccessful)
                            finish()
                    }
            } else {
                Toast.makeText(applicationContext, "Invalid email format", Toast.LENGTH_SHORT)
                    .show()
                enableInput()
            }
        }
    }

    private fun enableInput() {
        // Progress Indicator invisible
        findViewById<LinearProgressIndicator>(R.id.forgot_progress).visibility = View.INVISIBLE

        findViewById<TextInputEditText>(R.id.forgot_email_field).isEnabled = true
        findViewById<MaterialButton>(R.id.forgot_password_button).isEnabled = true
    }

    private fun disableInput() {
        // Progress Indicator visible
        findViewById<LinearProgressIndicator>(R.id.forgot_progress).visibility = View.VISIBLE

        findViewById<TextInputEditText>(R.id.forgot_email_field).isEnabled = false
        findViewById<MaterialButton>(R.id.forgot_password_button).isEnabled = false
    }

    /**
     * [isEmailValid] is used to validate Email format
     * @param email Email address to be validated
     * @return Boolean value of validation of Email format
     */
    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}