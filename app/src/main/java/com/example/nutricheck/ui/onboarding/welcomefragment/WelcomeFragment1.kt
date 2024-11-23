package com.example.nutricheck.ui.onboarding.welcomefragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.nutricheck.R
import com.example.nutricheck.databinding.FragmentWelcome1Binding

class WelcomeFragment1 : Fragment() {

    private var _binding: FragmentWelcome1Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcome1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Get Started button click listener
        binding.btnGetStarted.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment1_to_welcomeFragment2)
        }

        // Sign In text click listener
        binding.btnSignIn.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment1_to_loginActivity)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}