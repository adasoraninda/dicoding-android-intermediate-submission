package com.adasoraninda.dicodingstoryapp.validation

import com.adasoraninda.dicodingstoryapp.R
import com.adasoraninda.dicodingstoryapp.common.validation.InputValidation
import com.adasoraninda.dicodingstoryapp.features.auth.login.LoginValidation
import com.adasoraninda.dicodingstoryapp.model.InputLogin
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class LoginValidationTest {

    private lateinit var validation: InputValidation

    @Before
    fun setup() {
        validation = LoginValidation()
    }

    @Test
    fun should_return_message_invalid_when_data_instance_is_not_input_login() {
        val data = "" // not instance of InputLogin
        val expectedMessage = R.string.error_input_validation
        val resultMessage = validation.validate(data)

        assertEquals(expectedMessage, resultMessage)
    }

    @Test
    fun should_return_message_invalid_email_when_email_is_empty() {
        val expectedMessage = R.string.error_input_email

        val inputLogin = InputLogin(email = "")
        val resultMessage = validation.validate(inputLogin)

        assertEquals(expectedMessage, resultMessage)
    }

    @Test
    fun should_return_message_invalid_email_when_email_pattern_is_incorrect() {
        val expectedMessage = R.string.error_input_email

        val inputLogin = InputLogin(email = "ada")
        val resultMessage = validation.validate(inputLogin)

        assertEquals(expectedMessage, resultMessage)
    }

    @Test
    fun should_return_message_invalid_password_when_password_is_empty() {
        val expectedMessage = R.string.error_input_password

        val inputLogin = InputLogin(email = "ada@ada.com", password = "")
        val resultMessage = validation.validate(inputLogin)

        assertEquals(expectedMessage, resultMessage)
    }

    @Test
    fun should_return_message_invalid_password_when_password_length_is_less_than_6_chars() {
        val expectedMessage = R.string.error_input_password

        val inputLogin = InputLogin(email = "ada@ada.com", password = "ada")
        val resultMessage = validation.validate(inputLogin)

        assertEquals(expectedMessage, resultMessage)
    }

    @Test
    fun should_return_null_when_email_and_password_valid() {
        val inputLogin = InputLogin(email = "ada@ada.com", password = "adaada")
        val resultMessage = validation.validate(inputLogin)

        assertEquals(null, resultMessage)
    }

}