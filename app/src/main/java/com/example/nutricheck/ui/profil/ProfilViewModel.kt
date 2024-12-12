package com.example.nutricheck.ui.profil

import android.util.Log
import androidx.lifecycle.*
import com.example.nutricheck.auth.pref.UserModel
import com.example.nutricheck.data.UserRepository
import com.example.nutricheck.data.Result
import com.example.nutricheck.data.response.BMIData
import kotlinx.coroutines.launch

class ProfilViewModel(private val repository: UserRepository) : ViewModel() {
    private val _logoutStatus = MutableLiveData<Boolean>()
    val logoutStatus: LiveData<Boolean> get() = _logoutStatus

    private val _bmiData = MutableLiveData<BMIData?>()
    val bmiData: LiveData<BMIData?> get() = _bmiData

    private val _fetchStatus = MutableLiveData<String>()
    val fetchStatus: LiveData<String> get() = _fetchStatus

    private val _bmiResult = MutableLiveData<Float>()
    val bmiResult: LiveData<Float> get() = _bmiResult

    private val _username = MutableLiveData<String>()
    val username: LiveData<String> get() = _username

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _dailyCalories = MutableLiveData<Int?>()
    val dailyCalories: LiveData<Int?> get() = _dailyCalories

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _logoutStatus.postValue(true)
        }
    }

    fun fetchUserProfile(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = repository.getToken()
                Log.d("ProfilViewModel", "Fetching profile with token: $token")
                repository.fetchUserBMI(userId).collect { result ->
                    when (result) {
                        is Result.Loading -> _isLoading.value = true
                        is Result.Success -> {
                            val userData = result.data.data
                            if (userData != null) {
                                _username.postValue(userData.username ?: "Unknown User")

                                val bmiData = userData.bmi
                                if (bmiData != null) {
                                    _bmiData.postValue(bmiData)
                                    calculateBMI(bmiData)
                                }
                            }
                        }
                        is Result.Error -> _fetchStatus.postValue("Failed to fetch user profile: ${result.error}")
                    }
                }
            } catch (e: Exception) {
                Log.e("ProfilViewModel", "Error fetching token: ${e.message}")
                _fetchStatus.postValue("Token error: ${e.message}")
            }
        }
    }

    fun fetchDailyCalories(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val calculatedCalories = repository.fetchDailyCalories(userId)
                _dailyCalories.postValue(calculatedCalories)
            } catch (e: Exception) {
                _dailyCalories.postValue(null)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getInitials(username: String): String {
        val parts = username.trim().split(" ")
        return when {
            parts.size >= 2 -> parts[0].first().uppercase() + parts[1].first().uppercase()
            parts.isNotEmpty() -> parts[0].first().uppercase()
            else -> ""
        }
    }

    private fun calculateBMI(bmiData: BMIData) {
        val heightInMeters = (bmiData.height ?: 0) / 100.0f
        if (heightInMeters > 0) {
            val bmi = (bmiData.weight ?: 0) / (heightInMeters * heightInMeters)
            _bmiResult.postValue(bmi)
        } else {
            Log.e("ProfilViewModel", "Invalid height for BMI calculation")
        }
    }
}
