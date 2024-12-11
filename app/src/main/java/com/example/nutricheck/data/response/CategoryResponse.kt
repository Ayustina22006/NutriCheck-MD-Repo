package com.example.nutricheck.data.response

data class CategoryResponse(
	val data: List<DataItem?>? = null,
	val message: String? = null,
	val status: Int? = null
)

data class CategoryDataItem(
	val image: String? = null,
	val createdAt: String? = null,
	val description: String? = null,
	val id: String? = null,
	val categories: List<String?>? = null,
	val title: String? = null,
	val url: String? = null,
	val updatedAt: String? = null
)

