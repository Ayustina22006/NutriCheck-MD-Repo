package com.example.nutricheck.data.retrofit

import com.example.nutricheck.data.response.ArticleResponse
import com.example.nutricheck.data.response.ArticleSearch
import com.example.nutricheck.data.response.AssessmentRequest
import com.example.nutricheck.data.response.AssessmentResponse
import com.example.nutricheck.data.response.LoginRequest
import com.example.nutricheck.data.response.LoginResponse
import com.example.nutricheck.data.response.RegisterRequest
import com.example.nutricheck.data.response.RegisterResponse
import com.example.nutricheck.data.response.UserResponse
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

    @GET("news/title/:title")
    suspend fun getSearch(
        @Query("query") keyword: String
    ): Response<ArticleSearch>

    @POST("auth/login")
    fun loginUser(
        @Body loginRequest: LoginRequest
    ): Call<LoginResponse>

    @PUT("users/{userId}/bmi")
    fun submitAssessment(
        @Path("userId") userId: String,
        @Body assessmentRequest: AssessmentRequest
    ): Call<AssessmentResponse>

    @GET("users/{userId}")
    fun getUserBMI(
        @Path("userId") userId: String
    ): Call<UserResponse>


}
