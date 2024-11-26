package com.example.nutricheck.data

import android.content.Context
import com.example.nutricheck.auth.pref.UserPreference
import com.example.nutricheck.auth.pref.dataStore
import com.example.nutricheck.data.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { userPreference.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return UserRepository.getInstance(apiService, userPreference)
    }
}
