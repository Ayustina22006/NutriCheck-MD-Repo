package com.example.nutricheck.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nutricheck.ViewModelFactory
import com.example.nutricheck.databinding.FragmentHomeBinding
import com.example.nutricheck.ui.ArticleAdapter
import com.example.nutricheck.data.response.Food
import com.example.nutricheck.ui.add.AddActivity
import com.example.nutricheck.ui.add.AddSearchActivity
import androidx.appcompat.app.AlertDialog
import com.example.nutricheck.R
import com.example.nutricheck.ui.NutritionAdapter
import com.example.nutricheck.databinding.AlertBinding
import com.example.nutricheck.ui.scan.ScanActivity
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var articleAdapter: ArticleAdapter
    private var alertDialog: AlertDialog? = null
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var isActionBarHidden = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.hide()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!isActionBarHidden) {
            (activity as? AppCompatActivity)?.supportActionBar?.hide()
            isActionBarHidden = true
        }
        (activity as? AppCompatActivity)?.supportActionBar?.hide()

        val factory = ViewModelFactory.getInstance(requireContext())
        homeViewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        setupSearchView()
        setupObservers()
        setupArticleRecyclerView()
        setupNutritionsRecyclerView()

        binding.etAdd.setOnClickListener {
            val intent = Intent(requireContext(), AddActivity::class.java)
            startActivity(intent)
        }
        binding.etScan.setOnClickListener {
            val intent = Intent(requireContext(), ScanActivity::class.java)
            startActivity(intent)
        }

        binding.piringku.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = android.net.Uri.parse("https://ayosehat.kemkes.go.id/isi-piringku-pedoman-makan-kekinian-orang-indonesia")
            startActivity(intent)
            true
        }


        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        homeViewModel.fetchNutritionData(currentDate)
        Log.d("HomeFragment", "date in day: $currentDate")
//        homeViewModel.fetchNutritionData("2024-12-07")

    }


    private fun setupSearchView() {
        binding.searchView.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                val query = v.text.toString().trim()
                if (query.isNotEmpty()) {
                    homeViewModel.searchFood(query)
                }
                true
            } else {
                false
            }
        }
    }

    private fun setupObservers() {
        homeViewModel.foodNutritionResult.observe(viewLifecycleOwner) { food ->
            food?.let {
                navigateToAddSearchActivity(it)
                homeViewModel.resetFoodNutritionResult()
            }
        }

        homeViewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                showFoodNotFoundAlert(it)
                homeViewModel.resetErrorMessage()
            }
        }

        homeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        homeViewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (user.isLogin) {
                user.userId.let { userId ->
                    homeViewModel.fetchDailyCalories(userId)
                }
            }
        }

        val currentDate = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault()).format(Date())

        homeViewModel.caloriesConsumed.observe(viewLifecycleOwner) { caloriesConsumed ->
            homeViewModel.dailyCalories.observe(viewLifecycleOwner) { dailyCalories ->
                if (dailyCalories != null) {
                    val progress = (caloriesConsumed / dailyCalories) * 100

                    Log.d("HomeFragment", "Daily Calories: $dailyCalories")
                    Log.d("HomeFragment", "Calories Consumed: $caloriesConsumed")
                    Log.d("HomeFragment", "Progress: $progress")

                    binding.semiCircularProgressBar.setMax(dailyCalories)
                    binding.semiCircularProgressBar.setTotalCalories(dailyCalories)
                    binding.semiCircularProgressBar.setProgress(caloriesConsumed)

                    binding.tvCurrentDate.text = currentDate
                    binding.tvCaloriesNeeded.text = "${dailyCalories.toInt()} kkal"
                    binding.tvCaloriesConsumed.text = "${caloriesConsumed.toInt()} kkal"

                }
            }

        }
    }

    private fun setupNutritionsRecyclerView() {
        val nutritionAdapter = NutritionAdapter()
        binding.rvNutrition.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = nutritionAdapter
        }

        homeViewModel.nutritionData.observe(viewLifecycleOwner) { nutritionItems ->
            nutritionAdapter.setNutritionList(nutritionItems)
        }

        homeViewModel.nutritionAlertMessage.observe(viewLifecycleOwner) { alertMessage ->
            binding.alertNutrition.text = alertMessage ?: "Semua nutrisi kamu tercukupi."
        }

    }

    private fun setupArticleRecyclerView() {
        articleAdapter = ArticleAdapter()
        binding.rvArticle.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = articleAdapter
        }

        homeViewModel.articles.observe(viewLifecycleOwner) { articles ->
            articles?.let {
                articleAdapter.submitList(it)
            }
        }

        homeViewModel.fetchArticles()
    }

    private fun showFoodNotFoundAlert(message: String) {
        if (alertDialog == null || !alertDialog!!.isShowing) {
            val dialogBinding = AlertBinding.inflate(LayoutInflater.from(requireContext()))

            dialogBinding.tvAlertMessage.text = message

            // Atur aksi tombol
            dialogBinding.btnAddFood.setOnClickListener {
                val intent = Intent(requireContext(), AddActivity::class.java)
                startActivity(intent)
                alertDialog?.dismiss()
                homeViewModel.resetErrorMessage()
            }

            dialogBinding.btnCancel.setOnClickListener {
                alertDialog?.dismiss()
            }

            alertDialog = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
                .setView(dialogBinding.root)
                .create()

            alertDialog?.show()
        }
    }

    private fun navigateToAddSearchActivity(food: Food) {
        val intent = Intent(requireContext(), AddSearchActivity::class.java).apply {
            putExtra(AddSearchActivity.EXTRA_FOOD, food)
        }
        startActivity(intent)
        homeViewModel.resetFoodNutritionResult()

    }

    override fun onPause() {
        super.onPause()
        (activity as? AppCompatActivity)?.supportActionBar?.show()
        alertDialog?.dismiss()
        alertDialog = null
    }

    override fun onResume() {
        super.onResume()
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        homeViewModel.errorMessage.value?.let {
            showFoodNotFoundAlert(it)
            homeViewModel.resetErrorMessage()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        _binding = null
    }
}
