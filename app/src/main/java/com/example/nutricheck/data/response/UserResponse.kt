package com.example.nutricheck.data.response

data class UserResponse(
    val id: String,
    val email: String,
    val password: String,
    val username: String,
    val role: String,
    val createdAt: String,
    val updatedAt: String,
    val bmi: BmiData
)

data class BmiData(
    val activity: String,
    val age: Int,
    val gender: String,
    val height: Int,
    val weight: Int
)

