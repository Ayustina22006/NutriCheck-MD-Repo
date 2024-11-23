package com.example.nutricheck.auth.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nutricheck.data.response.RegisterResponse
import com.example.nutricheck.data.response.UserRequest
import com.example.nutricheck.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel : ViewModel() {
    private val _registerResult = MutableLiveData<String>()
    val registerResult: LiveData<String> = _registerResult

    fun registerUser(username: String, email: String, password: String, confirmPassword: String) {
        if (password != confirmPassword) {
            _registerResult.postValue("Password and Confirm Password do not match.")
            return
        }

        val apiService = ApiConfig.getApiService()
        val userRequest = UserRequest(username, email, password)

        apiService.registerUser(userRequest).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful && response.body()?.status == 201) { // Sesuaikan dengan respons API
                    val message = response.body()?.message ?: "Register Succesfully"
                    _registerResult.postValue(message)
                } else {
                    _registerResult.postValue("Registration failed: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _registerResult.postValue("Registration failed: ${t.message}")
            }
        })
    }
}

