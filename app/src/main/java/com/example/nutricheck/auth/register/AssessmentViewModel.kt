package com.example.nutricheck.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.example.nutricheck.data.UserRepository
import com.example.nutricheck.data.response.AssessmentRequest
import com.example.nutricheck.data.response.AssessmentResponse
import com.example.nutricheck.data.retrofit.ApiConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AssessmentViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _gender = MutableLiveData<String?>()
    val gender: LiveData<String?> get() = _gender

    private val _age = MutableLiveData<Int?>()
    val age: LiveData<Int?> get() = _age

    private val _height = MutableLiveData<Int?>()
    val height: LiveData<Int?> get() = _height

    private val _weight = MutableLiveData<Int?>()
    val weight: LiveData<Int?> get() = _weight

    private val _activity = MutableLiveData<String?>()
    val activity: LiveData<String?> get() = _activity

    private val _userId = MutableLiveData<String?>()
    val userId: LiveData<String?> get() = _userId

    private val _assessmentResult = MutableLiveData<String>()
    val assessmentResult: LiveData<String> get() = _assessmentResult

    fun setUserId(userId: String) {
        _userId.value = userId
    }

    fun setGender(gender: String) {
        _gender.value = gender
    }

    fun setAge(age: Int) {
        _age.value = age
    }

    fun setHeight(height: Int) {
        _height.value = height
    }

    fun setWeight(weight: Int) {
        _weight.value = weight
    }

    fun setActivity(activity: String) {
        _activity.value = activity
    }

    suspend fun submitAssessment(userId: String, request: AssessmentRequest) {
        val token = userRepository.getToken()
        if (token.isNotEmpty()) {
            val apiService = ApiConfig.getApiService(token)
            withContext(Dispatchers.IO) {
                apiService.submitAssessment(userId, request).enqueue(object : Callback<AssessmentResponse> {
                    override fun onResponse(call: Call<AssessmentResponse>, response: Response<AssessmentResponse>) {
                        if (response.isSuccessful) {
                            _assessmentResult.postValue("Assessment berhasil dikirim!")
                        } else {
                            _assessmentResult.postValue("Gagal mengirim assessment: ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<AssessmentResponse>, t: Throwable) {
                        _assessmentResult.postValue("Error jaringan: ${t.message}")
                    }
                })
            }
        } else {
            _assessmentResult.postValue("Token tidak ditemukan, silakan login ulang.")
        }
    }
}
