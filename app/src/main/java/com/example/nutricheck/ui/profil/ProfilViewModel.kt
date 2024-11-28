package com.example.nutricheck.ui.profil

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.nutricheck.auth.pref.UserModel
import com.example.nutricheck.data.UserRepository
import com.example.nutricheck.data.Result
import com.example.nutricheck.data.response.BMIData
import kotlinx.coroutines.launch

class ProfilViewModel(private val repository: UserRepository) : ViewModel() {
    private val _logoutStatus = MutableLiveData<Boolean>()
    val logoutStatus: LiveData<Boolean> get() = _logoutStatus

    private val _bmiData = MutableLiveData<BMIData?>()
    val bmiData: MutableLiveData<BMIData?> get() = _bmiData

    private val _fetchStatus = MutableLiveData<String>()
    val fetchStatus: LiveData<String> get() = _fetchStatus

    private val _bmiResult = MutableLiveData<Float>()
    val bmiResult: LiveData<Float> get() = _bmiResult

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _logoutStatus.postValue(true)
        }
    }

    fun fetchUserBMI(userId: String) {
        viewModelScope.launch {
            repository.fetchUserBMI(userId).collect { result ->
                when (result) {
                    is Result.Loading -> _fetchStatus.postValue("Loading...")
                    is Result.Success -> {
                        val bmiData = result.data.data?.bmi
                        if (bmiData != null) {
                            _bmiData.postValue(bmiData)
                            _fetchStatus.postValue("Berhasil mendapatkan data BMI!")
                            calculateBMI(bmiData)
                        } else {
                            _fetchStatus.postValue("BMI data is null!")
                        }
                    }
                    is Result.Error -> _fetchStatus.postValue("Gagal mendapatkan data BMI: ${result.error}")
                    else -> {}
                }
            }
        }
    }

    private fun calculateBMI(bmiData: BMIData) {
        val heightInMeters = (bmiData.height ?: 0) / 100.0f
        val bmi = (bmiData.weight ?: 0) / (heightInMeters * heightInMeters)
        _bmiResult.postValue(bmi)
    }

}
