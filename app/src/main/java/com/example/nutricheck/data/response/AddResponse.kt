package com.example.nutricheck.data.response

import com.google.gson.annotations.SerializedName

data class AddResponse(
	@SerializedName("data")
	val data: List<DataItem?>? = null,

	@SerializedName("count")
	val count: Int? = null,

	@SerializedName("message")
	val message: String? = null,

	@SerializedName("status")
	val status: Int? = null
)

data class DataItem(
	@SerializedName("id")
	val id: String? = null,

	@SerializedName("Calories")
	val calories: Int? = null,

	@SerializedName("Food Name")
	val foodName: String? = null,

	@SerializedName("Serving Size (grams)")
	val servingSizeGrams: Int? = null,

	@SerializedName("createdAt")
	val createdAt: String? = null,

	@SerializedName("userId")
	val userId: String? = null,

	@SerializedName("nutritions")
	val nutritions: Nutritions? = null,

	@SerializedName("updatedAt")
	val updatedAt: String? = null
)

data class Nutritions(
	@SerializedName("Vitamin A")
	val vitaminA: Int? = null,

	@SerializedName("Calcium")
	val calcium: Int? = null,

	@SerializedName("Vitamin B")
	val vitaminB: Int? = null,

	@SerializedName("Vitamin C")
	val vitaminC: Double? = null,

	@SerializedName("Dietary Fiber")
	val dietaryFiber: Double? = null,

	@SerializedName("Iron")
	val iron: Double? = null,

	@SerializedName("Carbohydrate")
	val carbohydrate: Double? = null,

	@SerializedName("Protein")
	val protein: Double? = null
)
