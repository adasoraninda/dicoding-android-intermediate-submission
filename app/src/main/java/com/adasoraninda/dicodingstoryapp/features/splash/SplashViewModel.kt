package com.adasoraninda.dicodingstoryapp.features.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.adasoraninda.dicodingstoryapp.model.UserPreference
import kotlinx.coroutines.Dispatchers

class SplashViewModel(preference: UserPreference) : ViewModel() {

    val isLoggedIn = preference.isLoggedIn().asLiveData(Dispatchers.Main)

}