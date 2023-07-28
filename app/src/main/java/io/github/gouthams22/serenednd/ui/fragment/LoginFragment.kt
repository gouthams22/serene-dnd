package io.github.gouthams22.serenednd.ui.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import io.github.gouthams22.serenednd.R
import io.github.gouthams22.serenednd.ui.activity.AboutActivity
import io.github.gouthams22.serenednd.ui.activity.ForgotPasswordActivity
import io.github.gouthams22.serenednd.ui.activity.HomeActivity
import io.github.gouthams22.serenednd.ui.activity.LoginRegisterActivity

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var rootView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        rootView = view

        // Loading bar
        val loginProgressIndicator: LinearProgressIndicator = view.findViewById(R.id.login_progress)

        val loginEmailField: TextInputEditText = view.findViewById(R.id.login_email_field)
        val loginPasswordField: TextInputEditText = view.findViewById(R.id.login_password_field)
        val forgotPasswordTextView: MaterialButton = view.findViewById(R.id.button_forgot_password)
        val privacyLicensesButton: MaterialButton =
            view.findViewById(R.id.button_privacy_and_licenses)

        // redirecting to AboutActivity
        privacyLicensesButton.setOnClickListener {
            it.isEnabled = false
            startActivity(Intent(view.context, AboutActivity::class.java))
            it.isEnabled = true
        }

        // redirecting to ForgotPasswordActivity
        forgotPasswordTextView.setOnClickListener {
            loginProgressIndicator.visibility = View.VISIBLE
            startActivity(Intent(view.context, ForgotPasswordActivity::class.java))
            loginProgressIndicator.visibility = View.INVISIBLE
        }

        // Firebase Authentication Instance
        firebaseAuth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(view.context as LoginRegisterActivity, gso)

        // Google login button
        val googleSignInButton: SignInButton = view.findViewById(R.id.sign_in_google_button)
        googleSignInButton.setSize(SignInButton.SIZE_WIDE)
        googleSignInButton.setOnClickListener {
            signIntoGoogle()
        }

        // Email & password login button
        val loginButton: MaterialButton = view.findViewById(R.id.login_button)
        // set on click listener for Login button
        loginButton.setOnClickListener {
            disableInput(view)
            loginProgressIndicator.visibility = View.VISIBLE
            val isValid = validateFields(loginEmailField, loginPasswordField)
            Log.d(
                TAG,
                if (isValid) "Login Form fields are in valid state" else "Login Form is invalid"
            )
            if (isValid) authenticateFirebase(
                view,
                loginEmailField,
                loginPasswordField
            )
            else {
                // Password format incorrect, reset password field
                loginPasswordField.text = null

                loginProgressIndicator.visibility = View.INVISIBLE
                enableInput(view)
            }
        }
        return view
    }

    private fun signIntoGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    // Activity Result for Google Sign in
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleResults(task)
            }
        }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            if (account != null)
                updateFirebaseCredential(account)
        } else {
            Toast.makeText(rootView.context, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * login to firebase from google sign in account
     * @param account google account from [GoogleSignInAccount]
     */
    private fun updateFirebaseCredential(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        Log.d(TAG, "update Firebase credential")
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            Log.d(TAG, "Successful authentication")
            startHomeActivity()
        }
            .addOnCanceledListener {
                Toast.makeText(rootView.context, "Canceled", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Canceled Firebase Authentication")
            }
    }

    /**
     * redirects to home page on login confirmation
     */
    private fun startHomeActivity() {
        //Add parameters or other feature if necessary
        val loginRegisterActivity = rootView.context as LoginRegisterActivity
        Log.d(TAG, "startHomeActivity: " + loginRegisterActivity.parent.toString())
        val intent = Intent(rootView.context, HomeActivity::class.java)
        (rootView.context as LoginRegisterActivity).setResult(
            AppCompatActivity.RESULT_OK,
            (rootView.context as LoginRegisterActivity).intent
        )
        startActivity(intent)
        loginRegisterActivity.finish()
    }

    /**
     * Signs in with firebase using email and password
     * @param emailField EditText of email
     * @param passwordField EditText of password
     */
    private fun authenticateFirebase(
        view: View,
        emailField: TextInputEditText,
        passwordField: TextInputEditText
    ) {
        val loginProgressIndicator: LinearProgressIndicator = view.findViewById(R.id.login_progress)

        val email: String = emailField.text?.trim().toString()
        val password: String = passwordField.text?.trim().toString()

        // Signing in with Email and Password
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            enableInput(view)
            loginProgressIndicator.visibility = View.INVISIBLE
            if (task.isSuccessful) {
                Log.d(TAG, "Login Success, User:" + firebaseAuth.currentUser)
                if (firebaseAuth.currentUser?.isEmailVerified == true) {
                    startHomeActivity()
                } else {
                    Log.d(TAG, "Login Success, email not verified")
                    task.result.user?.sendEmailVerification()
                        ?.addOnCompleteListener { verificationTask ->
                            if (verificationTask.isSuccessful) {
                                Toast.makeText(
                                    view.context,
                                    "Email couldn't be verified, please verify your email sent again to your email address",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    view.context,
                                    "Email couldn't be verified, please contact developer",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    if (firebaseAuth.currentUser != null) {
                        firebaseAuth.signOut()
                    }
                }
            } else {
                Log.d(TAG, "Login Unsuccessful")
                Toast.makeText(view.context, "Login Unsuccessful", Toast.LENGTH_SHORT).show()

                // Login unsuccessful, reset password field
                passwordField.text = null
            }
        }
    }

    /**
     * validate the email and password field
     * @param emailField EditText of email
     * @param passwordField EditText of password
     * @return whether the fields are valid(true) or not(false)
     */
    private fun validateFields(
        emailField: TextInputEditText,
        passwordField: TextInputEditText
    ): Boolean {
        val email: String = emailField.text?.trim().toString()
        val password: String = passwordField.text?.trim().toString()

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
        }
        return true
    }

    /**
     * [isEmailValid] is used to validate Email format
     * @param email Email address to be validated
     * @return Boolean value of validation of Email format
     */
    private fun isEmailValid(email: String): Boolean {
        // Using Regular Expressions
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun disableInput(view: View) {
        view.findViewById<TextInputEditText>(R.id.login_email_field).isEnabled = false
        view.findViewById<TextInputEditText>(R.id.login_password_field).isEnabled = false
        view.findViewById<MaterialButton>(R.id.login_button).isEnabled = false
    }

    private fun enableInput(view: View) {
        view.findViewById<TextInputEditText>(R.id.login_email_field).isEnabled = true
        view.findViewById<TextInputEditText>(R.id.login_password_field).isEnabled = true
        view.findViewById<MaterialButton>(R.id.login_button).isEnabled = true
    }

    companion object {

        /**
         * Use this factory method to create a new instance of
         * this fragment.
         *
         * @return A new instance of fragment LoginFragment.
         */
        @JvmStatic
        fun newInstance() = LoginFragment()
        private const val TAG = "LoginFragment"
    }
}