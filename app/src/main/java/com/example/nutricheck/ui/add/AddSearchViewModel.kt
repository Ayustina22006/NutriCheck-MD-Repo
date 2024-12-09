package com.example.nutricheck.ui.add

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.nutricheck.auth.pref.UserModel
import com.example.nutricheck.data.UserRepository
import com.example.nutricheck.data.response.Food
import com.example.nutricheck.data.retrofit.ApiConfig
import kotlinx.coroutines.launch

class AddSearchViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _foodData = MutableLiveData<Food?>()
    val foodData: LiveData<Food?> = _foodData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun setFoodData(food: Food) {
        _foodData.value = food
    }

    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
    }

    fun findHighestNutrition(food: Food): String {
        val nutritions = food.nutritions
        val nutritionMap = mapOf(
            "Calcium" to nutritions.calcium,
            "Protein" to nutritions.protein,
            "Carbohydrate" to nutritions.carbohydrate,
            "Dietary Fiber" to nutritions.dietaryFiber,
            "Iron" to nutritions.iron,
            "Vitamin A" to nutritions.vitaminA,
            "Vitamin B" to nutritions.vitaminB,
            "Vitamin C" to nutritions.vitaminC
        )

        return nutritionMap.maxByOrNull { it.value }?.key ?: "Protein"
    }

    fun resetErrorMessage() {
        _errorMessage.value = null
    }
}