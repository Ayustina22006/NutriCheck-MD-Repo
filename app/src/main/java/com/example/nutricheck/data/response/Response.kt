package com.example.nutricheck.data.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName




data class RegisterResponse(
	val status: Int,
	val message: String,
	val data: UserData
)

data class UserRequest(
	val username: String,
	val email: String,
	val password: String
)

@Parcelize
data class UserData(
	val userId: String
) : Parcelable

