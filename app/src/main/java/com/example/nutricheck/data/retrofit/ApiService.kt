package com.example.nutricheck.data.retrofit

import com.example.nutricheck.data.response.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*


interface ApiService {
    @POST("auth/register")
    fun registerUser(
        @Body userRequest: RegisterRequest
    ): Call<RegisterResponse>

    @GET("news")
    fun getNews(): Call<ArticleResponse>

    @POST("auth/login")
    fun loginUser(
        @Body loginRequest: LoginRequest
    ): Call<LoginResponse>

    @PUT("users/{userId}/bmi")
    fun submitAssessment(
        @Path("userId") userId: String,
        @Body assessmentRequest: AssessmentRequest
    ): Call<AssessmentResponse>

    @GET("meals/nutrition")
    suspend fun getFoodNutrition(
        @Query("food") foodName: String
    ): FoodResponse

    @GET("users/{userId}")
    fun getUserBMI(
        @Path("userId") userId: String
    ): Call<UserResponse>

    @GET("meals/nutrition")
    fun getNutritionData(@Query("food") foodName: String): Call<FoodResponse>

    @GET("meals")
    fun searchMeals(
        @Query("search") foodName: String? = null
    ): Call<AddResponse>

    @POST("meals_histories")
    suspend fun submitMealHistory(
        @Body request: MealHistoryRequest
    ): MealHistoryResponse

    @POST("meals_histories/manual")
    fun addManualMeal(
        @Body addManualRequest: AddMealRequest
    ): Call<AddManualResponse>

    @GET("meals_histories/search")
    suspend fun getMealsHistories(
        @Query("date") date: String
    ): Response<HistoryResponse>

    @GET("meals_histories/search")
    suspend fun getDetailHistory(
        @Query("meal_type") mealType: String,
        @Query("date") date: String? = null
    ): DetailHistoryResponse

}
