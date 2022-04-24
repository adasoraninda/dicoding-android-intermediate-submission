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
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.argumentCaptor
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

    private val name = "name"
    private val email = "email@email.com"
    private val password = "123456"

    @Before
    fun setup() {
        validation = mock()
        remoteDataSource = mock()
        viewModel = RegisterViewModel(remoteDataSource, validation)
    }

    @Test
    fun register_success_test() = runTest {
        // arrange
        val inputCaptor = argumentCaptor<InputRegister>()
        val responseSuccess = BaseResponse(error = false, message = "Success")
        val mockResult = flowOf(responseSuccess)

        `when`(validation.validate(inputCaptor.capture()))
            .thenReturn(null)
        `when`(remoteDataSource.register(name, email, password))
            .thenReturn(mockResult)

        // act
        viewModel.register(name, email, password)

        val inputUser = inputCaptor.firstValue
        val message = viewModel.dialogInfoSuccess.getOrAwaitValue()

        // assert
        verify(validation, only()).validate(inputCaptor.capture())
        verify(remoteDataSource, times(1)).register(name, email, password)
        Assert.assertEquals(name, inputUser.name)
        Assert.assertEquals(email, inputUser.email)
        Assert.assertEquals(password, inputUser.password)
        Assert.assertNotNull(message)
        Assert.assertEquals(responseSuccess.message, message)
    }

    @Test
    fun register_failure_test() = runTest {
        // arrange
        val inputCaptor = argumentCaptor<InputRegister>()
        val responseError = BaseResponse(error = true, message = "Error")
        val mockResult = flowOf(responseError)

        `when`(validation.validate(inputCaptor.capture()))
            .thenReturn(null)
        `when`(remoteDataSource.register(name, email, password))
            .thenReturn(mockResult)

        // act
        viewModel.register(name, email, password)

        val inputUser = inputCaptor.firstValue
        val message = viewModel.dialogInfoError.getOrAwaitValue()

        // assert
        verify(validation, only()).validate(inputCaptor.capture())
        verify(remoteDataSource, times(1)).register(name, email, password)
        Assert.assertEquals(name, inputUser.name)
        Assert.assertEquals(email, inputUser.email)
        Assert.assertEquals(password, inputUser.password)
        Assert.assertNotNull(message)
        Assert.assertEquals(responseError.message, message)
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