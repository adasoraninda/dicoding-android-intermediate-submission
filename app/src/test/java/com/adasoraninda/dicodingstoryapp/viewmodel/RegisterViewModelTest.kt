package com.adasoraninda.dicodingstoryapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.adasoraninda.dicodingstoryapp.common.validation.InputValidation
import com.adasoraninda.dicodingstoryapp.features.auth.register.RegisterViewModel
import com.adasoraninda.dicodingstoryapp.model.InputRegister
import com.adasoraninda.dicodingstoryapp.service.remote.IRemoteDataSource
import com.adasoraninda.dicodingstoryapp.service.remote.response.BaseResponse
import com.adasoraninda.dicodingstoryapp.utils.MainCoroutineRule
import com.adasoraninda.dicodingstoryapp.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private lateinit var remoteDataSource: IRemoteDataSource
    private lateinit var validation: InputValidation
    private lateinit var viewModel: RegisterViewModel

    @Before
    fun setup() {
        validation = mock()
        remoteDataSource = mock()
        viewModel = RegisterViewModel(remoteDataSource, validation)
    }

    @Test
    fun register_success_test() {
        val inputRegister = InputRegister("name", "email@email.com", "123456")
        val resultResponse = flowOf(BaseResponse(error = false, message = "success"))
        val argMessageCaptor = argumentCaptor<Function1<Int, Unit>>()

        `when`(validation.validate(inputRegister, eq(argMessageCaptor.capture()))).thenReturn(true)

        `when`(
            remoteDataSource.register(
                inputRegister.name!!,
                inputRegister.email!!,
                inputRegister.password!!
            )
        ).thenReturn(resultResponse)

        viewModel.register(
            inputRegister.name!!,
            inputRegister.email!!,
            inputRegister.password!!
        )

        verify(validation).validate(inputRegister,  eq(argMessageCaptor.capture()))
        verify(remoteDataSource).register(
            inputRegister.name!!,
            inputRegister.email!!,
            inputRegister.password!!
        )
    }

    @Test
    fun register_failure_test() {

    }

    @Test
    fun should_return_true_when_input_is_correct() {
        val expectedValue = true
        var expectedMessage = 0
        val inputRegister = InputRegister("name", "email@email.com", "123456")
        val message = { message: Int -> expectedMessage = message }

        `when`(validation.validate(inputRegister, message)).thenReturn(expectedValue)
        val result = viewModel.checkInput(inputRegister, message)

        verify(validation).validate(inputRegister, message)
        Assert.assertEquals(expectedValue, result)
        Assert.assertEquals(0, expectedMessage)
    }

    @Test
    fun should_return_false_when_input_is_incorrect() {
        val expectedValue = false
        var expectedMessage = 0
        val inputRegister = InputRegister("", "", "")
        val message = { message: Int -> expectedMessage = message }

        `when`(validation.validate(inputRegister, message)).thenReturn(expectedValue)
        val result = viewModel.checkInput(inputRegister, message)

        verify(validation).validate(inputRegister, message)
        Assert.assertEquals(expectedValue, result)
        Assert.assertEquals(0, expectedMessage)
    }

    @Test
    fun should_dialog_success_value_to_null_when_dismissed() {
        viewModel.dismissSuccessDialog()
        val result = viewModel.dialogInfoSuccess.getOrAwaitValue()

        Assert.assertNull(result)
    }

    @Test
    fun should_dialog_error_value_to_null_when_dismissed() {
        viewModel.dismissErrorDialog()
        val result = viewModel.dialogInfoError.getOrAwaitValue()

        Assert.assertNull(result)
    }

}