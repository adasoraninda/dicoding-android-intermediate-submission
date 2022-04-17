package com.adasoraninda.dicodingstoryapp

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
    fun should_return_false_when_data_instance_is_not_input_add_story() {
        val data = "" // not instance of InputAddStory
        val result = validation.validate(data)

        Assert.assertEquals(false, result)
    }

    @Test
    fun should_return_false_when_token_is_null_or_empty() {
        val expectedMessage = R.string.error_token
        var actualMessageEmpty = 0
        var actualMessageNull = 0

        val inputAddStoryEmpty = InputAddStory(token = "", file = File(""), description = "desc")
        val resultEmpty = validation.validate(inputAddStoryEmpty) {
            actualMessageEmpty = it
        }

        val inputAddStoryNull = InputAddStory(token = null, file = File(""), description = "desc")
        val resultNull = validation.validate(inputAddStoryNull) {
            actualMessageNull = it
        }

        Assert.assertEquals(expectedMessage, actualMessageEmpty)
        Assert.assertEquals(false, resultEmpty)

        Assert.assertEquals(expectedMessage, actualMessageNull)
        Assert.assertEquals(false, resultNull)
    }

    @Test
    fun should_return_false_when_file_is_null() {
        val expectedMessage = R.string.error_file_null
        var actualMessage = 0

        val inputAddStory = InputAddStory("fd", null, "fd")
        val result = validation.validate(inputAddStory) {
            actualMessage = it
        }

        Assert.assertEquals(actualMessage, expectedMessage)
        Assert.assertEquals(false, result)
    }


    @Test
    fun should_return_true_when_file_is_not_null() {
        val expectedMessage = null
        var actualMessage: Int? = null

        val inputAddStory = InputAddStory("fd", File(""), "fd")
        val result = validation.validate(inputAddStory) {
            actualMessage = it
        }

        Assert.assertEquals(actualMessage, expectedMessage)
        Assert.assertEquals(true, result)
    }

    @Test
    fun should_return_false_when_description_is_null() {
        val expectedMessage = R.string.error_input_desc_empty
        var actualMessage: Int? = null

        val inputAddStory = InputAddStory("fd", File(""), null)
        val result = validation.validate(inputAddStory) {
            actualMessage = it
        }

        Assert.assertEquals(actualMessage, expectedMessage)
        Assert.assertEquals(false, result)
    }

    @Test
    fun should_return_true_when_description_is_null() {
        val expectedMessage = null
        var actualMessage: Int? = null

        val inputAddStory = InputAddStory("fd", File(""), "fdf")
        val result = validation.validate(inputAddStory) {
            actualMessage = it
        }

        Assert.assertEquals(actualMessage, expectedMessage)
        Assert.assertEquals(true, result)
    }

    @Test
    fun should_return_true_when_input_all_correct() {
        val expectedMessage = null
        var actualMessage: Int? = null

        val inputAddStory = InputAddStory("fd", File(""), "fdf")
        val result = validation.validate(inputAddStory) {
            actualMessage = it
        }

        Assert.assertEquals(actualMessage, expectedMessage)
        Assert.assertEquals(true, result)
    }

}