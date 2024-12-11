package com.example.nutricheck.ui.scan

import android.app.Application
import androidx.lifecycle.*
import com.example.nutricheck.data.UserRepository
import com.example.nutricheck.data.database.AppDatabase
import com.example.nutricheck.data.entity.CapturedFoodItem
import com.example.nutricheck.data.response.*
import com.example.nutricheck.data.retrofit.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CameraViewModel(
    application: Application,
    private val userRepository: UserRepository
) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val capturedFoodItemDao = database.capturedFoodItemDao()

    val capturedFoodItems: LiveData<List<CapturedFoodItem>> = capturedFoodItemDao.getAllCapturedFoodItems()

    fun addCapturedFoodItem(
        imagePath: String, mealid: String, foodName: String
    ) {
        viewModelScope.launch {
            val capturedFoodItem = CapturedFoodItem(
                imagePath = imagePath,
                mealid = mealid,
                foodName = foodName
            )
            capturedFoodItemDao.insert(capturedFoodItem)
        }
    }

    fun clearAllCapturedFoodItems() {
        viewModelScope.launch {
            capturedFoodItemDao.deleteAll()
        }
    }

    fun submitMealHistory(
        mealIds: List<String>,
        onSuccess: (MealHistoryResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val apiService = ApiConfig.getApiService(userRepository.getToken())
                val request = MealHistoryRequest(mealIds = mealIds)
                val response = apiService.submitMealHistory(request)
                if (response.status == 201) { // Sesuaikan dengan status sukses API Anda
                    onSuccess(response)
                } else {
                    onError(response.message ?: "Failed to submit meal history")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error occurred")
            }
        }
    }


    suspend fun fetchAllMealIds(): List<String> {
        return capturedFoodItemDao.getAllMealIds()
    }

    fun predictFood(
        imagePart: MultipartBody.Part,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        // Implementasi sama seperti sebelumnya
        val apiService = ApiConfig.getMLModelApiService()
        apiService.predictFood(imagePart).enqueue(object : Callback<LabelResponse> {
            override fun onResponse(call: Call<LabelResponse>, response: Response<LabelResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val foodName = response.body()?.foodName ?: "Unknown"
                    onSuccess(foodName)
                } else {
                    onError("Prediction failed: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<LabelResponse>, t: Throwable) {
                onError("API call failed: ${t.message}")
            }
        })
    }

    fun fetchNutritionData(
        foodName: String,
        onSuccess: (FoodResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val token = userRepository.getToken()
                val apiService = ApiConfig.getApiService(token)
                val response = withContext(Dispatchers.IO) {
                    apiService.getNutritionData(foodName).execute()
                }
                if (response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()!!)
                } else {
                    onError("Gagal mendapatkan data nutrisi: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                onError("Terjadi kesalahan: ${e.message}")
            }
        }
    }
}