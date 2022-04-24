package com.adasoraninda.dicodingstoryapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.paging.PagingData
import com.adasoraninda.dicodingstoryapp.features.story.list.ListStoryViewModel
import com.adasoraninda.dicodingstoryapp.model.User
import com.adasoraninda.dicodingstoryapp.model.UserPreference
import com.adasoraninda.dicodingstoryapp.service.remote.IRemoteDataSource
import com.adasoraninda.dicodingstoryapp.service.remote.response.StoryResultResponse
import com.adasoraninda.dicodingstoryapp.utils.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock

@OptIn(ExperimentalCoroutinesApi::class)
class ListStoryViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var remoteDataSource: IRemoteDataSource
    private lateinit var userPreference: UserPreference
    private lateinit var viewModel: ListStoryViewModel

    private val user = User("id", "name", "token")
    private val stories = listOf(StoryResultResponse(id = "id"))

    @Before
    fun setup() {
        dataStore = mock()
        remoteDataSource = mock()
        userPreference = UserPreference(dataStore)
        viewModel = ListStoryViewModel(remoteDataSource, userPreference)
    }

    @Test
    fun initialize_test() {
        val tokenCaptor = argumentCaptor<String>()
        val sizeCaptor = argumentCaptor<Int>()
        val pagingData = PagingData.from(stories)
        val mockUser = flowOf(getUserPreferenceData(user))
        val mockStories = flowOf(pagingData)

        `when`(dataStore.data).thenReturn(mockUser)
        `when`(
            remoteDataSource.getPagingStories(
                tokenCaptor.capture(),
                sizeCaptor.capture()
            )
        ).thenReturn(mockStories)

        viewModel.initialize()

        val result = viewModel.initialize

        Assert.assertTrue(result)
    }

    @Test
    fun get_stories_success_test() = runTest {
        val tokenCaptor = argumentCaptor<String>()
        val sizeCaptor = argumentCaptor<Int>()
        val pagingData = PagingData.from(stories)
        val mockUser = flowOf(getUserPreferenceData(user))
        val mockStories = flowOf(pagingData)

        `when`(dataStore.data).thenReturn(mockUser)
        `when`(
            remoteDataSource.getPagingStories(
                tokenCaptor.capture(),
                sizeCaptor.capture()
            )
        ).thenReturn(mockStories)

        viewModel.getUser().join()
        viewModel.getStories()

        val storiesData = viewModel.storiesData.getOrAwaitValue()
        val tokenArgument = tokenCaptor.firstValue
        val sizeArgument = sizeCaptor.firstValue

        verify(remoteDataSource).getPagingStories(tokenCaptor.capture(), sizeCaptor.capture())
        Assert.assertNotNull(storiesData)
        Assert.assertEquals(user.token.formatToken(), tokenArgument)
        Assert.assertEquals(10, sizeArgument)
    }

    @Test
    fun get_stories_failure_test() = runTest {
        val mockResult = flowOf(getUserPreferenceData(User(token = "", name = "", userId = "")))

        `when`(dataStore.data).thenReturn(mockResult)

        viewModel.getStories()

        val errorMessage = viewModel.errorMessage.getOrAwaitValue()

        Assert.assertNotNull(errorMessage)
        Assert.assertEquals(ERROR_TOKEN_EMPTY, errorMessage)
    }

    @Test
    fun get_user_test() = runTest {
        val expectedValue = true
        val mockResult = flowOf(getUserPreferenceData(user))

        `when`(dataStore.data).thenReturn(mockResult)

        viewModel.getUser()

        val isMenuEnabled = viewModel.enableMenu.getOrAwaitValue()

        Assert.assertNotNull(isMenuEnabled)
        Assert.assertEquals(expectedValue, isMenuEnabled)
    }

    @Test
    fun show_profile_dialog_test() = runTest {
        val mockResult = flowOf(getUserPreferenceData(user))
        `when`(dataStore.data).thenReturn(mockResult)

        viewModel.getUser().join()
        viewModel.showProfileDialog()

        val profileDialog = viewModel.profileDialog.getOrAwaitValue()

        Assert.assertNotNull(profileDialog)
    }

    @Test
    fun dismiss_profile_dialog_test() {
        viewModel.dismissProfileDialog()

        val dialogProfile = viewModel.profileDialog.getOrAwaitValue()

        Assert.assertNull(dialogProfile)
    }

    @Test
    fun logout_test() = runTest {
        val argCaptor = argumentCaptor<suspend (MutablePreferences) -> Unit>()

        `when`(dataStore.edit(argCaptor.capture())).thenReturn(null)

        viewModel.logout()

        verify(dataStore).edit(argCaptor.capture())
    }

}