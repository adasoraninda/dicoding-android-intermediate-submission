package com.adasoraninda.dicodingstoryapp.validation

import com.adasoraninda.dicodingstoryapp.R
import com.adasoraninda.dicodingstoryapp.common.validation.InputValidation
import com.adasoraninda.dicodingstoryapp.features.auth.register.RegisterValidation
import com.adasoraninda.dicodingstoryapp.model.InputRegister
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class RegisterValidationTest {

    private lateinit var validation: InputValidation

    @Before
    fun setup() {
        validation = RegisterValidation()
    }

    @Test
    fun should_return_message_invalid_when_data_instance_is_not_input_register() {
        val data = "" // not instance of InputRegister
        val expectedMessage = R.string.error_input_validation
        val resultMessage = validation.validate(data)

        Assert.assertEquals(expectedMessage, resultMessage)
    }

    @Test
    fun should_return_message_name_empty_when_name_is_empty() {
        val expectedMessage = R.string.error_input_name

        val inputRegister = InputRegister(name = "")
        val resultMessage = validation.validate(inputRegister)

        Assert.assertEquals(expectedMessage, resultMessage)
    }

    @Test
    fun should_return_message_invalid_email_when_email_is_empty() {
        val expectedMessage = R.string.error_input_email

        val inputRegister = InputRegister(name = "ada", email = "")
        val resultMessage = validation.validate(inputRegister)

        Assert.assertEquals(expectedMessage, resultMessage)
    }

    @Test
    fun should_return_message_invalid_email_when_email_pattern_is_incorrect() {
        val expectedMessage = R.string.error_input_email

        val inputRegister = InputRegister(name = "ada", email = "ada")
        val resultMessage = validation.validate(inputRegister)

        Assert.assertEquals(expectedMessage, resultMessage)
    }

    @Test
    fun should_return_message_invalid_password_when_password_is_empty() {
        val expectedMessage = R.string.error_input_password

        val inputRegister = InputRegister(name = "ada", email = "ada@ada.com", password = "")
        val resultMessage = validation.validate(inputRegister)

        Assert.assertEquals(expectedMessage, resultMessage)
    }

    @Test
    fun should_return_message_invalid_password_when_password_length_is_less_than_6_chars() {
        val expectedMessage = R.string.error_input_password

        val inputRegister = InputRegister(name = "ada", email = "ada@ada.com", password = "ada")
        val resultMessage = validation.validate(inputRegister)

        Assert.assertEquals(expectedMessage, resultMessage)
    }

    @Test
    fun should_return_null_when_email_and_password_valid() {
        val inputRegister = InputRegister(name = "ada", email = "ada@ada.com", password = "adaada")
        val result = validation.validate(inputRegister)

        Assert.assertEquals(null, result)
    }

}