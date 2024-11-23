package com.example.nutricheck.auth.register

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.nutricheck.MainActivity
import com.example.nutricheck.R
import android.text.InputType
import android.widget.ImageView


class RegisterActivity : AppCompatActivity() {
    private val registerViewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Toggle visibility untuk Password
        val passwordField = findViewById<EditText>(R.id.etPassword)
        val togglePassword = findViewById<ImageView>(R.id.btnTogglePassword)
        setupPasswordToggle(passwordField, togglePassword)

        // Toggle visibility untuk Confirm Password
        val confirmPasswordField = findViewById<EditText>(R.id.etConfirmPassword)
        val toggleConfirmPassword = findViewById<ImageView>(R.id.btnToggleConfirmPassword)
        setupPasswordToggle(confirmPasswordField, toggleConfirmPassword)

        registerViewModel.registerResult.observe(this) { result ->
            if (result == "Register Succesfully") {
                val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
            }
        }

        // Logika untuk tombol registrasi
        findViewById<Button>(R.id.btnSignIn).setOnClickListener {
            val username = findViewById<EditText>(R.id.etUsername).text.toString().trim()
            val email = findViewById<EditText>(R.id.etEmailAddress).text.toString().trim()
            val password = passwordField.text.toString()
            val confirmPassword = confirmPasswordField.text.toString()

            if (username.isNotBlank() && email.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank()) {
                if (password == confirmPassword) {
                    registerViewModel.registerUser(username, email, password, confirmPassword)
                } else {
                    Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "All fields must be filled.", Toast.LENGTH_SHORT).show()
            }
        }
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
            // Posisikan kursor di akhir teks
            passwordField.setSelection(passwordField.text.length)
            isPasswordVisible = !isPasswordVisible
        }
    }
}

