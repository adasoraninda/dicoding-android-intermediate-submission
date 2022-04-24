package com.adasoraninda.dicodingstoryapp.validation

import com.adasoraninda.dicodingstoryapp.R
import com.adasoraninda.dicodingstoryapp.common.validation.InputValidation
import com.adasoraninda.dicodingstoryapp.features.story.add.AddStoryValidation
import com.adasoraninda.dicodingstoryapp.model.InputAddStory
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.File

class AddStoryValidationTest {

    private lateinit var validation: InputValidation

    @Before
    fun setup() {
        validation = AddStoryValidation()
    }

    @Test
    fun should_return_message_invalid_when_data_instance_is_not_input_add_story() {
        val data = "" // not instance of InputAddStory
        val expectedMessage = R.string.error_input_validation
        val resultMessage = validation.validate(data)

        Assert.assertEquals(expectedMessage, resultMessage)
    }

    @Test
    fun should_return_message_invalid_token_when_token_is_null() {
        val expectedMessage = R.string.error_token

        val inputAddStoryNull = InputAddStory(token = null, file = File(""), description = "desc")
        val resultMessage = validation.validate(inputAddStoryNull)

        Assert.assertEquals(expectedMessage, resultMessage)
    }

    @Test
    fun should_return_message_invalid_file_when_file_is_null() {
        val expectedMessage = R.string.error_file_null

        val inputAddStory = InputAddStory("fd", null, "fd")
        val resultMessage = validation.validate(inputAddStory)

        Assert.assertEquals(expectedMessage, resultMessage)
    }

    @Test
    fun should_return_false_when_description_is_null() {
        val expectedMessage = R.string.error_input_desc_empty

        val inputAddStory = InputAddStory("fd", File(""), null)
        val resultMessage = validation.validate(inputAddStory)

        Assert.assertEquals(expectedMessage, resultMessage)
    }

    @Test
    fun should_return_null_when_input_all_correct() {
        val inputAddStory = InputAddStory("fd", File(""), "fdf")
        val resultMessage = validation.validate(inputAddStory)

        Assert.assertEquals(null, resultMessage)
    }

}