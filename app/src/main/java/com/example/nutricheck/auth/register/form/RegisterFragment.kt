package com.example.nutricheck.auth.register.form

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.nutricheck.R
import com.example.nutricheck.ViewModelFactory
import com.example.nutricheck.auth.login.LoginActivity
import com.example.nutricheck.auth.register.AssessmentViewModel
import com.example.nutricheck.auth.register.RegisterViewModel
import com.example.nutricheck.databinding.FragmentRegisterBinding
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {
    private lateinit var registerViewModel: RegisterViewModel
    private val assessmentViewModel: AssessmentViewModel by activityViewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRegisterBinding.inflate(inflater, container, false)

        // Observasi data assessment
        assessmentViewModel.assessmentResult.observe(viewLifecycleOwner) { result ->
            Toast.makeText(context, result, Toast.LENGTH_SHORT).show()
        }

        // Inisialisasi ViewModel untuk register
        val factory = ViewModelFactory.getInstance(requireContext())
        registerViewModel = ViewModelProvider(this, factory)[RegisterViewModel::class.java]

        // Mengambil referensi EditText dan ImageView
        val passwordField = binding.etPassword
        val togglePassword = binding.btnTogglePassword
        setupPasswordToggle(passwordField, togglePassword)

        val confirmPasswordField = binding.etConfirmPassword
        val toggleConfirmPassword = binding.btnToggleConfirmPassword
        setupPasswordToggle(confirmPasswordField, toggleConfirmPassword)

        // Mengamati hasil registrasi
        registerViewModel.registerResult.observe(viewLifecycleOwner) { result ->
            if (result == "Register Succesfully") {
                findNavController().navigate(R.id.action_Register_to_Asessment1)
            } else {
                Toast.makeText(context, result, Toast.LENGTH_SHORT).show()
            }
        }

        // Mengirim userId ke assessmentViewModel
        registerViewModel.userId.observe(viewLifecycleOwner) { userId ->
            userId?.let {
                assessmentViewModel.setUserId(it)
            }
        }

        // Proses login button
        binding.btnSignIn.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val email = binding.etEmailAddress.text.toString().trim()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            // Validasi input
            if (username.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                Toast.makeText(context, "All fields must be filled.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.etEmailAddress.error = "Invalid email format"
                return@setOnClickListener
            }

            if (password.length < 8 || !password.any { it.isLetter() } || !password.any { it.isDigit() }) {
                binding.etPassword.error = "Password must be at least 8 characters with letters and digits"
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                binding.etConfirmPassword.error = "Passwords do not match"
                return@setOnClickListener
            }

            lifecycleScope.launch {
                registerViewModel.registerUser(username, email, password, confirmPassword)
            }
        }

        setupPasswordToggle(binding.etPassword, binding.btnTogglePassword)
        setupPasswordToggle(binding.etConfirmPassword, binding.btnToggleConfirmPassword)

        binding.btnSign.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        return binding.root
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
