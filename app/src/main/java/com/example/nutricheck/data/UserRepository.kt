package com.example.nutricheck.data

import com.example.nutricheck.auth.pref.UserModel
import com.example.nutricheck.auth.pref.UserPreference
import com.example.nutricheck.data.response.LoginRequest
import com.example.nutricheck.data.response.LoginResponse
import com.example.nutricheck.data.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import retrofit2.await
import java.io.IOException

class UserRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {
    fun login(email: String, password: String): Flow<Result<LoginResponse>> = flow {
        emit(Result.Loading)
        try {
            val loginRequest = LoginRequest(email, password)
            val response = apiService.loginUser(loginRequest).await()
            emit(Result.Success(response))

            response.data?.let {
                saveSession(
                    UserModel(
                        email = it.user?.email.orEmpty(),
                        token = it.token.orEmpty(),
                        isLogin = true
                    )
                )
            }
        } catch (e: IOException) {
            emit(Result.Error("Network error: ${e.message}"))
        } catch (e: Exception) {
            emit(Result.Error("Unexpected error: ${e.message}"))
        }
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    suspend fun getToken(): String {
        val token = userPreference.getSession().first().token
        if (token.isEmpty()) {
            throw IllegalStateException("Token tidak ditemukan. Pengguna mungkin belum login.")
        }
        return token
    }



    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(apiService: ApiService, userPreference: UserPreference): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService, userPreference).also { instance = it }
            }
    }
}
