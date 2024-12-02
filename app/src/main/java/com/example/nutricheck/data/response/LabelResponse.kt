package com.example.nutricheck.data.response

import com.google.gson.annotations.SerializedName

data class LabelResponse(
	@SerializedName("food_name")
	val foodName: String? = null,

	@SerializedName("message")
	val message: String? = null,

	@SerializedName("status")
	val status: Int? = null
)
