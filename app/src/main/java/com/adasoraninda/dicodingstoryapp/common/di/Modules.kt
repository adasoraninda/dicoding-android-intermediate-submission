package com.adasoraninda.dicodingstoryapp.common.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.adasoraninda.dicodingstoryapp.BuildConfig
import com.adasoraninda.dicodingstoryapp.model.UserPreference
import com.adasoraninda.dicodingstoryapp.service.remote.api.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object OkHttpClientInstance {
    private val loggingInterceptor =
        HttpLoggingInterceptor().setLevel(
            if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        )

    fun get(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }
}

object RetrofitInstance {
    @Volatile
    private var INSTANCE: Retrofit? = null

    fun get(
        baseUrl: String = BASE_URL,
        converter: Converter.Factory = GsonConverterFactory.create(),
        client: OkHttpClient = OkHttpClient(),
    ): Retrofit {
        return INSTANCE ?: synchronized(this) {
            val instance = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(converter)
                .client(client)
                .build()

            INSTANCE = instance
            instance
        }
    }
}

object UserPreferenceInstance {
    @Volatile
    private var INSTANCE: UserPreference? = null

    fun get(dataStore: DataStore<Preferences>): UserPreference {
        return INSTANCE ?: synchronized(this) {
            val instance = UserPreference(dataStore)
            INSTANCE = instance
            instance
        }
    }

}