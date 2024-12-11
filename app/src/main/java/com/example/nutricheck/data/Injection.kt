package com.example.nutricheck.data

import android.content.Context
import com.example.nutricheck.auth.pref.UserPreference
import com.example.nutricheck.auth.pref.dataStore
import com.example.nutricheck.data.database.ArticleDatabase
import com.example.nutricheck.data.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)

        // Use runBlocking carefully, preferably in a background thread or coroutine scope
        val user = runBlocking { userPreference.getSession().first() }

        val apiService = ApiConfig.getApiService(user.token)
        val articleDao = ArticleDatabase.getDatabase(context).articleDao()

        return UserRepository.getInstance(apiService, userPreference, articleDao)
    }

    fun provideResourceProvider(context: Context): ResourceProvider {
        return ResourceProvider(context)
    }
}