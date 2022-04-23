package com.adasoraninda.dicodingstoryapp.features.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.adasoraninda.dicodingstoryapp.model.UserPreference
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

class SplashViewModel(
    preference: UserPreference, dispatcher: CoroutineContext = Dispatchers.Main
) : ViewModel() {

    val isLoggedIn = preference.isLoggedIn().asLiveData(dispatcher)

}