package com.example.nutricheck.ui.profil

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
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

@Suppress("UNREACHABLE_CODE")
class HealthAssesmentFragment : Fragment() {

    private lateinit var tvHeightValue: TextView
    private lateinit var tvWeightValue: TextView
    private lateinit var tvActivityValue: TextView

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
        binding.apply {
            this@HealthAssesmentFragment.tvHeightValue = this.tvHeightValue
            this@HealthAssesmentFragment.tvWeightValue = this.tvWeightValue
            this@HealthAssesmentFragment.tvActivityValue = this.tvActivityValue
        }
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
        setupInput(view)
        binding.btnSave.setOnClickListener {
            findNavController().navigate(R.id.action_asessment_to_user)
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_asessment_to_user)
        }

        // Observing data directly to update views
        viewModel.height.observe(viewLifecycleOwner) { height ->
            binding.tvHeightValue.text = "$height cm"
        }

        viewModel.weight.observe(viewLifecycleOwner) { weight ->
            binding.tvWeightValue.text = "$weight kg"
        }

        viewModel.activity.observe(viewLifecycleOwner) { activity ->
            binding.tvActivityValue.text = activity
        }
    }


    private fun setupInput(view: View) {
        val ivpanah1: ImageView = view.findViewById(R.id.ivpanah1)
        val ivpanah2: ImageView = view.findViewById(R.id.ivzpanah2)
        val ivpanah3: ImageView = view.findViewById(R.id.ivpanah3)

        ivpanah1.setOnClickListener {
            showEditDialog("Height", tvHeightValue)
        }
        ivpanah2.setOnClickListener {
            showEditDialog("Weight", tvWeightValue)
        }
        ivpanah3.setOnClickListener {
            showEditDialog("Activity Status", tvActivityValue)
        }
    }

    private fun showEditDialog(title: String, textView: TextView?) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.popup_form, null)
        val editText: EditText = dialogView.findViewById(R.id.tvInputEdit)
        val tvDialogTitle: TextView = dialogView.findViewById(R.id.titleText)
        val tvDialogSubtitle: TextView = dialogView.findViewById(R.id.subtitleText)

        // Customize dialog based on the title
        when (title) {
            "Height" -> {
                tvDialogTitle.text = "Update your height"
                tvDialogSubtitle.text = "Enter your height in centimeters."
                editText.hint = "Enter height"
                editText.setText(textView?.text)
                editText.inputType = InputType.TYPE_CLASS_NUMBER
            }
            "Weight" -> {
                tvDialogTitle.text = "Update your weight"
                tvDialogSubtitle.text = "Enter your weight in kilograms."
                editText.hint = "Enter weight"
                editText.setText(textView?.text)
                editText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            }
            "Activity Status" -> {
                tvDialogTitle.text = "Update your activity status"
                tvDialogSubtitle.text = "Enter your activity level (e.g., Sedentary, Moderate, Active)."
                editText.hint = "Enter activity status"
                editText.setText(textView?.text)
                editText.inputType = InputType.TYPE_CLASS_TEXT
            }
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        // Set up custom submit button
        val submitButton = dialogView.findViewById<ImageButton>(R.id.tvButton)
        submitButton.setOnClickListener {
            val newValue = editText.text.toString()
            when (title) {
                "Height" -> {
                    val height = newValue.toIntOrNull()
                    if (height != null && height in 50..300) {
                        viewModel.updateHeight(height)
                        dialog.dismiss()
                    } else {
                        Toast.makeText(requireContext(), "Invalid height", Toast.LENGTH_SHORT).show()
                    }
                }
                "Weight" -> {
                    val weight = newValue.toIntOrNull()
                    if (weight != null && weight in 20..500) {
                        viewModel.updateWeight(weight)
                        dialog.dismiss()
                    } else {
                        Toast.makeText(requireContext(), "Invalid weight", Toast.LENGTH_SHORT).show()
                    }
                }
                "Activity Status" -> {
                    if (newValue.isNotBlank()) {
                        viewModel.updateActivityStatus(newValue)
                        dialog.dismiss()
                    } else {
                        Toast.makeText(requireContext(), "Activity status cannot be empty", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        dialog.show()
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
