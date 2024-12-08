package com.example.nutricheck.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FoodResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<Food>
) : Parcelable

@Parcelize
data class Food(
    @SerializedName("id") val id: String,
    @SerializedName("Calories") val calories: Double,
    @SerializedName("Food Name") val foodName: String,
    @SerializedName("Serving Size (grams)") val servingSize: Int,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("userId") val userId: String,
    @SerializedName("nutritions") val nutritions: Nutrition,
    @SerializedName("updatedAt") val updatedAt: String
) : Parcelable

@Parcelize
data class Nutrition(
    @SerializedName("Calcium") val calcium: Double,
    @SerializedName("Dietary Fiber") val dietaryFiber: Double,
    @SerializedName("Iron") val iron: Double,
    @SerializedName("Protein") val protein: Double,
    @SerializedName("Vitamin A") val vitaminA: Double,
    @SerializedName("Vitamin B") val vitaminB: Double,
    @SerializedName("Vitamin C") val vitaminC: Double,
    @SerializedName("Carbohydrate") val carbohydrate: Double
) : Parcelable