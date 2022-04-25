package com.adasoraninda.dicodingstoryapp

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagingData
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.adasoraninda.dicodingstoryapp.features.story.list.ListStoryFragment
import com.adasoraninda.dicodingstoryapp.features.story.list.ListStoryViewModel
import com.adasoraninda.dicodingstoryapp.model.User
import com.adasoraninda.dicodingstoryapp.model.UserPreference
import com.adasoraninda.dicodingstoryapp.service.remote.IRemoteDataSource
import com.adasoraninda.dicodingstoryapp.service.remote.response.StoryResponse
import com.adasoraninda.dicodingstoryapp.utils.EspressoIdlingResource
import com.google.gson.Gson
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.argumentCaptor

@MediumTest
@RunWith(AndroidJUnit4::class)
@Suppress("UNCHECKED_CAST")
@OptIn(ExperimentalCoroutinesApi::class)
class ListStoryFragmentTest {

    @Mock
    private lateinit var dataStore: DataStore<Preferences>

    @Mock
    private lateinit var remoteDataSource: IRemoteDataSource

    private lateinit var userPreference: UserPreference
    private lateinit var viewModelFactory: ViewModelProvider.Factory

    private val user = User(token = "token", userId = "userId", name = "name")
    private val prefUser = preferencesOf(
        UserPreference.NAME_KEY to user.name,
        UserPreference.USER_ID_KEY to user.userId,
        UserPreference.TOKEN_KEY to user.token
    )

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        userPreference = UserPreference(dataStore)
        viewModelFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ListStoryViewModel(remoteDataSource, userPreference) as T
            }
        }

        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun get_stories_success() {
        val successResponse = Gson().fromJson(
            JsonConverter.readStringFromFile("success_response.json"),
            StoryResponse::class.java
        )

        testFragment(successResponse) {
            launchFragmentInContainer(
                themeResId = R.style.Theme_DicodingStoryApp,
                instantiate = { ListStoryFragment(viewModelFactory) }
            )

            onView(withId(R.id.app_bar))
                .check(matches(isDisplayed()))
            onView(withText(R.string.app_name))
                .check(matches(isDisplayed()))
            onView(withId(R.id.list_stories))
                .check(matches(isDisplayed()))
            onView(withId(R.id.list_stories))
                .perform(
                    RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                        hasDescendant(withText("Anonim"))
                    )
                )
        }
    }

    @Test
    fun get_stories_failure() {
        val errorResponse = Gson().fromJson(
            JsonConverter.readStringFromFile("error_response.json"),
            StoryResponse::class.java
        )

        testFragment(errorResponse) {
            launchFragmentInContainer(
                themeResId = R.style.Theme_DicodingStoryApp,
                instantiate = { ListStoryFragment(viewModelFactory) }
            )

            onView(withId(R.id.app_bar))
                .check(matches(isDisplayed()))
            onView(withText(R.string.app_name))
                .check(matches(isDisplayed()))
            onView(withId(R.id.text_error))
                .check(matches(isDisplayed()))
                .check(matches(withText(R.string.success_empty)))
        }
    }

    private fun testFragment(response: StoryResponse, action: () -> Unit) {
        val tokenCaptor = argumentCaptor<String>()
        val sizeCaptor = argumentCaptor<Int>()
        val stories = response.listStory ?: emptyList()
        val pagingData = PagingData.from(stories)
        val mockStories = flowOf(pagingData)

        `when`(dataStore.data).thenReturn(flowOf(prefUser))
        `when`(
            remoteDataSource.getPagingStories(
                tokenCaptor.capture(),
                sizeCaptor.capture()
            )
        ).thenReturn(mockStories)

        action()

        verify(dataStore, only()).data
        verify(remoteDataSource).getPagingStories(tokenCaptor.capture(), sizeCaptor.capture())
    }

}