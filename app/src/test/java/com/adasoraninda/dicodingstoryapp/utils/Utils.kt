package com.adasoraninda.dicodingstoryapp.utils

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import com.adasoraninda.dicodingstoryapp.model.User
import com.adasoraninda.dicodingstoryapp.model.UserPreference

fun getUserPreferenceData(user: User): Preferences {
    return preferencesOf(
        UserPreference.USER_ID_KEY to user.userId,
        UserPreference.NAME_KEY to user.name,
        UserPreference.TOKEN_KEY to user.token,
    )
}

fun mapUserPreferenceToUser(pref: Preferences): User {
    return User(
        userId = pref[UserPreference.USER_ID_KEY].orEmpty(),
        name = pref[UserPreference.NAME_KEY].orEmpty(),
        token = pref[UserPreference.TOKEN_KEY].orEmpty(),
    )
}