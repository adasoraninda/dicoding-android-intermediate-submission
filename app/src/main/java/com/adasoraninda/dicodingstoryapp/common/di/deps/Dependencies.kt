package com.adasoraninda.dicodingstoryapp.common.di.deps

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.adasoraninda.dicodingstoryapp.model.UserPreference
import com.adasoraninda.dicodingstoryapp.service.remote.api.BASE_URL
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    @Volatile
    private var INSTANCE: Retrofit? = null

    fun get(
        baseUrl: String = BASE_URL,
        converter: Converter.Factory = GsonConverterFactory.create()
    ): Retrofit {
        return INSTANCE ?: synchronized(this) {
            val instance = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(converter)
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