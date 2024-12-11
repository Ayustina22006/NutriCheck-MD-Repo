package com.example.nutricheck.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class MealHistoryResponse(
    @SerializedName("status") val status: Int? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val mealHistory: MealHistory? = null
) : Parcelable

@Parcelize
data class MealHistory(
    @SerializedName("id") val id: String? = null,
    @SerializedName("total_calories") val totalCalories: Int? = null,
    @SerializedName("total_nutrition") val totalNutrition: TotalNutrition? = null,
    @SerializedName("meals_details") val mealsDetails: List<MealDetail>? = null,
    @SerializedName("userId") val userId: String? = null,
    @SerializedName("meal_type") val mealType: String? = null,
    @SerializedName("date") val date: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("updatedAt") val updatedAt: String? = null,
    @SerializedName("count") val count: Int? = null,
    @SerializedName("label") val label: String? = null
) : Parcelable

@Parcelize
data class TotalNutrition(
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
data class MealDetail(
    @SerializedName("id") val id: String? = null,
    @SerializedName("Calories") val calories: Int? = null,
    @SerializedName("Food Name") val foodName: String? = null,
    @SerializedName("Serving Size (grams)") val servingSize: Int? = null,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("userId") val userId: String? = null,
    @SerializedName("nutritions") val nutritionDetails: NutritionDetail? = null,
    @SerializedName("image") val image: String? = null,
    @SerializedName("updatedAt") val updatedAt: String? = null
) : Parcelable {

}

@Parcelize
data class NutritionDetail(
    @SerializedName("Calcium") val calcium: Double? = null,
    @SerializedName("Dietary Fiber") val dietaryFiber: Double? = null,
    @SerializedName("Iron") val iron: Double? = null,
    @SerializedName("Protein") val protein: Double? = null,
    @SerializedName("Vitamin A") val vitaminA: Double? = null,
    @SerializedName("Vitamin B") val vitaminB: Double? = null,
    @SerializedName("Vitamin C") val vitaminC: Double? = null,
    @SerializedName("Carbohydrate") val carbohydrate: Double? = null
) : Parcelable {
    fun toMap(): Map<String, Double> {
        return mapOf(
            "Calcium" to (calcium ?: 0.0),
            "Dietary Fiber" to (dietaryFiber ?: 0.0),
            "Iron" to (iron ?: 0.0),
            "Protein" to (protein ?: 0.0),
            "Vitamin A" to (vitaminA ?: 0.0),
            "Vitamin B" to (vitaminB ?: 0.0),
            "Vitamin C" to (vitaminC ?: 0.0),
            "Carbohydrate" to (carbohydrate ?: 0.0)
        )
    }
}

data class MealHistoryRequest(
    val mealIds: List<String>
)

