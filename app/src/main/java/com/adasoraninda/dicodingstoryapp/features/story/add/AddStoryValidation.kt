package com.adasoraninda.dicodingstoryapp.features.story.add

import com.adasoraninda.dicodingstoryapp.R
import com.adasoraninda.dicodingstoryapp.common.validation.InputValidation
import com.adasoraninda.dicodingstoryapp.model.InputAddStory

class AddStoryValidation : InputValidation {

    override fun <T> validate(data: T): Int? {
        if (data !is InputAddStory) return R.string.error_input_validation
        if (data.token.isNullOrEmpty()) return R.string.error_token
        if (data.description.isNullOrEmpty()) return R.string.error_input_desc_empty

        data.file ?: return R.string.error_file_null

        return null
    }
}