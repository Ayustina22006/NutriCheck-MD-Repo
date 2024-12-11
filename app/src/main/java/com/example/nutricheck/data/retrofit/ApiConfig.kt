package com.example.nutricheck.data.retrofit

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import java.util.TimeZone

class ApiConfig {
    companion object {
        private const val BASE_URL = "https://nutricheck-200676161700.asia-southeast1.run.app/"
        private const val ML_MODEL_BASE_URL = "https://api-model-200676161700.asia-southeast2.run.app/"

        fun getApiService(token: String = ""): ApiService {
            val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

            // Interceptor untuk Authorization
            val authInterceptor = Interceptor { chain ->
                val request = chain.request()
                val tokenHeader = if (token.isNotEmpty()) "Bearer $token" else null
                val modifiedRequest = request.newBuilder().apply {
                    tokenHeader?.let { addHeader("Authorization", it) }
                }.build()
                chain.proceed(modifiedRequest)
            }

            val timezoneInterceptor = Interceptor { chain ->
                val request = chain.request()
                val modifiedRequest = request.newBuilder()
                    .addHeader("X-Timezone", TimeZone.getDefault().id)
                    .build()
                chain.proceed(modifiedRequest)
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authInterceptor)
                .addInterceptor(timezoneInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit.create(ApiService::class.java)
        }

        fun getMLModelApiService(): MlApiService {
            val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(ML_MODEL_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit.create(MlApiService::class.java)
        }
    }
}

