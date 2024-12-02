package com.example.nutricheck.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.nutricheck.auth.pref.UserModel
import com.example.nutricheck.data.UserRepository
import com.example.nutricheck.data.response.ArticleDataItem
import com.example.nutricheck.data.response.ArticleResponse
import com.example.nutricheck.data.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await

class HomeViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _articles = MutableLiveData<List<ArticleDataItem>?>()
    val articles: LiveData<List<ArticleDataItem>?> = _articles
    private val _calorieResult = MutableLiveData<Float?>()
    val calorieResult: MutableLiveData<Float?> get() = _calorieResult

    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
    }

    fun fetchDailyCalories(userId: String) {
        viewModelScope.launch {
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
            }
        }
    }

    fun fetchArticles() {
        viewModelScope.launch {
            try {
                val token = userRepository.getToken()
                if (token.isNotEmpty()) {
                    val client = ApiConfig.getApiService(token).getNews()
                    client.enqueue(object : Callback<ArticleResponse> {
                        override fun onResponse(call: Call<ArticleResponse>, response: Response<ArticleResponse>) {
                            if (response.isSuccessful) {
                                val articles = response.body()?.data
                                articles?.forEach { article ->
                                    Log.d("ArticleCategory", "Article: ${article?.title}, Categories: ${article?.categories}")
                                }

                                if (!articles.isNullOrEmpty()) {
                                    val validArticles = articles.filterNotNull()
                                    _articles.postValue(validArticles)
                                } else {
                                    Log.e("HomeViewModel", "Article list is empty")
                                    _articles.postValue(emptyList())
                                }
                            } else {
                                Log.e("HomeViewModel", "Response not successful: ${response.errorBody()?.string()}")
                                _articles.postValue(null)
                            }
                        }

                        override fun onFailure(call: Call<ArticleResponse>, t: Throwable) {
                            Log.e("HomeViewModel", "Failed to fetch articles: ${t.message}")
                            _articles.postValue(null)
                        }
                    })
                } else {
                    Log.e("HomeViewModel", "Token is missing!")
                    _articles.postValue(null)
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error in fetchArticles: ${e.message}")
                _articles.postValue(null)
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
                    Log.e("HomeViewModel", "Unknown activity level: $activityLevel, using default multiplier 1.2")
                    1.2f
                }
            }
            Log.d("HomeViewModel", "Activity Multiplier: $activityMultiplier")

            // Hitung total kalori
            val totalCalories = bmr * activityMultiplier
            Log.d("HomeViewModel", "Total Calories: $totalCalories")
            return totalCalories
        }
    }




}