package com.example.nutricheck.auth.register

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.nutricheck.R

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)



        // Setup navigation component
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navOnboarding) as NavHostFragment
        val navController = navHostFragment.navController
    }
}


