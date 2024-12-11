package com.example.nutricheck.ui.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutricheck.data.Result
import com.example.nutricheck.data.UserRepository
import com.example.nutricheck.data.response.AddManualNutritions
import com.example.nutricheck.data.response.AddManualResponse
import com.example.nutricheck.data.response.AddMealRequest
import com.example.nutricheck.data.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.await

class AddViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _addMealResult = MutableLiveData<Result<AddManualResponse>>()
    val addMealResult: LiveData<Result<AddManualResponse>> = _addMealResult

    fun addManualMeal(
        calcium: Double?,
        dietaryFiber: Double?,
        iron: Double,
        foodName: String,
        servingSize: Int,
        calories: Int,
        protein: Double,
        carbohydrate: Double,
        vitaminA: Int,
        vitaminB: Int,
        vitaminC: Double
    ) {
        _addMealResult.value = Result.Loading
        viewModelScope.launch {
            try {
                val token = userRepository.getToken()
                val apiService = ApiConfig.getApiService(token)

                val nutritions = AddManualNutritions(
                    calcium = calcium,
                    dietaryFiber = dietaryFiber,
                    iron = iron,
                    protein = protein,
                    vitaminA = vitaminA,
                    vitaminB = vitaminB,
                    vitaminC = vitaminC,
                    carbohydrate = carbohydrate
                )

                val request = AddMealRequest(
                    calories = calories,
                    foodName = foodName,
                    servingSizeGrams = servingSize,
                    nutritions = nutritions
                )

                val response = apiService.addManualMeal(request).await()
                _addMealResult.value = Result.Success(response)
            } catch (e: Exception) {
                _addMealResult.value = Result.Error(e.message ?: "Terjadi kesalahan")
            }
        }
    }

}