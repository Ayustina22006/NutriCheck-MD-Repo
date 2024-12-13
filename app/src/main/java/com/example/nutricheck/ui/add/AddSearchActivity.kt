package com.example.nutricheck.ui.add

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.nutricheck.MainActivity
import com.example.nutricheck.ViewModelFactory
import com.example.nutricheck.data.response.Food
import com.example.nutricheck.databinding.ActivityAddSearchBinding

class AddSearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddSearchBinding
    private lateinit var viewModel: AddSearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[AddSearchViewModel::class.java]

        @Suppress("DEPRECATION") val food = intent.getParcelableExtra<Food>(EXTRA_FOOD)
        food?.let {
            viewModel.setFoodData(it)
            setupUI(it)
        }

        setupListeners()
        observeViewModel()
    }

    @SuppressLint("SetTextI18n")
    private fun setupUI(food: Food) {
        binding.apply {
            Glide.with(this@AddSearchActivity)
                .load(food.image)
                .into(imageFood)

            textFoodName.text = food.foodName
            textFoodDetail.text = "${food.servingSize} g | ${food.calories} kkal"

            textFoodCategory.text = viewModel.findHighestNutrition(food)

            etNamaMakanan.setText(food.foodName)
            etPorsi.setText(food.servingSize.toString())
            etKalori.setText(food.calories.toString())

            etDietaryFiber.setText(food.nutritions.dietaryFiber.toString())
            etKalsium.setText(food.nutritions.calcium.toString())
            etIron.setText(food.nutritions.iron.toString())
            etProtein.setText(food.nutritions.protein.toString())
            etKarbohidrat.setText(food.nutritions.carbohydrate.toString())
            etVitaminA.setText(food.nutritions.vitaminA.toString())
            etVitaminB.setText(food.nutritions.vitaminB.toString())
            etVitaminC.setText(food.nutritions.vitaminC.toString())
        }
    }

    private fun setupListeners() {
        binding.apply {
            backButton.setOnClickListener {
                val intent = Intent(this@AddSearchActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { isLoading ->
        }

        viewModel.errorMessage.observe(this) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                viewModel.resetErrorMessage()
            }
        }
    }

    companion object {
        const val EXTRA_FOOD = "extra_food"
    }
}