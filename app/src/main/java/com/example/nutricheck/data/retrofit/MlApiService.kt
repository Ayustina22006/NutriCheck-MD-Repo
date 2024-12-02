package com.example.nutricheck.data.retrofit

import com.example.nutricheck.data.response.LabelResponse
import com.example.nutricheck.data.response.NutritionResponse
import okhttp3.MultipartBody
import retrofit2.*
import retrofit2.http.*

interface MlApiService {
    @Multipart
    @POST("predict")
    fun predictFood(@Part image: MultipartBody.Part): Call<LabelResponse>

    @GET("nutrition")
    fun getNutritionData(@Query("food") foodName: String): Call<NutritionResponse>
}
