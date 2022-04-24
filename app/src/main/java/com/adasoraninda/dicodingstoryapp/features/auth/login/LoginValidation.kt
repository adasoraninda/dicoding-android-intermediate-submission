package com.adasoraninda.dicodingstoryapp.features.auth.login

import androidx.core.util.PatternsCompat
import com.adasoraninda.dicodingstoryapp.R
import com.adasoraninda.dicodingstoryapp.common.validation.InputValidation
import com.adasoraninda.dicodingstoryapp.model.InputLogin

class LoginValidation : InputValidation {

    override fun <T> validate(data: T): Int? {
        if (data !is InputLogin) return R.string.error_input_validation

        val email = data.email

        if (email.isNullOrEmpty()) {
            return R.string.error_input_email
        }

        if (PatternsCompat.EMAIL_ADDRESS.matcher(email).matches().not()) {
            return R.string.error_input_email
        }

        val password = data.password

        if (password.isNullOrEmpty()) {
            return R.string.error_input_password
        }

        if (password.length < 6) {
            return R.string.error_input_password
        }

        return null
    }
}