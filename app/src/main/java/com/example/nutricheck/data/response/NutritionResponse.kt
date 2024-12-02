package com.example.nutricheck.data.response

import com.google.gson.annotations.SerializedName

data class NutritionResponse(
	@SerializedName("Calories") val calories: Int,
	@SerializedName("Food ID") val foodId: Int,
	@SerializedName("Food Name") val foodName: String,
	@SerializedName("Serving Size") val servingSize: String,
	@SerializedName("Serving Size (grams)") val servingSizeGrams: Double,
	@SerializedName("nutritions") val nutritions: Nutritions
)

data class Nutritions(
	@SerializedName("Calcium") val calcium: Int,
	@SerializedName("Dietary Fiber") val dietaryFiber: Double,
	@SerializedName("Iron") val iron: Double,
	@SerializedName("Protein") val protein: Double,
	@SerializedName("Total Carbohydrate") val totalCarbohydrate: Double,
	@SerializedName("Vitamin A") val vitaminA: Int,
	@SerializedName("Vitamin B") val vitaminB: Int,
	@SerializedName("Vitamin C") val vitaminC: Double
)


