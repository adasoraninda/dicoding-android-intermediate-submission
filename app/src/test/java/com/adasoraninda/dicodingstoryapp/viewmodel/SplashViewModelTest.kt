package com.adasoraninda.dicodingstoryapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import com.adasoraninda.dicodingstoryapp.features.splash.SplashViewModel
import com.adasoraninda.dicodingstoryapp.model.UserPreference
import com.adasoraninda.dicodingstoryapp.model.UserPreference.Companion.TOKEN_KEY
import com.adasoraninda.dicodingstoryapp.utils.MainCoroutineRule
import com.adasoraninda.dicodingstoryapp.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var userPreference: UserPreference
    private lateinit var viewModel: SplashViewModel

    @Before
    fun setup() {
        dataStore = mock()
        userPreference = UserPreference(dataStore)
        viewModel = SplashViewModel(userPreference)
    }

    @Test
    fun should_isLoggedIn_to_be_true_when_user_preference_data_is_not_null() = runTest {
        val expectedValue = true

        `when`(dataStore.data).thenReturn(flowOf(preferencesOf(TOKEN_KEY to "TOKEN")))

        val result = viewModel.isLoggedIn.getOrAwaitValue()

        Assert.assertNotNull(result)
        Assert.assertEquals(expectedValue, result)
    }

    @Test
    fun should_isLoggedIn_to_be_false_when_user_preference_data_is_null() = runTest {
        val expectedValue = false

        `when`(dataStore.data).thenReturn(flowOf(preferencesOf(TOKEN_KEY to "")))

        val result = viewModel.isLoggedIn.getOrAwaitValue()

        Assert.assertNotNull(result)
        Assert.assertEquals(expectedValue, result)
    }

}