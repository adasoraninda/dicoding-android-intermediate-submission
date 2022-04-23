package com.adasoraninda.dicodingstoryapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.adasoraninda.dicodingstoryapp.features.splash.SplashViewModel
import com.adasoraninda.dicodingstoryapp.model.UserPreference
import com.adasoraninda.dicodingstoryapp.utils.MainCoroutineRule
import com.adasoraninda.dicodingstoryapp.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @Test
    fun should_isLoggedIn_to_be_true_when_user_preference_data_is_not_null() = runTest {
        val expectedValue = true
        val userPreference = FakeUserPreference(expectedValue, mock())
        val viewModel = SplashViewModel(userPreference)

        val result = viewModel.isLoggedIn.getOrAwaitValue()

        Assert.assertNotNull(result)
        Assert.assertEquals(expectedValue, result)
    }

    @Test
    fun should_isLoggedIn_to_be_false_when_user_preference_data_is_null() = runTest {
        val expectedValue = false
        val userPreference = FakeUserPreference(expectedValue, mock())
        val viewModel = SplashViewModel(userPreference)

        val result = viewModel.isLoggedIn.getOrAwaitValue()

        Assert.assertNotNull(result)
        Assert.assertEquals(expectedValue, result)
    }

    inner class FakeUserPreference(
        private val resultValue: Boolean,
        dataStore: DataStore<Preferences>
    ) : UserPreference(dataStore) {

        override fun isLoggedIn(): Flow<Boolean> {
            return flowOf(resultValue)
        }

    }
}