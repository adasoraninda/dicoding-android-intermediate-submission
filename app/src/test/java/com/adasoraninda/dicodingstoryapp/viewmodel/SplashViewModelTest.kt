package com.adasoraninda.dicodingstoryapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.adasoraninda.dicodingstoryapp.features.splash.SplashViewModel
import com.adasoraninda.dicodingstoryapp.model.UserPreference
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var userPreference: UserPreference
    private lateinit var viewModel: SplashViewModel

    @Before
    fun setup() {
        userPreference = mock()
        viewModel = SplashViewModel(userPreference)
    }


    @Test
    fun should_isLoggedIn_to_be_true_when_user_preference_data_is_not_null() = runTest {
        val expectedValue = true
        `when`(userPreference.isLoggedIn()).thenReturn(flowOf(true))
    }

    @Test
    fun should_isLoggedIn_to_be_false_when_user_preference_data_is_null() {

    }
}