package com.adasoraninda.dicodingstoryapp.common.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.adasoraninda.dicodingstoryapp.features.auth.login.LoginValidation
import com.adasoraninda.dicodingstoryapp.features.auth.login.LoginViewModel
import com.adasoraninda.dicodingstoryapp.features.auth.register.RegisterValidation
import com.adasoraninda.dicodingstoryapp.features.auth.register.RegisterViewModel
import com.adasoraninda.dicodingstoryapp.features.story.map.StoryMapsViewModel
import com.adasoraninda.dicodingstoryapp.features.splash.SplashViewModel
import com.adasoraninda.dicodingstoryapp.features.story.add.AddStoryValidation
import com.adasoraninda.dicodingstoryapp.features.story.add.AddStoryViewModel
import com.adasoraninda.dicodingstoryapp.features.story.list.ListStoryViewModel
import com.adasoraninda.dicodingstoryapp.model.dataStore
import com.adasoraninda.dicodingstoryapp.service.remote.RemoteDataSource
import com.adasoraninda.dicodingstoryapp.service.remote.api.BASE_URL
import com.adasoraninda.dicodingstoryapp.service.remote.api.DicodingStoryApi
import retrofit2.converter.gson.GsonConverterFactory

@Suppress("UNCHECKED_CAST")
class Injector(private val context: Context) {

    val userPreference by lazy { UserPreferenceInstance.get(context.dataStore) }

    private val remoteService: DicodingStoryApi by lazy {
        RetrofitInstance.get(
            baseUrl = BASE_URL,
            client = OkHttpClientInstance.get(),
            converter = GsonConverterFactory.create()
        ).create(DicodingStoryApi::class.java)
    }

    val remoteDataSource: RemoteDataSource by lazy {
        RemoteDataSource(remoteService)
    }

    val storyMapsFactory by lazy {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return StoryMapsViewModel(
                    remoteDataSource,
                    userPreference,
                ) as T
            }
        }
    }

    val addStoryFactory by lazy {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AddStoryViewModel(
                    userPreference,
                    remoteDataSource,
                    AddStoryValidation()
                ) as T
            }
        }
    }

    val listStoryFactory by lazy {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ListStoryViewModel(remoteDataSource, userPreference) as T
            }
        }
    }

    val registerFactory by lazy {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return RegisterViewModel(remoteDataSource, RegisterValidation()) as T
            }
        }
    }

    val loginFactory by lazy {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return LoginViewModel(remoteDataSource, userPreference, LoginValidation()) as T
            }
        }
    }

    val splashFactory by lazy {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SplashViewModel(userPreference) as T
            }
        }
    }

}