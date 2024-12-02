package com.example.nutricheck.ui.scan

import androidx.lifecycle.ViewModel
import com.example.nutricheck.data.UserRepository
import com.example.nutricheck.data.response.LabelResponse
import com.example.nutricheck.data.response.NutritionResponse
import com.example.nutricheck.data.retrofit.ApiConfig
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CameraViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun predictFood(
        imagePart: MultipartBody.Part,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
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
        onSuccess: (NutritionResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        val apiService = ApiConfig.getMLModelApiService()
        apiService.getNutritionData(foodName).enqueue(object : Callback<NutritionResponse> {
            override fun onResponse(call: Call<NutritionResponse>, response: Response<NutritionResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()!!)
                } else {
                    onError("Gagal mendapatkan data nutrisi: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<NutritionResponse>, t: Throwable) {
                onError("Gagal terhubung ke server nutrisi: ${t.message}")
            }
        })
    }
}
