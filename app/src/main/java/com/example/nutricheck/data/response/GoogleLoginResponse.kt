package com.example.nutricheck.data.response


data class GoogleLoginRequest(
    val email: String,
    val username: String?
)

data class GoogleLoginResponse(
    val status: Int,
    val message: String,
    val user: UserDataGoogle?,
    val token: String?
)

data class UserDataGoogle(
    val id: String,
    val username: String,
    val email: String,
    val role: String,
    val createdAt: String,
    val updatedAt: String
)
