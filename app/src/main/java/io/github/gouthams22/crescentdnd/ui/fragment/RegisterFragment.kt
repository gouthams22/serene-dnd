package io.github.gouthams22.crescentdnd.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import io.github.gouthams22.crescentdnd.R
import io.github.gouthams22.crescentdnd.ui.activity.LoginRegisterActivity

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterFragment : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_register, container, false)
        val registerButton: MaterialButton = view.findViewById(R.id.register_button)
        val regEmailField: TextInputEditText = view.findViewById(R.id.reg_email_field)
        val regPasswordField: TextInputEditText = view.findViewById(R.id.reg_password_field)
        val regConfirmPasswordField: TextInputEditText =
            view.findViewById(R.id.reg_confirm_password_field)

        //Checking if "Confirm password" and "password" are equal
        regConfirmPasswordField.doOnTextChanged { text, start, before, count ->
            regConfirmPasswordField.error =
                if (text.toString() == regPasswordField.text.toString()) null else "Passwords doesn't match"
        }

        //On clicking register button
        registerButton.setOnClickListener {
            it.isEnabled = false
            if (validateFields(regEmailField, regPasswordField, regConfirmPasswordField)) {
                registerAccount()
            }
            it.isEnabled = true
        }

        //Select 'Login' tab if user has account
        view.findViewById<MaterialTextView>(R.id.have_account).setOnClickListener {
            it.isEnabled = false
            (view.context as LoginRegisterActivity).haveAccount()
            it.isEnabled = true
        }
        return view
    }

    private fun registerAccount() {
        TODO("Implement to add on Firebase")
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

    companion object {
        //  /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment RegisterFragment.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            RegisterFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
        @JvmStatic
        fun newInstance() = RegisterFragment()
    }
}