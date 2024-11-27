package com.example.nutricheck.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutricheck.data.UserRepository
import com.example.nutricheck.data.response.ArticleDataItem
import com.example.nutricheck.data.response.ArticleResponse
import com.example.nutricheck.data.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _articles = MutableLiveData<List<ArticleDataItem>?>()
    val articles: LiveData<List<ArticleDataItem>?> = _articles

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