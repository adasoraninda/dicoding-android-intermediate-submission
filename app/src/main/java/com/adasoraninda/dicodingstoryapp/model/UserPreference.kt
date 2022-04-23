package com.adasoraninda.dicodingstoryapp.model

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

open class UserPreference(private val dataStore: DataStore<Preferences>) {

    open fun getUser(): Flow<User> {
        return dataStore.data.map { pref ->
            User(
                userId = pref[USER_ID_KEY].orEmpty(),
                name = pref[NAME_KEY].orEmpty(),
                token = pref[TOKEN_KEY].orEmpty(),
            )
        }
    }

    open fun isLoggedIn(): Flow<Boolean> {
        return dataStore.data.map { pref ->
            pref[TOKEN_KEY].isNullOrEmpty().not()
        }
    }

    suspend fun saveUser(user: User) {
        dataStore.edit { pref ->
            pref[USER_ID_KEY] = user.userId
            pref[NAME_KEY] = user.name
            pref[TOKEN_KEY] = user.token
        }
    }

    suspend fun logout() {
        dataStore.edit { pref ->
            pref[USER_ID_KEY] = ""
            pref[NAME_KEY] = ""
            pref[TOKEN_KEY] = ""
        }
    }

    companion object {
        val USER_ID_KEY = stringPreferencesKey("userId")
        val NAME_KEY = stringPreferencesKey("name")
        val TOKEN_KEY = stringPreferencesKey("token")
    }

}