package com.example.nutricheck.ui.add

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.nutricheck.ViewModelFactory
import com.example.nutricheck.data.response.*
import com.example.nutricheck.databinding.ActivityAddBinding
import com.example.nutricheck.data.Result
import com.example.nutricheck.ui.nutrition.NutritionActivity

class AddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBinding
    private val addViewModel: AddViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeAddMealResult()

        binding.btnAddFood.setOnClickListener {
            validateAndAddFood()
        }

        binding.btnSaveAndCheckNutrition.setOnClickListener {
            validateAndAddFood()
            val intent = Intent(this, NutritionActivity::class.java)
            startActivity(intent)
        }

        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun validateAndAddFood() {
        val foodName = binding.etNamaMakanan.text.toString().trim()
        val servingSize = binding.etPorsi.text.toString().trim().toIntOrNull()
        val calories = binding.etKalori.text.toString().trim().toIntOrNull()
        val protein = binding.etProtein.text.toString().trim().toDoubleOrNull()
        val iron = binding.etIron.text.toString().trim().toDoubleOrNull()
        val dietaryFiber = binding.etDietaryFiber.text.toString().trim().toDoubleOrNull()
        val calcium = binding.etKalsium.text.toString().trim().toDoubleOrNull()
        val carbohydrate = binding.etKarbohidrat.text.toString().trim().toDoubleOrNull()
        val vitaminA = binding.etVitaminA.text.toString().trim().toIntOrNull()
        val vitaminB = binding.etVitaminB.text.toString().trim().toIntOrNull()
        val vitaminC = binding.etVitaminC.text.toString().trim().toDoubleOrNull()

        if (validateInput(foodName, servingSize, calories, protein, iron, dietaryFiber, calcium,carbohydrate, vitaminA, vitaminB, vitaminC)) {
            addViewModel.addManualMeal(
                foodName = foodName,
                servingSize = servingSize!!,
                calories = calories!!,
                protein = protein!!,
                iron = iron!!,
                dietaryFiber = dietaryFiber,
                calcium = calcium,
                carbohydrate = carbohydrate!!,
                vitaminA = vitaminA!!,
                vitaminB = vitaminB!!,
                vitaminC = vitaminC!!
            )
        }
    }

    private fun validateInput(
        foodName: String,
        servingSize: Int?,
        calories: Int?,
        protein: Double?,
        carbohydrate: Double?,
        iron: Double?,
        dietaryFiber: Double?,
        calcium: Double?,
        vitaminA: Int?,
        vitaminB: Int?,
        vitaminC: Double?
    ): Boolean {
        var isValid = true

        if (foodName.isEmpty()) {
            binding.etNamaMakanan.error = "Nama makanan harus diisi"
            isValid = false
        }

        if (servingSize == null) {
            binding.etPorsi.error = "Porsi harus diisi"
            isValid = false
        }

        if (calories == null) {
            binding.etKalori.error = "Kalori harus diisi"
            isValid = false
        }

        if (protein == null) {
            binding.etProtein.error = "Protein harus diisi"
            isValid = false
        }

        if (carbohydrate == null) {
            binding.etKarbohidrat.error = "Karbohidrat harus diisi"
            isValid = false
        }

        if (vitaminA == null) {
            binding.etVitaminA.error = "Vitamin A harus diisi"
            isValid = false
        }

        if (vitaminB == null) {
            binding.etVitaminB.error = "Vitamin B harus diisi"
            isValid = false
        }

        if (vitaminC == null) {
            binding.etVitaminC.error = "Vitamin C harus diisi"
            isValid = false
        }
        if (iron == null) {
            binding.etIron.error = "Iron harus diisi"
            isValid = false
        }
        if (dietaryFiber == null) {
            binding.etDietaryFiber.error = "Fiber gizi harus diisi"
            isValid = false
        }
        if (calcium == null) {
            binding.etKalsium.error = "Kalsium gizi harus diisi"
            isValid = false
        }

        return isValid
    }

    private fun observeAddMealResult() {
        addViewModel.addMealResult.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                     binding.progressBar.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    Toast.makeText(this, "Makanan berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                    finish()
                }
                is Result.Error -> {
                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}