package com.example.nutricheck.auth

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class MyEditText(context: Context, attrs: AttributeSet) : TextInputEditText(context, attrs) {

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                when (hint.toString()) {
                    "Your Email" -> validateEmail(s)
                    "Your Password", "Confirm Password" -> validatePassword(s)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun validateEmail(email: CharSequence?) {
        val parentLayout = parent.parent as? TextInputLayout
        if (!email.isNullOrEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            parentLayout?.error = "Invalid email format"
        } else {
            parentLayout?.error = null
        }
    }

    private fun validatePassword(password: CharSequence?) {
        val parentLayout = parent.parent as? TextInputLayout
        val hasLetter = password?.any { it.isLetter() } ?: false
        val hasDigit = password?.any { it.isDigit() } ?: false
        when {
            password.isNullOrEmpty() -> parentLayout?.error = null
            password.length < 8 -> parentLayout?.error = "Password must be at least 8 characters"
            !hasLetter || !hasDigit -> parentLayout?.error = "Password must contain letters and digits"
            else -> parentLayout?.error = null
        }
    }
}
