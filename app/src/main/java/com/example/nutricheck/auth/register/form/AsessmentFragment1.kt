package com.example.nutricheck.auth.register.form

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.nutricheck.R
import com.example.nutricheck.auth.register.AssessmentViewModel
import com.example.nutricheck.databinding.FragmentAsessment1Binding
import com.google.android.material.card.MaterialCardView
import androidx.fragment.app.activityViewModels
import com.example.nutricheck.ViewModelFactory


class AsessmentFragment1 : Fragment() {

    private var _binding: FragmentAsessment1Binding? = null
    private val binding get() = _binding!!
    private val viewModel: AssessmentViewModel by activityViewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private var selectedGender: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAsessment1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            maleCard.setOnClickListener {
                selectedGender = "male"
                viewModel.setGender(selectedGender!!)
                updateCardSelection(maleCard, femaleCard)
            }

            femaleCard.setOnClickListener {
                selectedGender = "female"
                viewModel.setGender(selectedGender!!)
                updateCardSelection(femaleCard, maleCard)
            }

            btnContinue.setOnClickListener {
                if (selectedGender != null) {
                    findNavController().navigate(R.id.action_Asessment1_to_Asessment2)
                }
            }
        }
    }

    private fun updateCardSelection(selectedCard: MaterialCardView, unselectedCard: MaterialCardView) {
        selectedCard.strokeColor = ContextCompat.getColor(requireContext(), R.color.prymary_color)
        selectedCard.strokeWidth = resources.getDimensionPixelSize(R.dimen.selected_card_stroke_width)
        unselectedCard.strokeColor = ContextCompat.getColor(requireContext(), R.color.white)
        unselectedCard.strokeWidth = resources.getDimensionPixelSize(R.dimen.unselected_card_stroke_width)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


