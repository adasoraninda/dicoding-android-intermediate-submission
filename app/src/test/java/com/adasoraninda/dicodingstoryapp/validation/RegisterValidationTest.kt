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
    fun should_return_false_when_data_instance_is_not_input_register() {
        val data = "" // not instance of InputRegister
        val result = validation.validate(data)

        Assert.assertEquals(false, result)
    }

    @Test
    fun should_return_false_when_name_is_empty() {
        val expectedMessage = R.string.error_input_name
        var actualMessage = 0

        val inputRegister = InputRegister(name = "")
        val result = validation.validate(inputRegister, message = {
            actualMessage = it
        })

        Assert.assertEquals(expectedMessage, actualMessage)
        Assert.assertEquals(false, result)
    }

    @Test
    fun should_return_false_when_email_is_empty() {
        val expectedMessage = R.string.error_input_email
        var actualMessage = 0

        val inputRegister = InputRegister(name = "ada", email = "")
        val result = validation.validate(inputRegister, message = {
            actualMessage = it
        })

        Assert.assertEquals(expectedMessage, actualMessage)
        Assert.assertEquals(false, result)
    }

    @Test
    fun should_return_false_when_email_pattern_is_incorrect() {
        val expectedMessage = R.string.error_input_email
        var actualMessage = 0

        val inputRegister = InputRegister(name = "ada", email = "ada")
        val result = validation.validate(inputRegister, message = {
            actualMessage = it
        })

        Assert.assertEquals(expectedMessage, actualMessage)
        Assert.assertEquals(false, result)
    }

    @Test
    fun should_return_false_when_password_is_empty() {
        val expectedMessage = R.string.error_input_password
        var actualMessage = 0

        val inputRegister = InputRegister(name = "ada", email = "ada@ada.com", password = "")
        val result = validation.validate(inputRegister,message = {
            actualMessage = it
        })

        Assert.assertEquals(expectedMessage, actualMessage)
        Assert.assertEquals(false, result)
    }

    @Test
    fun should_return_false_when_password_length_is_less_than_6_chars() {
        val expectedMessage = R.string.error_input_password
        var actualMessage = 0

        val inputRegister = InputRegister(name = "ada", email = "ada@ada.com", password = "ada")
        val result = validation.validate(inputRegister,message = {
            actualMessage = it
        })

        Assert.assertEquals(expectedMessage, actualMessage)
        Assert.assertEquals(false, result)
    }

    @Test
    fun should_return_true_when_email_and_password_valid() {
        val inputRegister = InputRegister(name = "ada", email = "ada@ada.com", password = "adaada")
        val result = validation.validate(inputRegister)

        Assert.assertEquals(true, result)
    }

}