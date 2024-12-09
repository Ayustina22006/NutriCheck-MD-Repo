package com.example.nutricheck.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutricheck.data.UserRepository
import com.example.nutricheck.data.response.DetailHistoryResponse
import com.example.nutricheck.data.retrofit.ApiConfig
import kotlinx.coroutines.launch

class HistoryDetailViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _detailHistory = MutableLiveData<DetailHistoryResponse?>()
    val detailHistory: LiveData<DetailHistoryResponse?> = _detailHistory

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun fetchDetailHistory(mealType: String, date: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val token = userRepository.getToken()
                val apiService = ApiConfig.getApiService(token)
                val response = apiService.getDetailHistory(mealType, date)
                _detailHistory.value = response
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}
