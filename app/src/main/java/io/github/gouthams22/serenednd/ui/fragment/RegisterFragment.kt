package io.github.gouthams22.serenednd.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import io.github.gouthams22.serenednd.R
import io.github.gouthams22.serenednd.ui.activity.LoginRegisterActivity

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterFragment : Fragment() {

    // Firebase Authentication
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        regConfirmPasswordField.doOnTextChanged { text, _, _, _ ->
            regConfirmPasswordField.error =
                if (text.toString() == regPasswordField.text.toString()) null else "Passwords doesn't match"
        }

        //On clicking register button
        registerButton.setOnClickListener {
            disableInput(view)
            registerProgressIndicator.visibility = View.VISIBLE
            if (validateFields(regEmailField, regPasswordField, regConfirmPasswordField)) {
                registerAccount(
                    view,
                    regEmailField.text?.trim().toString(),
                    regPasswordField.text?.trim().toString()
                )
            } else {
                enableInput(view)
                registerProgressIndicator.visibility = View.INVISIBLE
            }

        }

        //Select 'Login' tab if user has account
        view.findViewById<MaterialButton>(R.id.button_fragment_login).setOnClickListener {
            it.isEnabled = false
            (view.context as LoginRegisterActivity).haveAccount()
            it.isEnabled = true
        }
    }

    private fun registerAccount(view: View, email: String, password: String) {

        val registerProgressIndicator: LinearProgressIndicator =
            view.findViewById(R.id.register_progress)

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            enableInput(view)
            registerProgressIndicator.visibility = View.INVISIBLE
            if (task.isSuccessful) {
                task.result.user?.sendEmailVerification()
                    ?.addOnCompleteListener { verificationTask ->
                        if (verificationTask.isSuccessful) {
                            Snackbar.make(
                                view,
                                "Registered successfully. Verification link is sent to your registered email",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        } else {
                            Snackbar.make(view, "Registered successfully", Snackbar.LENGTH_SHORT)
                                .show()
                        }
                    }
                if (firebaseAuth.currentUser != null) {
                    firebaseAuth.signOut()
                }
            } else if (task.isCanceled) {
                Snackbar.make(view, "Process is cancelled", Snackbar.LENGTH_SHORT).show()
            } else {
                Snackbar.make(view, "Registration failed!", Snackbar.LENGTH_SHORT).show()
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

        /**
         * Use this factory method to create a new instance of
         * this fragment.
         *
         * @return A new instance of fragment RegisterFragment.
         */
        @JvmStatic
        fun newInstance() = RegisterFragment()
    }
}