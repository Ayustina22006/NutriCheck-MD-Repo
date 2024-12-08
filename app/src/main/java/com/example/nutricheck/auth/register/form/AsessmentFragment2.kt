package com.example.nutricheck.auth.register.form

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.nutricheck.R
import com.example.nutricheck.ViewModelFactory
import com.example.nutricheck.auth.register.AssessmentViewModel
import com.example.nutricheck.databinding.FragmentAsessment2Binding

class AsessmentFragment2 : Fragment() {

    private var _binding: FragmentAsessment2Binding? = null
    private val binding get() = _binding!!
    private val viewModel: AssessmentViewModel by activityViewModels {
        ViewModelFactory.getInstance(requireContext())
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAsessment2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.numberPicker.apply {
            minValue = 1
            maxValue = 100
            value = 19
        }

        binding.btnContinue.setOnClickListener {
            val selectedAge = binding.numberPicker.value
            viewModel.setAge(selectedAge)
            findNavController().navigate(R.id.action_Asessment2_to_Asessment3)
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_Asessment2_to_Asessment1)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


