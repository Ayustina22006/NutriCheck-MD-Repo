package com.example.nutricheck.data.response

data class AuthResponse(
	val data: List<DataItem?>? = null,
	val message: String? = null,
	val status: Int? = null
)

data class DataItem(
	val password: String? = null,
	val id: String? = null,
	val email: String? = null,
	val username: String? = null,
)

