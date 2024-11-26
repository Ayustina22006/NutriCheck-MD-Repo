package com.example.nutricheck.auth.register.form

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.nutricheck.R
import com.example.nutricheck.auth.register.AssessmentViewModel
import com.example.nutricheck.auth.register.RegisterViewModel
import com.example.nutricheck.ViewModelFactory
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {
    private lateinit var registerViewModel: RegisterViewModel
    private val assessmentViewModel: AssessmentViewModel by activityViewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        assessmentViewModel.assessmentResult.observe(viewLifecycleOwner) { result ->
            Toast.makeText(context, result, Toast.LENGTH_SHORT).show()
        }
        val binding = inflater.inflate(R.layout.fragment_register, container, false)

        val factory = ViewModelFactory.getInstance(requireContext())
        registerViewModel = ViewModelProvider(this, factory)[RegisterViewModel::class.java]

        val passwordField = binding.findViewById<EditText>(R.id.etPassword)
        val togglePassword = binding.findViewById<ImageView>(R.id.btnTogglePassword)
        setupPasswordToggle(passwordField, togglePassword)

        val confirmPasswordField = binding.findViewById<EditText>(R.id.etConfirmPassword)
        val toggleConfirmPassword = binding.findViewById<ImageView>(R.id.btnToggleConfirmPassword)
        setupPasswordToggle(confirmPasswordField, toggleConfirmPassword)

        registerViewModel.registerResult.observe(viewLifecycleOwner) { result ->
            if (result == "Register Succesfully") {
                findNavController().navigate(R.id.action_Register_to_Asessment1)

            } else {
                Toast.makeText(context, result, Toast.LENGTH_SHORT).show()
            }
        }

        registerViewModel.userId.observe(viewLifecycleOwner) { userId ->
            userId?.let {
                assessmentViewModel.setUserId(it)
            }
        }
        binding.findViewById<Button>(R.id.btnSignIn).setOnClickListener {
            val username = binding.findViewById<EditText>(R.id.etUsername).text.toString().trim()
            val email = binding.findViewById<EditText>(R.id.etEmailAddress).text.toString().trim()
            val password = passwordField.text.toString()
            val confirmPassword = confirmPasswordField.text.toString()

            if (username.isNotBlank() && email.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank()) {
                if (password == confirmPassword) {
                    lifecycleScope.launch {
                        registerViewModel.registerUser(username, email, password, confirmPassword)
                    }
                } else {
                    Toast.makeText(context, "Passwords do not match.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "All fields must be filled.", Toast.LENGTH_SHORT).show()
            }
        }
        return binding
    }

    private fun setupPasswordToggle(passwordField: EditText, toggleButton: ImageView) {
        var isPasswordVisible = false
        toggleButton.setOnClickListener {
            if (isPasswordVisible) {
                passwordField.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                toggleButton.setImageResource(R.drawable.solid_eye)
            } else {
                passwordField.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                toggleButton.setImageResource(R.drawable._4_eye)
            }
            passwordField.setSelection(passwordField.text.length)
            isPasswordVisible = !isPasswordVisible
        }
    }
}
