package com.adasoraninda.dicodingstoryapp.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.adasoraninda.dicodingstoryapp.model.User
import com.adasoraninda.dicodingstoryapp.model.UserPreference
import com.adasoraninda.dicodingstoryapp.utils.getUserPreferenceData
import com.adasoraninda.dicodingstoryapp.utils.mapUserPreferenceToUser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
class UserPreferenceTest {

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var userPreference: UserPreference

    private val user = User(userId = "id", name = "name", token = "token")

    @Before
    fun setup() {
        dataStore = mock()
        userPreference = UserPreference(dataStore)
    }

    @Test
    fun should_return_data_user_when_getUser_being_called() = runTest {
        val prefUser = getUserPreferenceData(user)

        `when`(dataStore.data).thenReturn(flowOf(prefUser))

        val result = userPreference.getUser().firstOrNull()

        verify(dataStore).data.map(::mapUserPreferenceToUser)
        Assert.assertNotNull(result)
        Assert.assertEquals(user.userId, result?.userId)
        Assert.assertEquals(user.name, result?.name)
        Assert.assertEquals(user.token, result?.token)
    }

    @Test
    fun should_return_true_when_isLoggedIn_true() = runTest {
        val expectedValue = true
        val prefUser = getUserPreferenceData(user)

        `when`(dataStore.data).thenReturn(flowOf(prefUser))

        val result = userPreference.isLoggedIn().firstOrNull()

        verify(dataStore).data.map(::mapUserPreferenceToUser)
        Assert.assertNotNull(result)
        Assert.assertEquals(expectedValue, result)
    }

    @Test
    fun should_return_false_when_isLoggedIn_false() = runTest {
        val expectedValue = false
        val user = user.copy(token = "")
        val prefUser = getUserPreferenceData(user)

        `when`(dataStore.data).thenReturn(flowOf(prefUser))

        val result = userPreference.isLoggedIn().firstOrNull()

        verify(dataStore).data.map(::mapUserPreferenceToUser)
        Assert.assertNotNull(result)
        Assert.assertEquals(expectedValue, result)
    }

    @Test
    fun save_user_test() = runTest {
        val argCaptor = argumentCaptor<(Preferences) -> Preferences>()

        `when`(dataStore.updateData(argCaptor.capture())).thenReturn(null)

        userPreference.saveUser(user)

        verify(dataStore).updateData(argCaptor.capture())
    }

    @Test
    fun logout_test() = runTest {
        val argCaptor = argumentCaptor<(MutablePreferences) -> Unit>()

        `when`(dataStore.edit(argCaptor.capture())).thenReturn(null)

        userPreference.logout()

        verify(dataStore).edit(argCaptor.capture())
    }

}