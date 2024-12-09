package com.example.nutricheck.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.nutricheck.R
import com.example.nutricheck.auth.pref.UserModel
import com.example.nutricheck.data.UserRepository
import com.example.nutricheck.data.response.ArticleDataItem
import com.example.nutricheck.data.response.ArticleResponse
import com.example.nutricheck.data.response.Food
import com.example.nutricheck.data.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await
import com.example.nutricheck.data.ResourceProvider
import com.example.nutricheck.data.response.HistoryDataItem
import com.example.nutricheck.data.response.NutritionItem


class HomeViewModel(
    private val userRepository: UserRepository,
    private val resourceProvider: ResourceProvider
) : ViewModel() {
    private val _articles = MutableLiveData<List<ArticleDataItem>?>()
    val articles: LiveData<List<ArticleDataItem>?> = _articles

    private val _calorieResult = MutableLiveData<Float?>()
    val calorieResult: LiveData<Float?> get() = _calorieResult

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    // New LiveData for food search
    private val _foodNutritionResult = MutableLiveData<Food?>()
    val foodNutritionResult: LiveData<Food?> = _foodNutritionResult

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _nutritionData = MutableLiveData<List<NutritionItem>>()
    val nutritionData: LiveData<List<NutritionItem>> get() = _nutritionData

    private val _caloriesConsumed = MutableLiveData<Float>()
    val caloriesConsumed: LiveData<Float> = _caloriesConsumed

    // List makanan yang valid
    private val foodList = listOf(
        "Tempeh", "bibimbap", "cheesecake", "chicken Soto", "chicken noodle",
        "chicken porridge", "chicken wings", "chocolate cake", "churros",
        "cup cakes", "donuts", "fish and chips", "french fries",
        "french toast", "fried shrimp", "fried rice", "gado-gado",
        "green bean porridge", "grilled chicken", "gyoza", "hamburger",
        "hot dog", "ice cream", "ikan bakar", "kupat tahu", "lasagna",
        "macaroni and cheese", "macarons", "meatball", "nasi kuning",
        "nasi uduk", "omelette", "oxtail soup", "oysters", "pad thai",
        "pancakes", "pizza", "ramen", "red velvet cake", "rendang",
        "risotto", "samosa", "sashimi", "satay", "spaghetti bolognese",
        "spaghetti carbonara", "spring rolls", "steak", "sushi", "tacos",
        "takoyaki", "tiramisu", "waffles"
    )

    fun searchFood(foodName: String) {
        if (foodList.any { it.equals(foodName, ignoreCase = true) }) {
            fetchFoodNutrition(foodName)
        } else {
            _errorMessage.value = resourceProvider.getString(R.string.food_not_found)
        }
    }

    private fun fetchFoodNutrition(foodName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = userRepository.getToken()
                val apiService = ApiConfig.getApiService(token)
                val response = apiService.getFoodNutrition(foodName)

                if (response.status == 200 && response.data.isNotEmpty()) {
                    _foodNutritionResult.value = response.data[0]
                } else {
                    _errorMessage.value = "Makanan tidak ditemukan dalam database"
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching food nutrition: ${e.message}", e)
                _errorMessage.value = e.message ?: "Terjadi kesalahan saat mencari makanan"
            } finally {
                _isLoading.value = false
            }
        }
    }



    private fun processNutritionData(data: List<HistoryDataItem?>?) {
        val nutritionTotals = mutableMapOf<String, Float>()
        var totalDailyCaloriesConsumed = 0f

        data?.forEach { historyItem ->
            historyItem?.totalNutrition?.let { totalNutrition ->
                nutritionTotals["Calcium"] = (nutritionTotals["Calcium"] ?: 0f) + (totalNutrition.calcium ?: 0f)
                nutritionTotals["Dietary Fiber"] = (nutritionTotals["Dietary Fiber"] ?: 0f) + (totalNutrition.dietaryFiber ?: 0f)
                nutritionTotals["Iron"] = (nutritionTotals["Iron"] ?: 0f) + (totalNutrition.iron ?: 0f)
                nutritionTotals["Protein"] = (nutritionTotals["Protein"] ?: 0f) + (totalNutrition.protein ?: 0f)
                nutritionTotals["Vitamin A"] = (nutritionTotals["Vitamin A"] ?: 0f) + (totalNutrition.vitaminA ?: 0f)
                nutritionTotals["Vitamin B"] = (nutritionTotals["Vitamin B"] ?: 0f) + (totalNutrition.vitaminB ?: 0f)
                nutritionTotals["Vitamin C"] = (nutritionTotals["Vitamin C"] ?: 0f) + (totalNutrition.vitaminC ?: 0f)
                nutritionTotals["Carbohydrate"] = (nutritionTotals["Carbohydrate"] ?: 0f) + (totalNutrition.carbohydrate ?: 0f)
            }

            historyItem?.totalCalories?.let { calories ->
                totalDailyCaloriesConsumed += calories
            }
        }

        val nutritionItems = nutritionTotals.map { (name, amount) ->
            val formattedAmount = String.format("%.0f", amount)
            NutritionItem(name, "$formattedAmount g")
        }

        _nutritionData.postValue(nutritionItems)
        _caloriesConsumed.postValue(totalDailyCaloriesConsumed)
    }


    fun resetErrorMessage() {
        _errorMessage.value = null
    }

    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
    }

    fun resetFoodNutritionResult() {
        _foodNutritionResult.value = null
    }

    fun fetchDailyCalories(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = userRepository.getToken()
                val apiService = ApiConfig.getApiService(token)
                val result = apiService.getUserBMI(userId).await()

                val bmiData = result.data?.bmi
                if (bmiData != null) {
                    if (bmiData.weight in 30..300 && bmiData.height in 100..250) {
                        val calculatedCalories = calculateCalories(
                            gender = bmiData.gender.orEmpty(),
                            weight = bmiData.weight,
                            height = bmiData.height,
                            age = bmiData.age,
                            activityLevel = bmiData.activity.orEmpty()
                        )
                        _calorieResult.postValue(calculatedCalories.toFloat())
                    } else {
                        Log.e("HomeViewModel", "Invalid BMI data: weight=${bmiData.weight}, height=${bmiData.height}")
                        _calorieResult.postValue(null)
                    }
                } else {
                    Log.e("HomeViewModel", "BMI data is null")
                    _calorieResult.postValue(null)
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching or calculating calories: ${e.message}", e)
                _calorieResult.postValue(null)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchArticles() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val token = userRepository.getToken()
                val client = ApiConfig.getApiService(token).getNews()
                client.enqueue(object : Callback<ArticleResponse> {
                    override fun onResponse(call: Call<ArticleResponse>, response: Response<ArticleResponse>) {
                        if (response.isSuccessful) {
                            _articles.postValue(response.body()?.data?.filterNotNull())
                        } else {
                            _articles.postValue(emptyList())
                        }
                    }

                    override fun onFailure(call: Call<ArticleResponse>, t: Throwable) {
                        _articles.postValue(null)
                    }
                })
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching articles: ${e.message}", e)
                _articles.postValue(null)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchNutritionData(date: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val token = userRepository.getToken()
                val response = ApiConfig.getApiService(token).getMealsHistories(date) // Menggunakan fungsi suspend

                if (response.isSuccessful) {
                    val historyItems = response.body()?.data
                    processNutritionData(historyItems) // Proses data ke nutritionItems
                } else {
                    _nutritionData.postValue(emptyList())
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching nutrition data: ${e.message}", e)
                _nutritionData.postValue(emptyList())
            } finally {
                _isLoading.value = false
            }
        }
    }

    companion object {
        fun calculateCalories(
            gender: String,
            weight: Int?,
            height: Int?,
            age: Int?,
            activityLevel: String
        ): Double {
            Log.d("HomeViewModel", "calculateCalories: gender=$gender, weight=$weight, height=$height, age=$age, activityLevel=$activityLevel")

            // Hitung BMR
            val bmr = if (gender.lowercase() == "male") {
                (10 * weight!!) + (6.25 * height!!) - (5 * age!!) + 5
            } else {
                (10 * weight!!) + (6.25 * height!!) - (5 * age!!) - 16
            }
            Log.d("HomeViewModel", "BMR: $bmr")

            // Tentukan faktor aktivitas
            val activityMultiplier = when (activityLevel.lowercase()) {
                "inactive" -> 1.2f
                "light activity" -> 1.375f
                "moderate activity" -> 1.55f
                "high activity" -> 1.725f
                "very high activity" -> 1.9f
                else -> {
                    Log.e("HomeViewModel", "Unknown activity level: $activityLevel")
                    1.2f // Default jika tidak diketahui
                }
            }

            val dailyCalories = bmr * activityMultiplier
            Log.d("HomeViewModel", "Daily calories: $dailyCalories")

            return dailyCalories
        }
    }
}
