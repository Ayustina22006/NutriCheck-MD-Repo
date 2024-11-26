package com.example.nutricheck.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.nutricheck.auth.pref.UserModel
import com.example.nutricheck.data.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun login(email: String, password: String) =
        userRepository.login(email, password).asLiveData()

    fun getSession() = userRepository.getSession().asLiveData()

}
