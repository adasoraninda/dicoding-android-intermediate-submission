package com.adasoraninda.dicodingstoryapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.adasoraninda.dicodingstoryapp.common.validation.InputValidation
import com.adasoraninda.dicodingstoryapp.features.auth.login.LoginViewModel
import com.adasoraninda.dicodingstoryapp.model.InputLogin
import com.adasoraninda.dicodingstoryapp.model.UserPreference
import com.adasoraninda.dicodingstoryapp.service.remote.IRemoteDataSource
import com.adasoraninda.dicodingstoryapp.service.remote.response.LoginResponse
import com.adasoraninda.dicodingstoryapp.service.remote.response.LoginResultResponse
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
class LoginViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private lateinit var remoteDataSource: IRemoteDataSource
    private lateinit var validation: InputValidation
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var userPreference: UserPreference
    private lateinit var viewModel: LoginViewModel

    private val email = "email@email.com"
    private val password = "123456"
    private val loginResponse = LoginResponse(
        loginResult = LoginResultResponse(
            userId = "id", name = "name", token = "token"
        )
    )

    @Before
    fun setup() {
        validation = mock()
        remoteDataSource = mock()
        dataStore = mock()
        userPreference = UserPreference(dataStore)
        viewModel = LoginViewModel(remoteDataSource, userPreference, validation)
    }

    @Test
    fun login_success_test() = runTest {
        // arrange
        val inputCaptor = argumentCaptor<InputLogin>()
        val saveCaptor = argumentCaptor<(Preferences) -> Preferences>()
        val responseSuccess = loginResponse.apply {
            error = false
            message = "Success"
        }
        val mockResult = flowOf(responseSuccess)

        `when`(validation.validate(inputCaptor.capture()))
            .thenReturn(null)
        `when`(remoteDataSource.login(email, password))
            .thenReturn(mockResult)
        `when`(dataStore.updateData(saveCaptor.capture()))
            .thenReturn(null)

        // act
        viewModel.login(email, password)

        val inputUser = inputCaptor.firstValue
        val isSuccess = viewModel.loginSuccess.getOrAwaitValue().get()

        // assert
        verify(validation, only()).validate(inputCaptor.capture())
        verify(remoteDataSource, times(1)).login(email, password)
        verify(dataStore).updateData(saveCaptor.capture())
        Assert.assertEquals(email, inputUser.email)
        Assert.assertEquals(password, inputUser.password)
        Assert.assertNotNull(isSuccess)
        Assert.assertEquals(Unit, isSuccess)
    }

    @Test
    fun login_failure_test() = runTest {
        // arrange
        val inputCaptor = argumentCaptor<InputLogin>()
        val responseError = loginResponse.apply {
            error = true
            message = "Error"
        }
        val mockResult = flowOf(responseError)

        `when`(validation.validate(inputCaptor.capture()))
            .thenReturn(null)
        `when`(remoteDataSource.login(email, password))
            .thenReturn(mockResult)

        // act
        viewModel.login(email, password)

        val inputUser = inputCaptor.firstValue
        val errorMessage = viewModel.dialogInfoError.getOrAwaitValue()

        // assert
        verify(validation, only()).validate(inputCaptor.capture())
        verify(remoteDataSource, times(1)).login(email, password)
        Assert.assertEquals(email, inputUser.email)
        Assert.assertEquals(password, inputUser.password)
        Assert.assertNotNull(errorMessage)
        Assert.assertEquals(responseError.message, errorMessage)
    }

    @Test
    fun dismiss_error_dialog_test() {
        viewModel.dismissError()

        val result = viewModel.dialogInfoError.getOrAwaitValue()
        Assert.assertNull(result)
    }

}