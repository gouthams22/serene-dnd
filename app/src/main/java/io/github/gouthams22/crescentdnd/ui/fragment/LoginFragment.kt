package io.github.gouthams22.crescentdnd.ui.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import io.github.gouthams22.crescentdnd.R
import io.github.gouthams22.crescentdnd.ui.activity.HomeActivity
import io.github.gouthams22.crescentdnd.ui.activity.LoginRegisterActivity

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val logTag: String = "LoginFragment"
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var rootView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        rootView = view

        val loginEmailField: TextInputEditText = view.findViewById(R.id.login_email_field)
        val loginPasswordField: TextInputEditText = view.findViewById(R.id.login_password_field)

        firebaseAuth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(view.context as LoginRegisterActivity, gso)

        val loginGoogleButton: MaterialButton = view.findViewById(R.id.login_google_button)
        loginGoogleButton.setOnClickListener {
            signIntoGoogle()
        }

        val loginButton: MaterialButton = view.findViewById(R.id.login_button)
        //set on click listener for Login button
        loginButton.setOnClickListener {
            loginButton.isEnabled = false
            loginEmailField.isEnabled = false
            loginPasswordField.isEnabled = false
            val isValid = validateFields(loginEmailField, loginPasswordField)
            Log.d(
                logTag,
                if (isValid) "Login Form fields are in valid state" else "Login Form is invalid"
            )
            if (isValid) authenticateFirebase(
                view,
                loginEmailField,
                loginPasswordField
            )
            loginButton.isEnabled = true
            loginEmailField.isEnabled = true
            loginPasswordField.isEnabled = true
        }
        return view
    }

    private fun signIntoGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

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

    private fun updateFirebaseCredential(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        Log.d(logTag, "update Firebase credential")
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            Log.d(logTag, "Successful authentication")
            startHomeActivity()
        }
            .addOnCanceledListener {
                Toast.makeText(rootView.context, "Canceled", Toast.LENGTH_SHORT).show()
                Log.d(logTag, "Canceled Firebase Authentication")
            }
    }

    private fun startHomeActivity() {
        //Add parameters or other feature if necessary
        val loginRegisterActivity =rootView.context as LoginRegisterActivity
        Log.d(logTag, "startHomeActivity: "+loginRegisterActivity.parent.toString())
        loginRegisterActivity.parent?.finish()
        val intent = Intent(rootView.context, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        loginRegisterActivity.finish()
    }

    private fun authenticateFirebase(
        view: View,
        emailField: TextInputEditText,
        passwordField: TextInputEditText
    ) {
        val email: String = emailField.text?.trim().toString()
        val password: String = passwordField.text?.trim().toString()
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(logTag, "Login Success, User:" + firebaseAuth.currentUser)
                if (firebaseAuth.currentUser?.isEmailVerified == true) {
                    startHomeActivity()
                } else {
                    Log.d(logTag, "Login Success, email not verified")
                    Toast.makeText(view.context, "Email is not verified", Toast.LENGTH_SHORT).show()
                    if (firebaseAuth.currentUser != null) {
                        firebaseAuth.signOut()
                    }
                }
            } else {
                Log.d(logTag, "Login Unsuccessful")
                Toast.makeText(view.context, "Login Unsuccessful", Toast.LENGTH_SHORT).show()
                passwordField.text = null
            }
        }
//        startHomeActivity()
    }

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

    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    companion object {
        //  /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment LoginFragment.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            LoginFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
        @JvmStatic
        fun newInstance() = LoginFragment()
    }
}