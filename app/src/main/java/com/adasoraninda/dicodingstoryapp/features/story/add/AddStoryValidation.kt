package com.adasoraninda.dicodingstoryapp.features.story.add

import com.adasoraninda.dicodingstoryapp.R
import com.adasoraninda.dicodingstoryapp.common.validation.InputValidation
import com.adasoraninda.dicodingstoryapp.model.InputAddStory

class AddStoryValidation : InputValidation {

    override fun <T> validate(data: T, message: ((Int) -> Unit)?): Boolean {
        if (data !is InputAddStory) return false

        val token = data.token

        if (token.isNullOrEmpty()) {
            message?.invoke(R.string.error_token)
            return false
        }

        val file = data.file

        if (file == null) {
            message?.invoke(R.string.error_file_null)
            return false
        }

        val desc = data.description

        if (desc.isNullOrEmpty()) {
            message?.invoke(R.string.error_input_desc_empty)
            return false
        }

        return true
    }
}