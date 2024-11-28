package com.example.nutricheck.ui.profil

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.nutricheck.auth.pref.UserModel
import com.example.nutricheck.data.UserRepository
import com.example.nutricheck.data.Result
import com.example.nutricheck.data.response.BmiData
import kotlinx.coroutines.launch

class ProfilViewModel(private val repository: UserRepository) : ViewModel() {
    private val _logoutStatus = MutableLiveData<Boolean>()
    val logoutStatus: LiveData<Boolean> get() = _logoutStatus

    private val _bmiData = MutableLiveData<BmiData>()
    val bmiData: LiveData<BmiData> get() = _bmiData

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
                        _bmiData.postValue(result.data.bmi)
                        _fetchStatus.postValue("Berhasil mendapatkan data BMI!")

                        val bmi = result.data.bmi
                        val calories = calculateCalories(
                            gender = bmi.gender,
                            weight = bmi.weight,
                            height = bmi.height,
                            age = bmi.age,
                            activityLevel = bmi.activity
                        )
                        calculateBMI(bmi)
                    }
                    is Result.Error -> _fetchStatus.postValue("Gagal mendapatkan data BMI: ${result.error}")
                    else -> {}
                }
            }
        }
    }


    // Fungsi untuk menghitung BMI
    private fun calculateBMI(bmiData: BmiData) {
        val heightInMeters = bmiData.height / 100.0f // Mengubah tinggi badan dari cm ke meter
        val bmi = bmiData.weight / (heightInMeters * heightInMeters) // Rumus BMI
        _bmiResult.postValue(bmi) // Memperbarui nilai BMI di LiveData
    }

    companion object {
        fun calculateCalories(
            gender: String,
            weight: Int,
            height: Int,
            age: Int,
            activityLevel: String
        ): Double {
            // 1. Hitung BMR
            val bmr = if (gender.lowercase() == "male") {
                10 * weight + 6.25 * height - 5 * age + 5
            } else {
                10 * weight + 6.25 * height - 5 * age - 161
            }

            // 2. Tentukan faktor aktivitas
            val activityMultiplier = when (activityLevel.lowercase()) {
                "sedentary" -> 1.2f
                "light" -> 1.375f
                "moderate" -> 1.55f
                "active" -> 1.725f
                "extra" -> 1.9f
                else -> 1.2f // Default ke sedentary jika tidak diketahui
            }

            // 3. Hitung total kalori
            return bmr * activityMultiplier
        }
    }



}
