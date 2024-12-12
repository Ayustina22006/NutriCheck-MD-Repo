package com.example.nutricheck.ui.profil

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.nutricheck.auth.pref.UserModel
import com.example.nutricheck.data.Result
import com.example.nutricheck.data.UserRepository
import com.example.nutricheck.data.response.BMIData
import com.example.nutricheck.data.response.UserResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HealthAssesmentViewModel ( private val  userRepository: UserRepository) : ViewModel() {
    private val _bmiResult = MutableLiveData<Float>()
    val bmiResult: LiveData<Float> get() = _bmiResult

    private val _dailyCalories = MutableLiveData<Int?>()
    val dailyCalories: LiveData<Int?> get() = _dailyCalories

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _height = MutableLiveData<Int>()
    val height: LiveData<Int> get() = _height

    private val _weight = MutableLiveData<Int>()
    val weight: LiveData<Int> get() = _weight

    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
    }

    suspend fun fetchUserBMI(): Flow<Result<UserResponse>> {
        return viewModelScope.launch {
            val userId = userRepository.getSession().first().userId
            userRepository.fetchUserBMI(userId)
        }.let {
            userRepository.fetchUserBMI(userRepository.getSession().first().userId)
        }
    }

    fun fetchDailyCalories(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val calculatedCalories = userRepository.fetchDailyCalories(userId)
                _dailyCalories.postValue(calculatedCalories)
            } catch (e: Exception) {
                _dailyCalories.postValue(null)
            } finally {
                _isLoading.value = false
            }
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