package com.example.nutricheck.ui.history

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.nutricheck.data.UserRepository
import com.example.nutricheck.data.response.HistoryDataItem
import com.example.nutricheck.data.retrofit.ApiConfig
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

class HistoryViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _historyData = MutableLiveData<List<HistoryDataItem>>()
    val historyData: LiveData<List<HistoryDataItem>> = _historyData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    @SuppressLint("NewApi")
    fun fetchMealHistory(date: LocalDate) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val formattedDate = date.format(DateTimeFormatter.ISO_DATE)
                val token = userRepository.getToken()
                val response = ApiConfig.getApiService(token).getMealsHistories(formattedDate)

                if (response.isSuccessful) {
                    val historyItems = response.body()?.data ?: emptyList()
                    _historyData.value = historyItems.filterNotNull()
                    _isLoading.value = false
                } else {
                    _error.value = "No History Available\n ${response.message()}"
                    _historyData.value = emptyList()
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.localizedMessage}"
                _isLoading.value = false
            }
        }
    }
}