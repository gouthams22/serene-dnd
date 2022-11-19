package io.github.gouthams22.crescentdnd.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import io.github.gouthams22.crescentdnd.R

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private val logTag = "ForgotPasswordActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        // Retrieving the Firebase Auth instance
        firebaseAuth = FirebaseAuth.getInstance()

        val forgotEmailEditText: TextInputEditText = findViewById(R.id.forgot_email_field)
        val forgotPasswordButton: MaterialButton = findViewById(R.id.forgot_password_button)

        forgotPasswordButton.setOnClickListener {
            // Locking email field and button to not be able to interact
            forgotPasswordButton.isEnabled = false
            forgotEmailEditText.isEnabled = false
            if (isEmailValid(forgotEmailEditText.text?.trim().toString())) {
                Log.d(logTag, "isEmailValid: true")
                firebaseAuth.sendPasswordResetEmail(forgotEmailEditText.text?.trim().toString())
                    .addOnCompleteListener { task ->
                        Log.d(
                            logTag,
                            "FirebaseAuth password reset task: " + task.isSuccessful.toString()
                        )

                        // Releasing email field and button to be able to interact
                        forgotPasswordButton.isEnabled = true
                        forgotEmailEditText.isEnabled = true

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
                forgotPasswordButton.isEnabled = true
                forgotEmailEditText.isEnabled = true
            }
        }
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