package com.example.nutricheck.ui.pedia

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

class ArticleViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _articles = MutableLiveData<List<ArticleDataItem>?>()
    val articles: LiveData<List<ArticleDataItem>?> = _articles

    private var lastSearchResult: List<ArticleDataItem>? = null

    // Untuk mendapatkan sesi pengguna saat ini
    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
    }

    // Fungsi untuk mengambil semua artikel
    fun fetchArticles() {
        viewModelScope.launch {
            try {
                val token = userRepository.getToken()

                if (token.isNotEmpty()) {
                    val client = ApiConfig.getApiService(token).getNews()
                    client.enqueue(object : Callback<ArticleResponse> {
                        override fun onResponse(call: Call<ArticleResponse>, response: Response<ArticleResponse>) {
                            if (response.isSuccessful) {
                                val articles = response.body()?.data?.filterNotNull() ?: emptyList()
                                lastSearchResult = null // Reset hasil pencarian
                                _articles.postValue(articles)
                            } else {
                                Log.e("ArticleViewModel", "Response not successful: ${response.errorBody()?.string()}")
                                _articles.postValue(emptyList())
                            }
                        }

                        override fun onFailure(call: Call<ArticleResponse>, t: Throwable) {
                            Log.e("ArticleViewModel", "Failed to fetch articles: ${t.message}")
                            _articles.postValue(emptyList())
                        }
                    })
                } else {
                    Log.e("ArticleViewModel", "Token is missing!")
                    _articles.postValue(emptyList())
                }
            } catch (e: Exception) {
                Log.e("ArticleViewModel", "Error in fetchArticles: ${e.message}")
                _articles.postValue(emptyList())
            }
        }
    }

    // Fungsi untuk melakukan pencarian artikel berdasarkan kata kunci
    fun searchArticles(keyword: String) {
        viewModelScope.launch {
            try {
                val token = userRepository.getToken()
                if (token.isNotEmpty()) {
                    val response = ApiConfig.getApiService(token).getSearch(keyword)
                    if (response.isSuccessful) {
                        val articles = response.body()?.data?.filterNotNull() ?: emptyList()
                        lastSearchResult = articles
                        _articles.postValue(articles)
                    } else {
                        Log.e("ArticleViewModel", "Search response not successful: ${response.errorBody()?.string()}")
                        _articles.postValue(emptyList())
                    }
                } else {
                    Log.e("ArticleViewModel", "Token is missing!")
                }
            } catch (e: Exception) {
                Log.e("ArticleViewModel", "Error in searchArticles: ${e.message}")
                _articles.postValue(emptyList())
            }
        }
    }

    // Fungsi untuk memfilter artikel berdasarkan kategori
    fun filterArticlesByCategory(category: String) {
        val source = lastSearchResult ?: _articles.value ?: emptyList()
        val filteredArticles = source.filter { it.categories?.contains(category) == true }
        _articles.postValue(filteredArticles)
    }
}
