package com.adasoraninda.dicodingstoryapp.common.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.adasoraninda.dicodingstoryapp.common.di.deps.RetrofitInstance
import com.adasoraninda.dicodingstoryapp.common.di.deps.UserPreferenceInstance
import com.adasoraninda.dicodingstoryapp.features.splash.SplashViewModel
import com.adasoraninda.dicodingstoryapp.model.dataStore
import com.adasoraninda.dicodingstoryapp.service.remote.RemoteDataSource
import com.adasoraninda.dicodingstoryapp.service.remote.api.BASE_URL
import com.adasoraninda.dicodingstoryapp.service.remote.api.DicodingStoryApi

class Injector(private val context: Context) {

    val userPreference by lazy { UserPreferenceInstance.get(context.dataStore) }

    val remoteService: DicodingStoryApi by lazy {
        RetrofitInstance.get(baseUrl = BASE_URL).create(DicodingStoryApi::class.java)
    }

    val remoteDataSource: RemoteDataSource by lazy {
        RemoteDataSource(remoteService)
    }

    val splashFactory by lazy {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return SplashViewModel(userPreference) as T
            }
        }
    }

}