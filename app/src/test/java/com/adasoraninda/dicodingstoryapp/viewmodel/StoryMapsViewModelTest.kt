package com.adasoraninda.dicodingstoryapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.adasoraninda.dicodingstoryapp.features.story.map.StoryMapsViewModel
import com.adasoraninda.dicodingstoryapp.model.User
import com.adasoraninda.dicodingstoryapp.model.UserPreference
import com.adasoraninda.dicodingstoryapp.service.remote.IRemoteDataSource
import com.adasoraninda.dicodingstoryapp.service.remote.response.StoryResponse
import com.adasoraninda.dicodingstoryapp.service.remote.response.StoryResultResponse
import com.adasoraninda.dicodingstoryapp.utils.MainCoroutineRule
import com.adasoraninda.dicodingstoryapp.utils.formatToken
import com.adasoraninda.dicodingstoryapp.utils.getOrAwaitValue
import com.adasoraninda.dicodingstoryapp.utils.getUserPreferenceData
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock

@OptIn(ExperimentalCoroutinesApi::class)
class StoryMapsViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var userPreference: UserPreference
    private lateinit var remoteDataSource: IRemoteDataSource
    private lateinit var viewModel: StoryMapsViewModel

    private val user = User("id", "name", "token")
    private val stories = listOf(StoryResultResponse(id = "id"))

    @Before
    fun setup() {
        dataStore = mock()
        remoteDataSource = mock()
        userPreference = UserPreference(dataStore)
        viewModel = StoryMapsViewModel(remoteDataSource, userPreference)
    }

    @Test
    fun initialize_test() {
        val storyResponse = StoryResponse(stories).apply {
            error = false
            message = "Success"
        }
        val resultResponse = Result.success(storyResponse)
        val argTokenCaptor = argumentCaptor<String>()
        val pageCaptor = argumentCaptor<Int>()
        val locationCaptor = argumentCaptor<Int>()
        val sizeCaptor = argumentCaptor<Int>()
        val mockUserData = flowOf(getUserPreferenceData(user))
        val mockResponse = flowOf(resultResponse)

        `when`(dataStore.data).thenReturn(mockUserData)
        `when`(
            remoteDataSource.getStories(
                token = argTokenCaptor.capture(),
                page = pageCaptor.capture(),
                size = sizeCaptor.capture(),
                location = locationCaptor.capture()
            )
        ).thenReturn(mockResponse)

        viewModel.initialize()

        val result = viewModel.initialize

        Assert.assertTrue(result)
    }

    @Test
    fun get_all_stories_success_test() = runTest {
        // arrange
        val storyResponse = StoryResponse(stories).apply {
            error = false
            message = "Success"
        }
        val resultResponse = Result.success(storyResponse)
        val argTokenCaptor = argumentCaptor<String>()
        val pageCaptor = argumentCaptor<Int>()
        val locationCaptor = argumentCaptor<Int>()
        val sizeCaptor = argumentCaptor<Int>()
        val mockUserData = flowOf(getUserPreferenceData(user))
        val mockResponse = flowOf(resultResponse)

        `when`(dataStore.data).thenReturn(mockUserData)
        `when`(
            remoteDataSource.getStories(
                token = argTokenCaptor.capture(),
                page = pageCaptor.capture(),
                size = sizeCaptor.capture(),
                location = locationCaptor.capture()
            )
        ).thenReturn(mockResponse)

        // act
        viewModel.getAllStories()

        val storiesData = viewModel.storiesData.getOrAwaitValue()
        val tokenArgument = argTokenCaptor.firstValue
        val pageArgument = pageCaptor.firstValue
        val locationArgument = locationCaptor.firstValue
        val sizeArgument = sizeCaptor.firstValue

        // assert
        verify(remoteDataSource).getStories(
            token = tokenArgument,
            page = pageArgument,
            location = locationArgument,
            size = sizeArgument
        )
        Assert.assertNotNull(storiesData)
        Assert.assertNotNull(tokenArgument)
        Assert.assertEquals(stories.size, storiesData.size)
        Assert.assertEquals(stories[0].id, storiesData[0].id)
        Assert.assertEquals(user.token.formatToken(), tokenArgument)
        Assert.assertEquals(1, pageArgument)
        Assert.assertEquals(1, locationArgument)
        Assert.assertEquals(10, sizeArgument)
    }

    @Test
    fun get_all_stories_failure_test() = runTest {
        // arrange
        val resultResponse = Result.failure<StoryResponse>(IllegalStateException("Error"))
        val argTokenCaptor = argumentCaptor<String>()
        val pageCaptor = argumentCaptor<Int>()
        val locationCaptor = argumentCaptor<Int>()
        val sizeCaptor = argumentCaptor<Int>()
        val mockUserData = flowOf(getUserPreferenceData(user))
        val mockResponse = flowOf(resultResponse)

        `when`(dataStore.data).thenReturn(mockUserData)
        `when`(
            remoteDataSource.getStories(
                token = argTokenCaptor.capture(),
                page = pageCaptor.capture(),
                size = sizeCaptor.capture(),
                location = locationCaptor.capture()
            )
        ).thenReturn(mockResponse)

        // act
        viewModel.getAllStories()

        val errorMessage = viewModel.errorMessage.getOrAwaitValue().get()
        val tokenArgument = argTokenCaptor.firstValue
        val pageArgument = pageCaptor.firstValue
        val locationArgument = locationCaptor.firstValue
        val sizeArgument = sizeCaptor.firstValue

        // assert
        verify(remoteDataSource).getStories(
            token = tokenArgument,
            page = pageArgument,
            location = locationArgument,
            size = sizeArgument
        )
        Assert.assertNotNull(errorMessage)
        Assert.assertNotNull(tokenArgument)
        Assert.assertEquals("Error", errorMessage)
        Assert.assertEquals(user.token.formatToken(), tokenArgument)
        Assert.assertEquals(1, pageArgument)
        Assert.assertEquals(1, locationArgument)
        Assert.assertEquals(10, sizeArgument)

    }

    @Test
    fun calculate_list_data_when_old_list_empty_test() {
        val oldList = listOf<String>()
        val newList = listOf("1")

        val resultList = viewModel.calculateListData(oldList, newList)

        assert(newList.isNotEmpty())
        assertEquals(newList.size, resultList.size)
        assertEquals(newList.toString(), resultList.toString())
    }

    @Test
    fun calculate_list_data_when_new_list_empty_test() {
        val oldList = listOf("1")
        val newList = listOf<String>()

        val resultList = viewModel.calculateListData(oldList, newList)

        assert(resultList.isNotEmpty())
        assertEquals(oldList.size, resultList.size)
        assertEquals(oldList.toString(), resultList.toString())
    }

    @Test
    fun calculate_list_data_when_old_new_list_not_empty_test() {
        val oldList = listOf("1", "2")
        val newList = listOf("3", "3")
        val expectedSize = 3
        val expectedValue = setOf(*oldList.toTypedArray(), *newList.toTypedArray())

        val resultList = viewModel.calculateListData(oldList, newList)

        assert(resultList.isNotEmpty())
        assertEquals(expectedSize, resultList.size)
        assertEquals(expectedValue.toString(), resultList.toString())
    }

    @Test
    fun set_camera_focus_test() {
        val data: Array<Double> = arrayOf(1.0, 1.0)

        viewModel.setCameraFocus(data[0], data[1])

        val latLonData = viewModel.cameraFocus.getOrAwaitValue()

        assertNotNull(latLonData)
        assertEquals(data.size, latLonData.size)
        assertEquals(data[0], latLonData[0])
        assertEquals(data[1], latLonData[1])
    }

    @Test
    fun show_dialog_test() {
        viewModel.showDialog()

        val isShow = viewModel.showDialog.getOrAwaitValue()

        assertNotNull(isShow)
        assertEquals(true, isShow)
    }

    @Test
    fun dismiss_dialog_test() {
        viewModel.dismissDialog()

        val isShow = viewModel.showDialog.getOrAwaitValue()

        assertNotNull(isShow)
        assertEquals(false, isShow)
    }

}