package com.example.nutricheck.ui.nutrition

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutricheck.data.UserRepository
import com.example.nutricheck.data.response.MealDetail
import com.example.nutricheck.data.response.MealHistory
import com.example.nutricheck.data.response.MealHistoryResponse
import kotlinx.coroutines.launch

class NutritionViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _mealHistory = MutableLiveData<MealHistory?>()
    val mealHistory: LiveData<MealHistory?> = _mealHistory

    private val _mealDetails = MutableLiveData<List<MealDetail>>()
    val mealDetails: LiveData<List<MealDetail>> = _mealDetails

    private val _totalCalories = MutableLiveData<Int>()
    val totalCalories: LiveData<Int> = _totalCalories

    fun loadMealHistory(mealHistoryResponse: MealHistoryResponse) {
        viewModelScope.launch {
            mealHistoryResponse.mealHistory?.let { history ->
                _mealHistory.value = history
                _mealDetails.value = history.mealsDetails ?: emptyList()
                _totalCalories.value = history.totalCalories ?: 0
            }
        }
    }

    fun formatNutritionValue(value: Double?): String {
        return value?.let { String.format("%.1f", it) } ?: "0.0"
    }

    fun formatNutritionValue(value: Int?): String {
        return value?.toString() ?: "0"
    }
}