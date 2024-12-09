package com.example.nutricheck.ui.scan

import androidx.room.*

@Entity(tableName = "captured_food_item")
data class CapturedFoodItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val imagePath: String,
    val mealid: String? = null,
    val foodName: String? = null
)
