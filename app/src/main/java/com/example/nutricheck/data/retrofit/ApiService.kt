package com.example.nutricheck.data.retrofit

import com.example.nutricheck.data.response.RegisterResponse
import com.example.nutricheck.data.response.UserRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("auth/register")
    fun registerUser(
        @Body userRequest: UserRequest
    ): Call<RegisterResponse>
}
