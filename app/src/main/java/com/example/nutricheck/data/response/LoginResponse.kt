package com.example.nutricheck.data.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class LoginResponse(
	val data: Data? = null,
	val message: String? = null,
	val status: Int? = null
) : Parcelable

@Parcelize
data class Bmi(
	val gender: String? = null,
	val weight: Int? = null,
	val age: Int? = null,
	val height: Int? = null
) : Parcelable

@Parcelize
data class User(
	val createdAt: String? = null,
	val password: String? = null,
	val role: String? = null,
	val id: String? = null,
	val email: String? = null,
	val username: String? = null,
	val updatedAt: String? = null,
	val bmi: Bmi? = null
) : Parcelable

@Parcelize
data class Data(
	val user: User? = null,
	val token: String? = null
) : Parcelable

data class LoginRequest(
	val email: String,
	val password: String
)