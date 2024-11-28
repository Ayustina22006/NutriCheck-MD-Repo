package com.example.nutricheck.auth.register.form

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.nutricheck.MainActivity
import com.example.nutricheck.R
import com.example.nutricheck.ViewModelFactory
import com.example.nutricheck.auth.register.AssessmentViewModel
import com.example.nutricheck.data.response.AssessmentRequest
import com.example.nutricheck.databinding.FragmentAsessment4Binding
import kotlinx.coroutines.launch


class AsessmentFragment4 : Fragment() {

    private var _binding: FragmentAsessment4Binding? = null
    private val binding get() = _binding!!
    private val assessmentViewModel: AssessmentViewModel by activityViewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAsessment4Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedActivity = when (checkedId) {
                R.id.rbInactive -> "inactive"
                R.id.rbLightActivity -> "light activity"
                R.id.rbModerateActivity -> "moderate activity"
                R.id.rbHighActivity -> "high activity"
                R.id.rbVeryHighActivity -> "very high activity"
                else -> null
            }
            selectedActivity?.let { assessmentViewModel.setActivity(it) }
        }

        binding.btnContinue.setOnClickListener {
            lifecycleScope.launch {
                sendAssessmentData()
            }
        }

        // Observasi perubahan assessmentResult
        assessmentViewModel.assessmentResult.observe(viewLifecycleOwner) { resultMessage ->
            Toast.makeText(requireContext(), resultMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun sendAssessmentData() {
        val gender = assessmentViewModel.gender.value
        val age = assessmentViewModel.age.value
        val height = assessmentViewModel.height.value
        val weight = assessmentViewModel.weight.value
        val activity = assessmentViewModel.activity.value
        val userId = assessmentViewModel.userId.value

        Log.d("AssessmentData", "Gender: $gender, Age: $age, Height: $height, Weight: $weight, Activity: $activity, UserId: $userId")

        if (gender == null || age == null || height == null || weight == null || activity == null || userId == null) {
            Toast.makeText(requireContext(), "Lengkapi semua data terlebih dahulu!", Toast.LENGTH_SHORT).show()
            return
        }

        val request = AssessmentRequest(gender, age, height, weight, activity)
        assessmentViewModel.submitAssessment(userId, request)

        // Observasi hasil pengiriman data
        assessmentViewModel.assessmentResult.observe(viewLifecycleOwner) { resultMessage ->
            Toast.makeText(requireContext(), resultMessage, Toast.LENGTH_SHORT).show()

            // Jika berhasil, pindah ke MainActivity
            if (resultMessage.contains("Assessment berhasil")) {
                navigateToMainActivity()
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
