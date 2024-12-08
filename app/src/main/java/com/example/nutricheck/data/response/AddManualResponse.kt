package com.example.nutricheck.data.response

import com.google.gson.annotations.SerializedName

data class AddManualResponse(
	@SerializedName("data")
	val data: AddData? = null,

	@SerializedName("message")
	val message: String? = null,

	@SerializedName("status")
	val status: Int? = null
)

data class AddData(
	@SerializedName("id")
	val id: String? = null,

	@SerializedName("total_calories")
	val totalCalories: Int? = null,

	@SerializedName("total_nutrition")
	val totalNutrition: TotalAddNutrition? = null,

	@SerializedName("meals_details")
	val mealsDetails: List<MealsDetailsItem>? = null,

	@SerializedName("userId")
	val userId: String? = null,

	@SerializedName("meal_type")
	val mealType: String? = null,

	@SerializedName("date")
	val date: String? = null,

	@SerializedName("createdAt")
	val createdAt: String? = null,

	@SerializedName("updatedAt")
	val updatedAt: String? = null,

	@SerializedName("count")
	val count: Int? = null,

	@SerializedName("label")
	val label: String? = null
)

data class MealsDetailsItem(
	@SerializedName("Calories")
	val calories: Int? = null,

	@SerializedName("Food Name")
	val foodName: String? = null,

	@SerializedName("Serving Size (grams)")
	val servingSizeGrams: Int? = null,

	@SerializedName("nutritions")
	val nutritions: AddManualNutritions? = null,

	@SerializedName("id")
	val id: String? = null,

	@SerializedName("userId")
	val userId: String? = null,

	@SerializedName("createdAt")
	val createdAt: String? = null,

	@SerializedName("updatedAt")
	val updatedAt: String? = null
)

data class AddManualNutritions(
	@SerializedName("Calcium")
	val calcium: Double? = null,

	@SerializedName("Dietary Fiber")
	val dietaryFiber: Double? = null,

	@SerializedName("Iron")
	val iron: Double? = null,

	@SerializedName("Protein")
	val protein: Double? = null,

	@SerializedName("Vitamin A")
	val vitaminA: Int? = null,

	@SerializedName("Vitamin B")
	val vitaminB: Int? = null,

	@SerializedName("Vitamin C")
	val vitaminC: Double? = null,

	@SerializedName("Carbohydrate")
	val carbohydrate: Double? = null
)

data class TotalAddNutrition(
	@SerializedName("Calcium")
	val calcium: Double? = null,

	@SerializedName("Dietary Fiber")
	val dietaryFiber: Double? = null,

	@SerializedName("Iron")
	val iron: Double? = null,

	@SerializedName("Protein")
	val protein: Double? = null,

	@SerializedName("Vitamin A")
	val vitaminA: Int? = null,

	@SerializedName("Vitamin B")
	val vitaminB: Int? = null,

	@SerializedName("Vitamin C")
	val vitaminC: Double? = null,

	@SerializedName("Carbohydrate")
	val carbohydrate: Double? = null
)

data class AddMealRequest(
	@SerializedName("Calories")
	val calories: Int,

	@SerializedName("Food Name")
	val foodName: String,

	@SerializedName("Serving Size (grams)")
	val servingSizeGrams: Int,

	@SerializedName("nutritions")
	val nutritions: AddManualNutritions
)
