package com.example.nutricheck.data.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class AssessmentResponse(
	val message: String? = null,
	val status: Int? = null
) : Parcelable

data class AssessmentRequest(
	val gender: String,
	val age: Int,
	val height: Int,
	val weight: Int,
	val activity: String
)





