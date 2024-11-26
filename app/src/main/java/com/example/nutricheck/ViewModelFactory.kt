package com.example.nutricheck

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.nutricheck.auth.login.LoginViewModel
import com.example.nutricheck.auth.register.AssessmentViewModel
import com.example.nutricheck.auth.register.RegisterViewModel
import com.example.nutricheck.data.Injection
import com.example.nutricheck.data.UserRepository
import com.example.nutricheck.ui.home.HomeViewModel

class ViewModelFactory(private val repository: UserRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(repository) as T
            }
            modelClass.isAssignableFrom(AssessmentViewModel::class.java) -> {
                Log.d("ViewModelFactory", "Creating AssessmentViewModel")
                AssessmentViewModel(repository) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(repository) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }


    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory {
            return INSTANCE ?: synchronized(this) {
                val userRepository = Injection.provideRepository(context)
                INSTANCE = ViewModelFactory(userRepository)
                INSTANCE!!
            }
        }
    }
}
