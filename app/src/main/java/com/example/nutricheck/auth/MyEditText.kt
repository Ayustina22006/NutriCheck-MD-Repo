package com.example.nutricheck.auth

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import com.example.nutricheck.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class MyEditText(context: Context, attrs: AttributeSet) : TextInputEditText(context, attrs) {

    private var isPasswordValidation = false
    private var isEmailValidation = false

    init {
        // Cek apakah atribut menentukan tipe validasi
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.MyEditText,
            0, 0
        ).apply {
            try {
                isPasswordValidation = getBoolean(R.styleable.MyEditText_isPasswordValidation, false)
                isEmailValidation = getBoolean(R.styleable.MyEditText_isEmailValidation, false)
            } finally {
                recycle()
            }
        }

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                when {
                    isPasswordValidation -> validatePassword(s)
                    isEmailValidation -> validateEmail(s)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun validatePassword(password: CharSequence?) {
        val parentLayout = parent.parent as? TextInputLayout
        if (!password.isNullOrEmpty() && password.length < 8) {
            parentLayout?.error = "Password must be at least 8 characters long"
        } else {
            parentLayout?.error = null
        }
    }

    private fun validateEmail(email: CharSequence?) {
        val parentLayout = parent.parent as? TextInputLayout
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        if (!email.isNullOrEmpty() && !emailRegex.matches(email)) {
            parentLayout?.error = "Invalid email address"
        } else {
            parentLayout?.error = null
        }
    }
}
