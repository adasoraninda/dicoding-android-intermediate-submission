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
    fun should_return_false_when_data_instance_is_not_input_login() {
        val data = "" // not instance of InputLogin
        val result = validation.validate(data)

        assertEquals(false, result)
    }

    @Test
    fun should_return_false_when_email_is_empty() {
        val expectedMessage = R.string.error_input_email
        var actualMessage = 0

        val inputLogin = InputLogin(email = "")
        val result = validation.validate(inputLogin) {
            actualMessage = it
        }

        assertEquals(expectedMessage, actualMessage)
        assertEquals(false, result)
    }

    @Test
    fun should_return_false_when_email_pattern_is_incorrect() {
        val expectedMessage = R.string.error_input_email
        var actualMessage = 0

        val inputLogin = InputLogin(email = "ada")
        val result = validation.validate(inputLogin) {
            actualMessage = it
        }

        assertEquals(expectedMessage, actualMessage)
        assertEquals(false, result)
    }

    @Test
    fun should_return_false_when_password_is_empty() {
        val expectedMessage = R.string.error_input_password
        var actualMessage = 0

        val inputLogin = InputLogin(email = "ada@ada.com", password = "")
        val result = validation.validate(inputLogin) {
            actualMessage = it
        }

        assertEquals(expectedMessage, actualMessage)
        assertEquals(false, result)
    }

    @Test
    fun should_return_false_when_password_length_is_less_than_6_chars() {
        val expectedMessage = R.string.error_input_password
        var actualMessage = 0

        val inputLogin = InputLogin(email = "ada@ada.com", password = "ada")
        val result = validation.validate(inputLogin) {
            actualMessage = it
        }

        assertEquals(expectedMessage, actualMessage)
        assertEquals(false, result)
    }

    @Test
    fun should_return_true_when_email_and_password_valid() {
        val inputLogin = InputLogin(email = "ada@ada.com", password = "adaada")
        val result = validation.validate(inputLogin)

        assertEquals(true, result)
    }

}