package io.github.gouthams22.crescentdnd.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import io.github.gouthams22.crescentdnd.R
import io.github.gouthams22.crescentdnd.ui.activity.LoginRegisterActivity

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterFragment : Fragment() {

    // Tag
    private val logTag = "RegisterFragment"

    // Firebase Authentication
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        val registerProgressIndicator: LinearProgressIndicator =
            view.findViewById(R.id.register_progress)

        val regEmailField: TextInputEditText = view.findViewById(R.id.reg_email_field)
        val regPasswordField: TextInputEditText = view.findViewById(R.id.reg_password_field)
        val regConfirmPasswordField: TextInputEditText =
            view.findViewById(R.id.reg_confirm_password_field)

        val registerButton: MaterialButton = view.findViewById(R.id.register_button)

        // Initialize Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance()

        //Checking if "Confirm password" and "password" are equal
        regConfirmPasswordField.doOnTextChanged { text, start, before, count ->
            regConfirmPasswordField.error =
                if (text.toString() == regPasswordField.text.toString()) null else "Passwords doesn't match"
        }

        //On clicking register button
        registerButton.setOnClickListener {
            disableInput(view)
            registerProgressIndicator.visibility = View.VISIBLE
            if (validateFields(regEmailField, regPasswordField, regConfirmPasswordField)) {
                Log.d(logTag, "Username and Password field is in valid format")
                registerAccount(
                    view,
                    regEmailField.text?.trim().toString(),
                    regPasswordField.text?.trim().toString()
                )
            } else {
                Log.d(logTag, "Username and Password field is in invalid format")
                enableInput(view)
                registerProgressIndicator.visibility = View.INVISIBLE
            }

        }

        //Select 'Login' tab if user has account
        view.findViewById<MaterialTextView>(R.id.have_account).setOnClickListener {
            it.isEnabled = false
            (view.context as LoginRegisterActivity).haveAccount()
            it.isEnabled = true
        }
        return view
    }

    private fun registerAccount(view: View, email: String, password: String) {

        val registerProgressIndicator: LinearProgressIndicator =
            view.findViewById(R.id.register_progress)

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            enableInput(view)
            registerProgressIndicator.visibility = View.INVISIBLE
            if (task.isSuccessful) {
                Log.d(logTag, "Task successful" + task.result.user?.email)
                task.result.user?.sendEmailVerification()
                    ?.addOnCompleteListener { verificationTask ->
                        if (verificationTask.isSuccessful) {
                            Toast.makeText(
                                view.context,
                                "Registered successfully. Verification is sent to the registered email",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                view.context,
                                "Registered successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                if (firebaseAuth.currentUser != null) {
                    firebaseAuth.signOut()
                }
            } else if (task.isCanceled) {
                Log.d(logTag, "Create user task is cancelled")
                Toast.makeText(view.context, "Process is cancelled", Toast.LENGTH_SHORT).show()
            } else {
                Log.d(logTag, "Create user task failed")
                Toast.makeText(view.context, "Registration failed!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * [validateFields] is used to check the validation of email id, password and confirm password.
     * @param emailField Email ID
     * @param passwordField Password Field
     * @param confirmPasswordField Confirm Password Field
     * @return Boolean value true if validation check is passed otherwise false
     */
    private fun validateFields(
        emailField: TextInputEditText,
        passwordField: TextInputEditText,
        confirmPasswordField: TextInputEditText
    ): Boolean {
        val email: String = emailField.text?.trim().toString()
        val password: String = passwordField.text?.trim().toString()
        val confirmPassword: String = confirmPasswordField.text?.trim().toString()

        //Validation for email
        if (email.isEmpty()) {
            emailField.error = "Empty field"
            return false
        } else if (!isEmailValid(email)) {
            emailField.error = "Incorrect email format"
            return false
        }

        //Validation for password
        if (password.isEmpty()) {
            passwordField.error = "Empty field"
            return false
        } else if (password.length < 6 || password.length > 20) {
            passwordField.error = "Password should be within 6-20 characters in length"
            return false
        } else if (!password.matches(Regex("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}\$"))) {
            passwordField.error =
                "Password should be alphanumeric(including at least a capital letter and a special character)"
            return false
        } else if (confirmPassword != password)
            return false
        return true
    }

    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun disableInput(view: View) {
        view.findViewById<MaterialButton>(R.id.register_button).isEnabled = false
        view.findViewById<TextInputEditText>(R.id.reg_email_field).isEnabled = false
        view.findViewById<TextInputEditText>(R.id.reg_password_field).isEnabled = false
        view.findViewById<TextInputEditText>(R.id.reg_confirm_password_field).isEnabled = false
    }

    private fun enableInput(view: View) {
        view.findViewById<MaterialButton>(R.id.register_button).isEnabled = true
        view.findViewById<TextInputEditText>(R.id.reg_email_field).isEnabled = true
        view.findViewById<TextInputEditText>(R.id.reg_password_field).isEnabled = true
        view.findViewById<TextInputEditText>(R.id.reg_confirm_password_field).isEnabled = true
    }


    companion object {
        @JvmStatic
        fun newInstance() = RegisterFragment()
    }
}