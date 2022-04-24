package com.adasoraninda.dicodingstoryapp.features.auth.register

import androidx.core.util.PatternsCompat
import com.adasoraninda.dicodingstoryapp.R
import com.adasoraninda.dicodingstoryapp.common.validation.InputValidation
import com.adasoraninda.dicodingstoryapp.model.InputRegister

class RegisterValidation : InputValidation {

    override fun <T> validate(data: T): Int? {
        if (data !is InputRegister) return R.string.error_input_validation

        val name = data.name

        if (name.isNullOrEmpty()) {
            return R.string.error_input_name
        }

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