package com.example.nutricheck.auth.register.form

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.nutricheck.R
import com.example.nutricheck.ViewModelFactory
import com.example.nutricheck.auth.register.AssessmentViewModel
import com.example.nutricheck.databinding.FragmentAsessment3Binding

class AsessmentFragment3 : Fragment() {

    private var _binding: FragmentAsessment3Binding? = null
    private val binding get() = _binding!!
    private val viewModel: AssessmentViewModel by activityViewModels {
        ViewModelFactory.getInstance(requireContext())
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAsessment3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNext.setOnClickListener {
            val height = binding.etHeight.text.toString().toIntOrNull()
            val weight = binding.etWeight.text.toString().toIntOrNull()

            if (height == null || height <= 0 || weight == null || weight <= 0) {
                Toast.makeText(requireContext(), "Fill in all the data correctly!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            viewModel.setHeight(height)
            viewModel.setWeight(weight)
            findNavController().navigate(R.id.action_Asessment3_to_Asessment4)
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_Asessment3_to_Asessment2)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

