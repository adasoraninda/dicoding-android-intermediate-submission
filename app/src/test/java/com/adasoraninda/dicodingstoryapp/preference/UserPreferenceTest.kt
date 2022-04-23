package com.adasoraninda.dicodingstoryapp.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesOf
import com.adasoraninda.dicodingstoryapp.model.User
import com.adasoraninda.dicodingstoryapp.model.UserPreference
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
class UserPreferenceTest {

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var userPreference: UserPreference

    @Before
    fun setup() {
        dataStore = mock()
        userPreference = UserPreference(dataStore)
    }

    @Test
    fun should_return_data_user_when_getUser_being_called() = runTest {
        val user = User(userId = "id", name = "name", token = "token")
        val prefUser = getUserPreferenceData(user)

        `when`(dataStore.data).thenReturn(flowOf(prefUser))

        val result = userPreference.getUser().firstOrNull()

        verify(dataStore).data.map(::mapToUser)
        Assert.assertNotNull(result)
        Assert.assertEquals(user.userId, result?.userId)
        Assert.assertEquals(user.name, result?.name)
        Assert.assertEquals(user.token, result?.token)
    }

    @Test
    fun should_return_true_when_isLoggedIn_true() = runTest {
        val expectedValue = true
        val user = User(userId = "id", name = "name", token = "token")
        val prefUser = getUserPreferenceData(user)

        `when`(dataStore.data).thenReturn(flowOf(prefUser))

        val result = userPreference.isLoggedIn().firstOrNull()

        verify(dataStore).data.map(::mapToUser)
        Assert.assertNotNull(result)
        Assert.assertEquals(expectedValue, result)
    }

    @Test
    fun should_return_false_when_isLoggedIn_false() = runTest {
        val expectedValue = false
        val user = User(userId = "id", name = "name", token = "")
        val prefUser = getUserPreferenceData(user)

        `when`(dataStore.data).thenReturn(flowOf(prefUser))

        val result = userPreference.isLoggedIn().firstOrNull()

        verify(dataStore).data.map(::mapToUser)
        Assert.assertNotNull(result)
        Assert.assertEquals(expectedValue, result)
    }

    @Test
    fun should_has_data_user_when_saveUser_being_called() = runTest {
        val user = User(userId = "id", name = "name", token = "")

        userPreference.saveUser(user)
    }

    @Test
    fun should_data_user_empty_when_logout_being_called() = runTest {
        userPreference.logout()

        verify(dataStore).edit {  } // ini tidak bisa
    }

    private fun mapToUser(pref: Preferences): User {
        return User(
            userId = pref[UserPreference.USER_ID_KEY].orEmpty(),
            name = pref[UserPreference.NAME_KEY].orEmpty(),
            token = pref[UserPreference.TOKEN_KEY].orEmpty(),
        )
    }

    private fun getUserPreferenceData(user: User): Preferences {
        return preferencesOf(
            UserPreference.USER_ID_KEY to user.userId,
            UserPreference.NAME_KEY to user.name,
            UserPreference.TOKEN_KEY to user.token,
        )
    }

}