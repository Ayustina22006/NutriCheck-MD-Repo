package com.example.nutricheck.auth.login

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.nutricheck.MainActivity
import com.example.nutricheck.R
import com.example.nutricheck.ViewModelFactory
import com.example.nutricheck.auth.pref.UserModel
import com.example.nutricheck.auth.register.RegisterActivity
import com.example.nutricheck.data.Result
import com.example.nutricheck.data.response.GoogleLoginRequest
import com.example.nutricheck.data.response.GoogleLoginResponse
import com.example.nutricheck.data.retrofit.ApiConfig
import com.example.nutricheck.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupGoogleSignIn()

        viewModel.getSession().observe(this) { session ->
            if (session.isLogin) {
                startMainActivity()
            }
        }

        val passwordField = binding.etPassword
        val togglePassword = binding.btnTogglePassword
        setupPasswordToggle(passwordField, togglePassword)

        // Login biasa dengan email dan password
        binding.btnSignIn.setOnClickListener {
            val email = binding.etEmailAddress.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Email dan password tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Format email tidak valid", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            hideKeyboard()
            loginUser(email, password)
        }

        // Arahkan ke halaman register
        binding.btnSignUp.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Login dengan Google
        binding.btnSignWithGoogle.setOnClickListener {
            startGoogleSignIn()
        }
    }

    private fun loginUser(email: String, password: String) {
        viewModel.login(email, password).observe(this) { result ->
            when (result) {
                is Result.Loading -> showLoading(true)
                is Result.Success -> {
                    showLoading(false)
                    val user = result.data.data?.user
                    Toast.makeText(this, "Welcome ${user?.username}", Toast.LENGTH_SHORT).show()
                    startMainActivity()
                }
                is Result.Error -> {
                    showLoading(false)
                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                }
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
            passwordField.setSelection(passwordField.text.length)
            isPasswordVisible = !isPasswordVisible
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnSignIn.isEnabled = !isLoading
    }

    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Ambil dari google-services.json
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun startGoogleSignIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account?.idToken
                if (idToken != null) {
                    sendTokenToBackend(idToken)
                } else {
                    Log.e("GoogleSignIn", "ID Token is null")
                    Toast.makeText(this, "ID Token is null", Toast.LENGTH_SHORT).show()
                }
            } catch (e: ApiException) {
                Log.e("GoogleSignIn", "ApiException: ${e.statusCode}", e)
                Toast.makeText(this, "Login Google gagal: ${e.statusCode}", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun handleGoogleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            account?.idToken?.let { idToken ->
                sendTokenToBackend(idToken)
            }
        } catch (e: ApiException) {
            Toast.makeText(this, "Login Google gagal: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendTokenToBackend(idToken: String) {
        val apiService = ApiConfig.getApiService()
        val call = apiService.googleLogin(GoogleLoginRequest(email = idToken, username = null))
        call.enqueue(object : Callback<GoogleLoginResponse> {
            override fun onResponse(
                call: Call<GoogleLoginResponse>,
                response: Response<GoogleLoginResponse>
            ) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse?.status == 200) {
                        val user = UserModel(
                            email = loginResponse.user?.email.orEmpty(),
                            token = loginResponse.token.orEmpty(),
                            isLogin = true,
                            userId = loginResponse.user?.id.orEmpty()
                        )
                        viewModel.saveSession(user) // Gunakan ViewModel
                        startMainActivity()
                    } else {
                        Toast.makeText(this@LoginActivity, "Login gagal: ${loginResponse?.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GoogleLoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    companion object {
        private const val RC_SIGN_IN = 100
    }
}
