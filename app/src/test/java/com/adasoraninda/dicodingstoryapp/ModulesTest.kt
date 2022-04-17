package com.adasoraninda.dicodingstoryapp

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.adasoraninda.dicodingstoryapp.common.di.RetrofitInstance
import com.adasoraninda.dicodingstoryapp.common.di.UserPreferenceInstance
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock

class ModulesTest {

    @Test
    fun should_user_preference_dependencies_is_a_singleton() {
        val dataStore = mock<DataStore<Preferences>>()
        val instance1 = UserPreferenceInstance.get(dataStore)
        val instance2 = UserPreferenceInstance.get(dataStore)

        assertEquals(instance1, instance2)
    }

    @Test
    fun should_retrofit_dependencies_is_a_singleton() {
        val instance1 = RetrofitInstance.get()
        val instance2 = RetrofitInstance.get()

        assertEquals(instance1, instance2)
    }

}