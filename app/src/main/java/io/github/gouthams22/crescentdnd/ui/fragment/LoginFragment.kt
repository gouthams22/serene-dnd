package io.github.gouthams22.crescentdnd.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import io.github.gouthams22.crescentdnd.R
import io.github.gouthams22.crescentdnd.ui.activity.HomeActivity

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

        val button: MaterialButton = view.findViewById(R.id.login_button)
        val loginEmailField: TextInputEditText = view.findViewById(R.id.login_email_field)
        val loginPasswordField: TextInputEditText = view.findViewById(R.id.login_password_field)

        //set on click listener for Login button
        button.setOnClickListener {
            button.isEnabled = false
            loginEmailField.isEnabled = false
            loginPasswordField.isEnabled = false
            val isValid = validateFields(loginEmailField, loginPasswordField)
            if (isValid) authenticateFirebase(view)
            button.isEnabled = true
            loginEmailField.isEnabled = true
            loginPasswordField.isEnabled = true
        }
        return view
    }

    private fun startHomeActivity(view: View) {
        //Add parameters or other feature if necessary
        startActivity(Intent(view.context,HomeActivity::class.java))
    }

    private fun authenticateFirebase(view: View) {
        startHomeActivity(view)
        //TODO("Implement Firebase Authentication")
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