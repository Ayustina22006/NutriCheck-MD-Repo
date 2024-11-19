package com.example.nutricheck.auth.register

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.nutricheck.MainActivity
import com.example.nutricheck.R

class RegisterActivity : AppCompatActivity() {
    private val registerViewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register) // Sesuaikan dengan layout Anda

        // Observasi hasil registrasi
        registerViewModel.registerResult.observe(this) { result ->
            if (result == "Registration successful!") {
                val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish() // Menutup RegisterActivity
            } else {
                // Jika gagal, tampilkan pesan error
                Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
            }
        }

        // Button untuk melakukan registrasi
        findViewById<Button>(R.id.btnSignIn).setOnClickListener {
            val username = findViewById<EditText>(R.id.etUsername).text.toString().trim()
            val email = findViewById<EditText>(R.id.etEmailAddress).text.toString().trim()
            val password = findViewById<EditText>(R.id.etPassword).text.toString()
            val confirmPassword = findViewById<EditText>(R.id.etConfirmPassword).text.toString()

            // Validasi input
            if (username.isNotBlank() && email.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank()) {
                registerViewModel.registerUser(username, email, password, confirmPassword)
            } else {
                Toast.makeText(this, "All fields must be filled.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
