package com.example.nutricheck.ui.nutrition

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nutricheck.R
import com.example.nutricheck.ViewModelFactory
import com.example.nutricheck.data.response.MealHistoryResponse
import com.example.nutricheck.databinding.ActivityNutritionBinding
import com.example.nutricheck.ui.NutritionDetailsAdapter

class NutritionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNutritionBinding
    private lateinit var viewModel: NutritionViewModel
    private lateinit var nutritionDetailsAdapter: NutritionDetailsAdapter

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNutritionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the MealHistoryResponse passed from previous screen
        @Suppress("DEPRECATION") val mealHistoryResponse = intent.getParcelableExtra<MealHistoryResponse>("MEAL_HISTORY_RESPONSE")

        // Setup ViewModel
        val factory = ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[NutritionViewModel::class.java]

        // Load meal history data
        mealHistoryResponse?.let { viewModel.loadMealHistory(it) }

        // Setup RecyclerView
        nutritionDetailsAdapter = NutritionDetailsAdapter { mealDetail ->
            // Navigasi ke halaman detail
            val intent = Intent(this, NutritionDetailActivity::class.java).apply {
                putExtra("MEAL_DETAIL", mealDetail)
            }
            startActivity(intent)
        }

        binding.rvViewNutrition.apply {
            layoutManager = LinearLayoutManager(this@NutritionActivity)
            adapter = nutritionDetailsAdapter
        }

        binding.piringku.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = android.net.Uri.parse("https://ayosehat.kemkes.go.id/isi-piringku-pedoman-makan-kekinian-orang-indonesia")
            startActivity(intent)
            true
        }

        mealHistoryResponse?.mealHistory?.mealsDetails?.let {
            nutritionDetailsAdapter.submitList(it)
        }
        // Observe data
        viewModel.mealDetails.observe(this) { mealDetails ->
            nutritionDetailsAdapter.submitList(mealDetails)
        }

        viewModel.mealHistory.observe(this) { mealHistory ->
            mealHistory?.totalNutrition?.let { nutrition ->
                binding.nutritionProtein.text =
                    "${viewModel.formatNutritionValue(nutrition.protein)} (g)"
                binding.nutritionCalories.text = "${mealHistory.totalCalories} (kcal)"
                binding.nutritionDietaryFiber.text =
                    "${viewModel.formatNutritionValue(nutrition.dietaryFiber)} (g)"
                binding.nutritionCalcium.text =
                    "${viewModel.formatNutritionValue(nutrition.calcium)} (g)"
                binding.nutritionIron.text = "${viewModel.formatNutritionValue(nutrition.iron)} (g)"
                binding.nutritionCarbohydrate.text =
                    "${viewModel.formatNutritionValue(nutrition.carbohydrate)} (g)"
                binding.nutritionVitaminA.text =
                    "\n${viewModel.formatNutritionValue(nutrition.vitaminA)} (g)"
                binding.nutritionVitaminB.text =
                    "${viewModel.formatNutritionValue(nutrition.vitaminB)} (g)"
                binding.nutritionVitaminC.text =
                    "${viewModel.formatNutritionValue(nutrition.vitaminC)} (g)"

                // Kelompokkan nutrisi
                val laukPauk = listOf(
                    nutrition.protein,
                    nutrition.calcium,
                    nutrition.iron
                )

                val makananPokok = listOf(
                    nutrition.carbohydrate
                )

                val sayur = listOf(
                    nutrition.vitaminA,
                    nutrition.vitaminB,
                    nutrition.vitaminC,
                    nutrition.dietaryFiber
                )

                fun determineGroupStatus(group: List<Double?>, thresholds: List<Double>): String {
                    // Ganti nilai null dengan 0.0 agar dapat dijumlahkan
                    val totalGroup = group.map { it ?: 0.0 }.sum()
                    return when {
                        totalGroup < thresholds[0] -> "KURANG"
                        totalGroup > thresholds[1] -> "BERLEBIH"
                        else -> "IDEAL"
                    }
                }


                // Tentukan status masing-masing kelompok
                // Sesuaikan threshold dengan kebutuhan Anda
                val laukPaukStatus = determineGroupStatus(laukPauk, listOf(30.0, 60.0))
                val makananPokokStatus = determineGroupStatus(makananPokok, listOf(50.0, 100.0))
                val sayurStatus = determineGroupStatus(sayur, listOf(40.0, 80.0))

                // Tentukan kalori
                val calories = mealHistory.totalCalories

                // Tentukan pesan berdasarkan kondisi
                val alertMessage = when {
                    calories!! < 500 -> when {
                        sayurStatus == "KURANG" && laukPaukStatus == "KURANG" && makananPokokStatus == "KURANG" ->
                            "Yuk, tingkatkan asupan kalori Anda agar tubuh mendapat energi yang cukup! Cobalah menambahkan lebih banyak sayuran segar. Tambahkan lauk pauk seperti telur, ikan, atau tahu tempe. Jangan lupa tambahkan nasi atau makanan pokok lainnya untuk energi."

                        sayurStatus == "IDEAL" && laukPaukStatus == "KURANG" && makananPokokStatus == "KURANG" ->
                            "Yuk, tingkatkan asupan kalori Anda agar tubuh mendapat energi yang cukup! Porsi sayur Anda sudah bagus, pertahankan ya! Tambahkan lauk pauk seperti telur, ikan, atau tahu tempe. Jangan lupa tambahkan nasi atau makanan pokok lainnya untuk energi."

                        sayurStatus == "BERLEBIH" && laukPaukStatus == "KURANG" && makananPokokStatus == "KURANG" ->
                            "Yuk, tingkatkan asupan kalori Anda agar tubuh mendapat energi yang cukup! Meskipun sayuran sudah banyak, coba imbangi juga dengan lauk pauk dan makanan pokok. Tambahkan lauk pauk seperti telur, ikan, atau tahu tempe. Jangan lupa tambahkan nasi atau makanan pokok lainnya."

                        sayurStatus == "KURANG" && laukPaukStatus == "IDEAL" && makananPokokStatus == "KURANG" ->
                            "Yuk, tingkatkan asupan kalori Anda agar tubuh mendapat energi yang cukup! Cobalah menambahkan lebih banyak sayuran segar. Porsi lauk pauk Anda sudah pas! Jangan lupa tambahkan nasi atau makanan pokok lainnya untuk energi."

                        else -> "Yuk, tingkatkan asupan kalori Anda agar tubuh mendapat energi yang cukup!"
                    }

                    calories.toDouble() in 500.0..700.0 ->
                        "Selamat! Asupan kalori Anda sudah ideal. Porsi sayur, lauk pauk, dan makanan pokok sudah seimbang!"

                    calories.toDouble() in 701.0..800.0 -> when {
                        sayurStatus == "BERLEBIH" && laukPaukStatus == "BERLEBIH" && makananPokokStatus == "BERLEBIH" ->
                            "Asupan kalori Anda sedikit melebihi kebutuhan. Kurangi penggunaan saus pada sayuran. Kurangi porsi lauk pauk dan pilih yang tidak berlemak. Kurangi porsi nasi atau makanan pokok."

                        sayurStatus == "KURANG" && laukPaukStatus == "BERLEBIH" && makananPokokStatus == "BERLEBIH" ->
                            "Asupan kalori Anda sedikit melebihi kebutuhan. Tambahkan sayuran segar dan kurangi porsi lainnya. Kurangi porsi lauk pauk dan pilih yang tidak berlemak. Kurangi porsi nasi atau makanan pokok."

                        sayurStatus == "IDEAL" && laukPaukStatus == "BERLEBIH" && makananPokokStatus == "BERLEBIH" ->
                            "Asupan kalori Anda sedikit melebihi kebutuhan. Porsi sayur sudah bagus. Kurangi porsi lauk pauk dan pilih yang tidak berlemak. Kurangi porsi nasi atau makanan pokok."

                        else -> "Asupan kalori Anda sedikit melebihi kebutuhan."
                    }

                    calories > 800 -> when {
                        sayurStatus == "BERLEBIH" && laukPaukStatus == "BERLEBIH" && makananPokokStatus == "BERLEBIH" ->
                            "Asupan kalori Anda terlalu tinggi! Kurangi saus pada sayuran. Kurangi drastis porsi lauk pauk, pilih yang rendah lemak. Kurangi porsi nasi atau makanan pokok secara signifikan."

                        sayurStatus == "KURANG" && laukPaukStatus == "BERLEBIH" && makananPokokStatus == "BERLEBIH" ->
                            "Asupan kalori Anda terlalu tinggi! Tambahkan sayuran segar. Kurangi drastis porsi lauk pauk, pilih yang rendah lemak. Kurangi porsi nasi atau makanan pokok secara signifikan."

                        sayurStatus == "IDEAL" && laukPaukStatus == "BERLEBIH" && makananPokokStatus == "BERLEBIH" ->
                            "Asupan kalori Anda terlalu tinggi! Pertahankan porsi sayur. Kurangi drastis porsi lauk pauk, pilih yang rendah lemak. Kurangi porsi nasi atau makanan pokok secara signifikan."

                        else -> "Asupan kalori Anda terlalu tinggi!"
                    }

                    else -> "Tidak dapat menentukan status asupan kalori"
                }

                // Update teks binding
                binding.textKalori.text = alertMessage
                when {
                    calories < 500 -> {
                        binding.profilePlaceholder.setImageResource(R.drawable.kurang)
                        binding.textAlert.text = binding.root.context.getString(R.string.alert_kurang)
                    }
                    calories in 500..700 -> {
                        binding.profilePlaceholder.setImageResource(R.drawable.ideal)
                        binding.textAlert.text = binding.root.context.getString(R.string.alert_ideal)
                    }
                    calories in 701..800 -> {
                        binding.profilePlaceholder.setImageResource(R.drawable.berlebih)
                        binding.textAlert.text = binding.root.context.getString(R.string.alert_berlebih)
                    }
                    calories > 800 -> {
                        binding.profilePlaceholder.setImageResource(R.drawable.sangat_berlebih)
                        binding.textAlert.text = binding.root.context.getString(R.string.alert_berlebihan)
                    }
                }
            }
        }

        // Back button
        binding.backButton.setOnClickListener { finish() }
    }
}