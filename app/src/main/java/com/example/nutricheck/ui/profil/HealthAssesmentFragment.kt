package com.example.nutricheck.ui.profil

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.nutricheck.R
import com.example.nutricheck.ViewModelFactory
import com.example.nutricheck.auth.login.LoginActivity
import com.example.nutricheck.data.Result
import com.example.nutricheck.data.response.BMIData
import com.example.nutricheck.databinding.FragmentHealthAssesmentBinding
import kotlinx.coroutines.launch

class HealthAssesmentFragment : Fragment() {

    private var _binding: FragmentHealthAssesmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HealthAssesmentViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHealthAssesmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (user.isLogin) {
                viewModel.fetchDailyCalories(user.userId)
            } else {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                requireActivity().finish()
            }
        }
        fetchUserData()
        observeViewModel()
        binding.btnSave.setOnClickListener {
            findNavController().navigate(R.id.action_asessment_to_user)

        }

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_asessment_to_user)
        }
    }

    private fun observeViewModel(){
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }


        viewModel.dailyCalories.observe(viewLifecycleOwner) { totalCalories ->
            binding.tvCalorieValue.text = if (totalCalories != null) {
                "$totalCalories kcal"
            } else {
                "Calorie User Not FOund"
            }
        }
    }

    private fun fetchUserData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.fetchUserBMI().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        // Show loading indicator if needed
                    }
                    is Result.Success -> {
                        val userData = result.data.data
                        val bmiData = userData?.bmi

                        bmiData?.let { calculateBMI(it) }
                        binding.tvActivityValue.text = "${bmiData?.activity ?: "N/A"}"
                        binding.tvHeightValue.text = "${bmiData?.height ?: "N/A"} cm"
                        binding.tvWeightValue.text = "${bmiData?.weight ?: "N/A"} kg"
                    }
                    is Result.Error -> {
                        Toast.makeText(
                            requireContext(),
                            "Failed to load user data: ${result.error}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun calculateBMI(bmiData: BMIData) {
        val heightInMeters = (bmiData.height ?: 0) / 100.0f
        if (heightInMeters > 0) {
            val bmi = (bmiData.weight ?: 0) / (heightInMeters * heightInMeters)
            binding.tvBMIValue.text = String.format("%.1f", bmi)

            val bmiStatus = when {
                bmi < 18.5 -> "Sangat Kurus"
                bmi < 25 -> "Normal"
                bmi < 30 -> "Overweight"
                else -> "Obesitas"
            }
            binding.tvBMIStatus.text = bmiStatus

        } else {
            Log.e("ProfilViewModel", "Invalid height for BMI calculation")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
