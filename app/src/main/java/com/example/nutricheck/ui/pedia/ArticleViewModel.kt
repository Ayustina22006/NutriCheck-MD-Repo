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

    private val _noArticlesAvailable = MutableLiveData<Boolean>()
    val noArticlesAvailable: LiveData<Boolean> = _noArticlesAvailable

    private var allArticles: List<ArticleEntity> = emptyList()

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
                    allArticles = articles
                    _articles.postValue(articles)
                    _noArticlesAvailable.postValue(articles.isEmpty())
                }
            } catch (e: Exception) {
                _articles.postValue(emptyList())
                _noArticlesAvailable.postValue(true)
                Log.e("ArticleViewModel", "Error fetching articles: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchArticles(keyword: String) {
        _isLoading.value = true
        try {
            val searchResults = allArticles.filter { article ->
                // Pencarian berdasarkan judul atau deskripsi (case-insensitive)
                article.title.lowercase().contains(keyword.lowercase()) ||
                        article.description.lowercase().contains(keyword.lowercase()) ||
                        article.categories.lowercase().contains(keyword.lowercase())
            }

            // Update artikel dengan hasil pencarian
            _articles.value = searchResults

            // Perbarui status artikel tidak tersedia
            _noArticlesAvailable.value = searchResults.isEmpty()

            // Tambahkan log untuk debugging
            Log.d("ArticleViewModel", "Search results for '$keyword': ${searchResults.size} articles found")
        } catch (e: Exception) {
            Log.e("ArticleViewModel", "Error searching articles: ${e.message}")
            _articles.value = emptyList()
            _noArticlesAvailable.value = true
        } finally {
            _isLoading.value = false
        }
    }

    // Filter artikel berdasarkan kategori
    fun filterArticlesByCategory(category: String) {
        _isLoading.value = true
        try {
            val filteredArticles = if (category.isEmpty()) {
                // Jika kategori kosong, kembalikan semua artikel
                allArticles
            } else {
                // Filter artikel berdasarkan kategori (case-insensitive)
                allArticles.filter { article ->
                    article.categories.lowercase().contains(category.lowercase())
                }
            }

            _articles.value = filteredArticles
            // Update noArticlesAvailable based on filtered results
            _noArticlesAvailable.value = filteredArticles.isEmpty()
        } catch (e: Exception) {
            Log.e("ArticleViewModel", "Error filtering articles: ${e.message}")
            _articles.value = emptyList()
            _noArticlesAvailable.value = true
        } finally {
            _isLoading.value = false
        }
    }
}