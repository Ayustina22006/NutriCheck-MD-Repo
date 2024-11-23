package com.example.nutricheck.data.response




data class ArticleResponse(
	val data: List<ArticleDataItem?>? = null,
	val message: String? = null,
	val status: Int? = null
)


data class ArticleDataItem(
	val image: String? = null,
	val createdAt: String? = null,
	val description: String? = null,
	val id: String? = null,
	val title: String? = null,
	val category: List<String?>? = null,
	val url: String? = null,
	val updatedAt: String? = null
)