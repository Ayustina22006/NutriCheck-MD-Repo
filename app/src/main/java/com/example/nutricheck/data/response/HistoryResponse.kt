package com.example.nutricheck.data.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class HistoryResponse(
	@SerializedName("data")
	val data: List<HistoryDataItem?>? = null,
	@SerializedName("message")
	val message: String? = null,
	@SerializedName("status")
	val status: Int? = null
) : Parcelable

@Parcelize
data class MealsHistoryDetailsItem(
	@SerializedName("createdAt")
	val createdAt: String? = null,
	@SerializedName("Serving Size (grams)")
	val servingSizeGrams: Float? = null,
	@SerializedName("Food Name")
	val foodName: String? = null,
	@SerializedName("id")
	val id: String? = null,
	@SerializedName("userId")
	val userId: String? = null,
	@SerializedName("Calories")
	val calories: Float? = null,
	@SerializedName("nutritions")
	val nutritions: NutritionsHistory? = null,
	@SerializedName("updatedAt")
	val updatedAt: String? = null
) : Parcelable

@Parcelize
data class TotalHistoryNutrition(
	@SerializedName("Vitamin A")
	val vitaminA: Float? = null,
	@SerializedName("Calcium")
	val calcium: Float? = null,
	@SerializedName("Vitamin B")
	val vitaminB: Float? = null,
	@SerializedName("Vitamin C")
	val vitaminC: Float? = null,
	@SerializedName("Dietary Fiber")
	val dietaryFiber: Float? = null,
	@SerializedName("Iron")
	val iron: Float? = null,
	@SerializedName("Carbohydrate")
	val carbohydrate: Float? = null,
	@SerializedName("Protein")
	val protein: Float? = null
) : Parcelable

@Parcelize
data class HistoryDataItem(
	@SerializedName("date")
	val date: String? = null,
	@SerializedName("createdAt")
	val createdAt: String? = null,
	@SerializedName("total_nutrition")
	val totalNutrition: TotalHistoryNutrition? = null,
	@SerializedName("meal_type")
	val mealType: String? = null,
	@SerializedName("total_calories")
	val totalCalories: Int? = null,
	@SerializedName("count")
	val count: Int? = null,
	@SerializedName("meals_details")
	val mealsDetails: List<MealsHistoryDetailsItem?>? = null,
	@SerializedName("id")
	val id: String? = null,
	@SerializedName("label")
	val label: String? = null,
	@SerializedName("userId")
	val userId: String? = null,
	@SerializedName("updatedAt")
	val updatedAt: String? = null
) : Parcelable

@Parcelize
data class NutritionsHistory(
	@SerializedName("Calcium")
	val calcium: Double? = null,
	@SerializedName("Dietary Fiber")
	val dietaryFiber: Double? = null,
	@SerializedName("Iron")
	val iron: Double? = null,
	@SerializedName("Protein")
	val protein: Double? = null,
	@SerializedName("Vitamin A")
	val vitaminA: Double? = null,
	@SerializedName("Vitamin B")
	val vitaminB: Double? = null,
	@SerializedName("Vitamin C")
	val vitaminC: Double? = null,
	@SerializedName("Carbohydrate")
	val carbohydrate: Double? = null
) : Parcelable

data class NutritionItem(
	val name: String,
	val amount: String
)
