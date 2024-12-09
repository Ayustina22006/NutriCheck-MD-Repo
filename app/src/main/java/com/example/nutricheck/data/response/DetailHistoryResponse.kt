package com.example.nutricheck.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class DetailHistoryResponse(
	@SerializedName("status") val status: Int? = null,  
	@SerializedName("message") val message: String? = null,
	@SerializedName("data") val data: List<DataHistoryItem>? = null
) : Parcelable

@Parcelize
data class DataHistoryItem(
	@SerializedName("id") val id: String? = null,
	@SerializedName("total_calories") val totalCalories: Int? = null,  
	@SerializedName("total_nutrition") val totalNutrition: TotalNutritionDetail? = null,
	@SerializedName("meals_details") val mealsDetails: List<MealsDetailsHistoryItem>? = null,
	@SerializedName("userId") val userId: String? = null,
	@SerializedName("meal_type") val mealType: String? = null,
	@SerializedName("date") val date: String? = null,
	@SerializedName("createdAt") val createdAt: String? = null,
	@SerializedName("updatedAt") val updatedAt: String? = null,
	@SerializedName("count") val count: Int? = null,  
	@SerializedName("label") val label: String? = null
) : Parcelable

@Parcelize
data class MealsDetailsHistoryItem(
	@SerializedName("id") val id: String? = null,
	@SerializedName("Calories") val calories: Int? = null,  
	@SerializedName("Food Name") val foodName: String? = null,
	@SerializedName("Serving Size (grams)") val servingSize: Int? = null,  
	@SerializedName("createdAt") val createdAt: String? = null,
	@SerializedName("userId") val userId: String? = null,
	@SerializedName("nutritions") val nutritions: NutritionDetailHistory? = null,
	@SerializedName("updatedAt") val updatedAt: String? = null
) : Parcelable

@Parcelize
data class NutritionDetailHistory(
	@SerializedName("Calcium") val calcium: Double? = null,  
	@SerializedName("Dietary Fiber") val dietaryFiber: Double? = null,  
	@SerializedName("Iron") val iron: Double? = null,  
	@SerializedName("Protein") val protein: Double? = null,  
	@SerializedName("Vitamin A") val vitaminA: Double? = null,  
	@SerializedName("Vitamin B") val vitaminB: Double? = null,  
	@SerializedName("Vitamin C") val vitaminC: Double? = null,  
	@SerializedName("Carbohydrate") val carbohydrate: Double? = null  
) : Parcelable

@Parcelize
data class TotalNutritionDetail(
	@SerializedName("Calcium") val calcium: Double? = null,  
	@SerializedName("Dietary Fiber") val dietaryFiber: Double? = null,  
	@SerializedName("Iron") val iron: Double? = null,  
	@SerializedName("Protein") val protein: Double? = null,  
	@SerializedName("Vitamin A") val vitaminA: Double? = null,  
	@SerializedName("Vitamin B") val vitaminB: Double? = null,  
	@SerializedName("Vitamin C") val vitaminC: Double? = null,  
	@SerializedName("Carbohydrate") val carbohydrate: Double? = null  
) : Parcelable
