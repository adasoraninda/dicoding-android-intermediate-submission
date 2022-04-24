package com.adasoraninda.dicodingstoryapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.adasoraninda.dicodingstoryapp.common.validation.InputValidation
import com.adasoraninda.dicodingstoryapp.features.story.add.AddStoryViewModel
import com.adasoraninda.dicodingstoryapp.model.InputAddStory
import com.adasoraninda.dicodingstoryapp.model.User
import com.adasoraninda.dicodingstoryapp.model.UserPreference
import com.adasoraninda.dicodingstoryapp.service.remote.IRemoteDataSource
import com.adasoraninda.dicodingstoryapp.service.remote.response.BaseResponse
import com.adasoraninda.dicodingstoryapp.utils.MainCoroutineRule
import com.adasoraninda.dicodingstoryapp.utils.getOrAwaitValue
import com.adasoraninda.dicodingstoryapp.utils.getUserPreferenceData
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
import org.mockito.kotlin.only

@OptIn(ExperimentalCoroutinesApi::class)
class AddStoryViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var userPreference: UserPreference
    private lateinit var remoteDataSource: IRemoteDataSource
    private lateinit var validation: InputValidation
    private lateinit var viewModel: AddStoryViewModel

    private val user = User("id", "name", "token")

    @Before
    fun setup() {
        dataStore = mock()
        remoteDataSource = mock()
        validation = mock()
        userPreference = UserPreference(dataStore)
        viewModel = AddStoryViewModel(userPreference, remoteDataSource, validation)
    }

    @Test
    fun initialize_test() {
        val mockResult = flowOf(getUserPreferenceData(user))

        `when`(dataStore.data).thenReturn(mockResult)

        viewModel.initialize()

        val result = viewModel.initialize

        Assert.assertTrue(result)
    }

    @Test
    fun add_story_success_test() = runTest {
        val description = "description"
        val inputCaptor = argumentCaptor<InputAddStory>()
        val successResponse = BaseResponse(error = false, message = "Success")
        val mockResponse = flowOf(successResponse)

        `when`(validation.validate(inputCaptor.capture())).thenReturn(null)
        `when`(remoteDataSource.addStory(inputCaptor.capture()))
            .thenReturn(mockResponse)

        viewModel.addStory(description)

        val input = inputCaptor.firstValue
        val dialogSuccess = viewModel.dialogInfoSuccess.getOrAwaitValue()

        verify(validation, only()).validate(inputCaptor.capture())
        verify(remoteDataSource, times(1)).addStory(inputCaptor.capture())
        Assert.assertNotNull(input)
        Assert.assertNotNull(dialogSuccess)
        Assert.assertEquals(description, input.description)
        Assert.assertEquals(successResponse.message, dialogSuccess)
    }

    @Test
    fun add_story_failure_test() = runTest {
        val description = "description"
        val inputCaptor = argumentCaptor<InputAddStory>()
        val errorResponse = BaseResponse(error = true, message = "Error")
        val mockResponse = flowOf(errorResponse)

        `when`(validation.validate(inputCaptor.capture())).thenReturn(null)
        `when`(remoteDataSource.addStory(inputCaptor.capture()))
            .thenReturn(mockResponse)

        viewModel.addStory(description)

        val input = inputCaptor.firstValue
        val dialogError = viewModel.dialogInfoError.getOrAwaitValue()

        verify(validation, only()).validate(inputCaptor.capture())
        verify(remoteDataSource, times(1)).addStory(inputCaptor.capture())
        Assert.assertNotNull(input)
        Assert.assertNotNull(dialogError)
        Assert.assertEquals(description, input.description)
        Assert.assertEquals(errorResponse.message, dialogError)
    }

    @Test
    fun get_user_test() = runTest {
        val mockResult = flowOf(getUserPreferenceData(user))

        `when`(dataStore.data).thenReturn(mockResult)

        viewModel.getUser()

        verify(dataStore, only()).data
    }

    @Test
    fun dismiss_success_dialog_test() {
        viewModel.dismissSuccessDialog()

        val messageSuccess = viewModel.dialogInfoSuccess.getOrAwaitValue()

        Assert.assertNull(messageSuccess)
    }

    @Test
    fun dismiss_error_dialog_test() {
        viewModel.dismissErrorDialog()

        val messageError = viewModel.dialogInfoError.getOrAwaitValue()

        Assert.assertNull(messageError)
    }

}