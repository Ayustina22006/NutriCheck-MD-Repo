package com.example.nutricheck.data.retrofit

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.nutricheck.ui.scan.CapturedFoodItem

@Dao
interface AppDao {
    @Insert
    suspend fun insert(capturedFoodItem: CapturedFoodItem)

    @Query("SELECT * FROM captured_food_item")
    fun getAllCapturedFoodItems(): LiveData<List<CapturedFoodItem>>

    @Delete
    suspend fun delete(capturedFoodItem: CapturedFoodItem)

    @Query("DELETE FROM captured_food_item")
    suspend fun deleteAll()

    @Query("SELECT mealid FROM captured_food_item WHERE mealid IS NOT NULL")
    suspend fun getAllMealIds(): List<String>

}