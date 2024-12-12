package com.example.nutricheck.data.retrofit

import com.example.nutricheck.data.response.AddManualResponse
import com.example.nutricheck.data.response.AddMealRequest
import com.example.nutricheck.data.response.AddResponse
import com.example.nutricheck.data.response.ArticleResponse
import com.example.nutricheck.data.response.AssessmentRequest
import com.example.nutricheck.data.response.AssessmentResponse
import com.example.nutricheck.data.response.DetailHistoryResponse
import com.example.nutricheck.data.response.FoodResponse
import com.example.nutricheck.data.response.GoogleLoginRequest
import com.example.nutricheck.data.response.GoogleLoginResponse
import com.example.nutricheck.data.response.HistoryResponse
import com.example.nutricheck.data.response.LoginRequest
import com.example.nutricheck.data.response.LoginResponse
import com.example.nutricheck.data.response.MealHistoryRequest
import com.example.nutricheck.data.response.MealHistoryResponse
import com.example.nutricheck.data.response.RegisterRequest
import com.example.nutricheck.data.response.RegisterResponse
import com.example.nutricheck.data.response.UpdateBMIRequest
import com.example.nutricheck.data.response.UpdateUserRequest
import com.example.nutricheck.data.response.UserResponse
import com.example.nutricheck.data.response.updateUserResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

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

    @POST("auth/google/callback")
    fun googleLogin(@Body request: GoogleLoginRequest): Call<GoogleLoginResponse>

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

    @PUT("users/{userId}")
    fun updateUser(
        @Path("userId") userId: String,
        @Body updateRequest: UpdateUserRequest
    ): Call<updateUserResponse>

    @PUT("users/{userId}/bmi")
    fun updateBmiUser(
        @Path("userId") userId: String,
        @Body assessmentRequest: UpdateBMIRequest
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