package com.example.nutricheck.data.response

data class UserResponse(
    val status: Int?,
    val message: String?,
    val data: DataUser?
)


data class DataUser(
    val id: String?,
    val email: String?,
    val password: String?,
    val username: String?,
    val role: String?,
    val createdAt: String?,
    val updatedAt: String?,
    val bmi: BMIData?
)

data class BMIData(
    val activity: String?,
    val age: Int?,
    val gender: String?,
    val height: Int?,
    val weight: Int?
)


