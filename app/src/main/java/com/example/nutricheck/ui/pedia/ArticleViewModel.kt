package com.example.nutricheck.ui.pedia

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.nutricheck.auth.pref.UserModel
import com.example.nutricheck.data.UserRepository
import com.example.nutricheck.data.entity.ArticleEntity
import com.example.nutricheck.data.response.SearchDataItem
import com.example.nutricheck.data.retrofit.ApiConfig
import kotlinx.coroutines.launch
class ArticleViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _articles = MutableLiveData<List<ArticleEntity>>()
    val articles: LiveData<List<ArticleEntity>> = _articles

    private val _searcharticles = MutableLiveData<List<SearchDataItem>?>()
    val searcharticles: LiveData<List<SearchDataItem>?> = _searcharticles

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private var lastSearchResult: List<ArticleEntity>? = null

    // Untuk mendapatkan sesi pengguna saat ini
    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
    }

    // Mengambil semua artikel
    fun fetchArticles() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                userRepository.getArticles().collect { articles ->
                    _articles.postValue(articles)
                    lastSearchResult = articles // Simpan hasil pencarian terakhir
                }
            } catch (e: Exception) {
                _articles.postValue(emptyList())
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Mencari artikel berdasarkan keyword
    fun searchArticles(keyword: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val token = userRepository.getToken()
                val response = ApiConfig.getApiService(token).getSearch(keyword).execute()
                if (response.isSuccessful) {
                    val searchResults = response.body()?.data?.filterNotNull() ?: emptyList()
                    // Mapping dari ArticleDataItem ke ArticleEntity
                    val mappedResults = searchResults.map { item ->
                        ArticleEntity(
                            id = item.id ?: "",
                            title = item.title ?: "",
                            description = item.description ?: "",
                            image = item.image,
                            url = item.url ?: "",
                            categories = item.categories?.joinToString(",") ?: ""
                        )
                    }
                    lastSearchResult = mappedResults // Simpan hasil pencarian terakhir
                    _articles.postValue(mappedResults) // Perbarui LiveData
                } else {
                    _articles.postValue(emptyList())
                }
            } catch (e: Exception) {
                Log.e("ArticleViewModel", "Error fetching search articles: ${e.message}")
                _articles.postValue(emptyList())
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun filterArticlesByCategory(category: String) {
        val source = lastSearchResult ?: _articles.value ?: emptyList()
        val filteredArticles = source.filter { article ->
            article.categories.split(",").contains(category)
        }
        _articles.postValue(filteredArticles)
    }

}
