package com.example.nutricheck.data

import android.content.Context
import com.example.nutricheck.auth.pref.UserModel
import com.example.nutricheck.auth.pref.UserPreference
import com.example.nutricheck.data.database.ArticleDao
import com.example.nutricheck.data.entity.ArticleEntity
import com.example.nutricheck.data.response.*
import com.example.nutricheck.data.retrofit.ApiConfig
import com.example.nutricheck.data.retrofit.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.await
import java.io.IOException

class UserRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference,
    private val articleDao: ArticleDao
) {

    fun updateBMI(userId: String, age: Int? = null, gender: String? = null, height: Int? = null, weight: Int? = null, activity: String? = null) = flow {
        emit(Result.Loading)
        try {
            // Ambil data BMI terkini dari server
            val userResponse = apiService.getUserBMI(userId).await()
            val currentBMI = userResponse.data?.bmi

            if (currentBMI != null) {
                // Update hanya atribut yang ingin diubah, gunakan nilai lama jika tidak diubah
                val updatedBMI = UpdateBMIRequest(
                    age = age ?: currentBMI.age,
                    gender = gender ?: currentBMI.gender,
                    height = height ?: currentBMI.height,
                    weight = weight ?: currentBMI.weight,
                    activity = activity ?: currentBMI.activity
                )

                // Kirim data BMI yang diperbarui ke API
                val updateResponse = apiService.updateBmiUser(userId, updatedBMI).await()

                if (updateResponse.status == 200) {
                    emit(Result.Success(updateResponse))
                } else {
                    emit(Result.Error(updateResponse.message ?: "Update failed"))
                }
            } else {
                emit(Result.Error("BMI data not found"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Update failed"))
        }
    }.flowOn(Dispatchers.IO)

    fun updateUser(userId: String, request: UpdateUserRequest) = flow {
        emit(Result.Loading)
        try {
            val updateResponse = apiService.updateUser(userId, request).await()

            // Check if update was successful
            if (updateResponse.status == 200) {
                // Fetch fresh user data after successful update
                val userResponse = apiService.getUserBMI(userId).await()
                emit(Result.Success(userResponse))
            } else {
                emit(Result.Error(updateResponse.message ?: "Update failed"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Update failed"))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getArticlesFromApi(): List<ArticleEntity> {
        return try {
            val token = getToken()
            val apiService = ApiConfig.getApiService(token)
            val response = apiService.getNews().execute().body()

            response?.data?.mapNotNull { article ->
                article?.let {
                    ArticleEntity(
                        id = it.id ?: "",
                        title = it.title ?: "",
                        description = it.description ?: "",
                        image = it.image,
                        url = it.url ?: "",
                        categories = it.categories?.joinToString(",") ?: ""
                    )
                }
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getArticles(): Flow<List<ArticleEntity>> = flow {
        // First, get local cached articles
        val localArticles = withContext(Dispatchers.IO) {
            articleDao.getAllArticles().first()
        }
        emit(localArticles)

        try {
            val apiArticles = getArticlesFromApi()

            if (apiArticles.isNotEmpty()) {
                withContext(Dispatchers.IO) {
                    articleDao.clearArticles()
                    articleDao.insertArticles(apiArticles)
                }

                emit(apiArticles)
            }
        } catch (e: Exception) {
            emit(localArticles)
        }
    }.flowOn(Dispatchers.IO)

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
                        isLogin = true,
                        userId = it.user?.id.orEmpty()
                    )
                )
            }
        } catch (e: IOException) {
            emit(Result.Error("Network error: ${e.message}"))
        } catch (e: Exception) {
            emit(Result.Error("Unexpected error: ${e.message}"))
        }
    }


    suspend fun searchMeals(foodName: String): AddResponse {
        return withContext(Dispatchers.IO) {
            apiService.searchMeals(foodName).execute().body() ?: AddResponse()
        }
    }



    fun fetchUserBMI(userId: String): Flow<Result<UserResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getUserBMI(userId).await()
            val bmiData = response.data?.bmi

            if (bmiData != null) {
                emit(Result.Success(response))
            } else {
                emit(Result.Error("Data BMI tidak ditemukan"))
            }
        } catch (e: IOException) {
            emit(Result.Error("Network error: ${e.message}"))
        } catch (e: Exception) {
            emit(Result.Error("Unexpected error: ${e.message}"))
        }
    }.catch { e ->
        emit(Result.Error("Flow caught an exception: ${e.localizedMessage}"))
    }

    suspend fun fetchDailyCalories(userId: String): Int? {
        return try {
            val token = getToken()
            val apiService = ApiConfig.getApiService(token)
            val result = apiService.getUserBMI(userId).await()

            val bmiData = result.data?.bmi
            if (bmiData != null && bmiData.weight in 30..300 && bmiData.height in 100..250) {
                calculateCalories(
                    gender = bmiData.gender.orEmpty(),
                    weight = bmiData.weight,
                    height = bmiData.height,
                    age = bmiData.age,
                    activityLevel = bmiData.activity.orEmpty()
                ).toInt()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun calculateCalories(
        gender: String,
        weight: Int?,
        height: Int?,
        age: Int?,
        activityLevel: String
    ): Double {
        val bmr = if (gender.lowercase() == "male") {
            (10 * weight!!) + (6.25 * height!!) - (5 * age!!) + 5
        } else {
            (10 * weight!!) + (6.25 * height!!) - (5 * age!!) - 16
        }

        val activityMultiplier = when (activityLevel.lowercase()) {
            "inactive" -> 1.2f
            "light activity" -> 1.375f
            "moderate activity" -> 1.55f
            "high activity" -> 1.725f
            "very high activity" -> 1.9f
            else -> 1.2f
        }

        return bmr * activityMultiplier
    }


    fun searchMeals(foodName: String?): AddResponse {
        return apiService.searchMeals(foodName).execute().body()!!
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
        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference,
            articleDao: ArticleDao
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService, userPreference, articleDao).also { instance = it }
            }
    }
}

class ResourceProvider(private val context: Context) {
    fun getString(resId: Int): String {
        return context.getString(resId)
    }
}

