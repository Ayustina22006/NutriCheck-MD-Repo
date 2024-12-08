package com.example.nutricheck.data.response


import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MealHistoryResponse(
    val status: Int? = null,
    val message: String? = null,
    val mealHistory: MealHistory? = null
) : Parcelable

@Parcelize
data class MealHistory(
    val id: String? = null,
    val totalCalories: Int? = null,
    val totalNutrition: TotalNutrition? = null,
    val mealsDetails: List<MealDetail>? = null,
    val userId: String? = null,
    val mealType: String? = null,
    val date: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val count: Int? = null,
    val label: String? = null
) : Parcelable

@Parcelize
data class TotalNutrition(
    val calcium: Int? = null,
    val dietaryFiber: Double? = null,
    val iron: Double? = null,
    val protein: Double? = null,
    val vitaminA: Int? = null,
    val vitaminB: Int? = null,
    val vitaminC: Double? = null,
    val carbohydrate: Double? = null
) : Parcelable

@Parcelize
data class MealDetail(
    val id: String? = null,
    val calories: Int? = null,
    val foodName: String? = null,
    val servingSize: Int? = null,
    val createdAt: String? = null,
    val userId: String? = null,
    val nutritionDetails: NutritionDetail? = null,
    val updatedAt: String? = null
) : Parcelable

@Parcelize
data class NutritionDetail(
    val calcium: Int? = null,
    val dietaryFiber: Double? = null,
    val iron: Double? = null,
    val protein: Double? = null,
    val vitaminA: Int? = null,
    val vitaminB: Int? = null,
    val vitaminC: Double? = null,
    val carbohydrate: Double? = null
) : Parcelable

data class MealHistoryRequest(
    val mealIds: List<String>
)
