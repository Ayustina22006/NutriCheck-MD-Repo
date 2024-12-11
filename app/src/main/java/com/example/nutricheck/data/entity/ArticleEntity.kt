package com.example.nutricheck.data.entity

import androidx.lifecycle.LiveData
import androidx.room.*

@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val image: String?,
    val url: String,
    val categories: String
)

