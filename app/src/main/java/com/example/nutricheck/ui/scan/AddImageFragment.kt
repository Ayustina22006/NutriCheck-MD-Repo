package com.example.nutricheck.ui.scan

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.nutricheck.R
import com.example.nutricheck.ViewModelFactory
import com.example.nutricheck.data.Injection
import com.example.nutricheck.data.database.AppDatabase
import com.example.nutricheck.databinding.FragmentAddImageBinding
import com.example.nutricheck.ui.nutrition.NutritionActivity
import kotlinx.coroutines.launch

class AddImageFragment : Fragment() {
    private lateinit var binding: FragmentAddImageBinding
    private lateinit var cameraViewModel: CameraViewModel
    private lateinit var imageAdapter: ImageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddImageBinding.inflate(inflater, container, false)

        Log.d("AddImageFragment", "Binding root not null: ${binding.root != null}")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Log view dimensions
        view.post {
            Log.d("AddImageFragment", "View width after post: ${view.width}")
            Log.d("AddImageFragment", "RecyclerView width after post: ${binding.rvCapturedFood.width}")
        }

        // Inisialisasi ViewModel
        val userRepository = Injection.provideRepository(requireContext())
        val resourceProvider = Injection.provideResourceProvider(requireContext())
        val factory = ViewModelFactory(userRepository, resourceProvider, requireActivity().application)
        cameraViewModel = ViewModelProvider(requireActivity(), factory)[CameraViewModel::class.java]

        val appDao = AppDatabase.getDatabase(requireContext()).capturedFoodItemDao()

        imageAdapter = ImageAdapter(foodItems = emptyList(), appDao)
        binding.rvCapturedFood.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = imageAdapter
        }

        // Observasi LiveData
        cameraViewModel.capturedFoodItems.observe(viewLifecycleOwner) { capturedItems ->
            Log.d("AddImageFragment", "Captured items size: ${capturedItems.size}")

            // Atur visibilitas RecyclerView dan TextView
            if (capturedItems.isEmpty()) {
                binding.rvCapturedFood.visibility = View.GONE
                binding.emptyStateText.visibility = View.VISIBLE
            } else {
                binding.rvCapturedFood.visibility = View.VISIBLE
                binding.emptyStateText.visibility = View.GONE

                imageAdapter.updateData(capturedItems)
            }
        }

        // Button listeners
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnAddImage.setOnClickListener {
            findNavController().navigate(R.id.action_addImageFragment_to_cameraFragment)
        }
        binding.btnCheckNutrition.setOnClickListener {
            lifecycleScope.launch {
                val mealIds = cameraViewModel.fetchAllMealIds()
                if (mealIds.isNotEmpty()) {
                    cameraViewModel.submitMealHistory(
                        mealIds = mealIds,
                        onSuccess = { mealHistoryResponse ->
                            Toast.makeText(requireContext(), "Meal history submitted successfully!", Toast.LENGTH_SHORT).show()
                            cameraViewModel.clearAllCapturedFoodItems()

                            // Pass the entire MealHistoryResponse to NutritionActivity
                            val intent = Intent(requireContext(), NutritionActivity::class.java).apply {
                                putExtra("MEAL_HISTORY_RESPONSE", mealHistoryResponse)
                            }
                            startActivity(intent)
                        },
                        onError = { errorMessage ->
                            Toast.makeText(requireContext(), "Failed to submit: $errorMessage", Toast.LENGTH_SHORT).show()
                        }
                    )
                } else {
                    Toast.makeText(requireContext(), "No food items to submit!", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
}