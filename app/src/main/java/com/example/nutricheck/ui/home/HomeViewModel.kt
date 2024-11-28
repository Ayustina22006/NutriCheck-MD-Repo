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
import com.example.nutricheck.ui.profil.ProfilViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.nutricheck.data.Result
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
                if (result.bmi != null) {
                    val data = result.bmi
                    val calories = ProfilViewModel.calculateCalories(
                        gender = data.gender,
                        weight = data.weight,
                        height = data.height,
                        age = data.age,
                        activityLevel = data.activity
                    )
                    _calorieResult.postValue(calories.toFloat())
                } else {
                    _calorieResult.postValue(null)
                }
            } catch (e: Exception) {
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


}