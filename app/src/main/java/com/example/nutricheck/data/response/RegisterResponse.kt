package com.example.nutricheck.data.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable


data class RegisterResponse(
	val status: Int,
	val message: String,
	val data: UserData
)

data class RegisterRequest(
	val username: String,
	val email: String,
	val password: String
)

@Parcelize
data class UserData(
	val userId: String,
	val token: String // Menambahkan token
) : Parcelable

