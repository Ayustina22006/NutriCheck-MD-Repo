package com.example.nutricheck.auth.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutricheck.auth.pref.UserModel
import com.example.nutricheck.data.UserRepository
import com.example.nutricheck.data.response.RegisterRequest
import com.example.nutricheck.data.response.RegisterResponse
import com.example.nutricheck.data.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _registerResult = MutableLiveData<String>()
    val registerResult: LiveData<String> get() = _registerResult

    private val _userId = MutableLiveData<String>()
    val userId: LiveData<String> get() = _userId

    fun registerUser(username: String, email: String, password: String, confirmPassword: String) {
        val registerRequest = RegisterRequest(username, email, password)
        val call = ApiConfig.getApiService("").registerUser(registerRequest) // Token kosong untuk registrasi

        call.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.data?.let { userData ->
                        _registerResult.value = responseBody.message
                        _userId.value = userData.userId
                        viewModelScope.launch {
                            userRepository.saveSession(UserModel(email, userData.token, true, userData.userId))
                        }
                    }
                } else {
                    _registerResult.value = "Registration failed: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _registerResult.value = "Error: ${t.message}"
            }
        })
    }
}




