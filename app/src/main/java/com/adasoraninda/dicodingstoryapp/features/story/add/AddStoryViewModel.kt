package com.adasoraninda.dicodingstoryapp.features.story.add

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adasoraninda.dicodingstoryapp.common.validation.InputValidation
import com.adasoraninda.dicodingstoryapp.model.InputAddStory
import com.adasoraninda.dicodingstoryapp.model.User
import com.adasoraninda.dicodingstoryapp.model.UserPreference
import com.adasoraninda.dicodingstoryapp.service.remote.IRemoteDataSource
import com.adasoraninda.dicodingstoryapp.utils.*
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File

class AddStoryViewModel(
    private val userPreference: UserPreference,
    private val remoteDataSource: IRemoteDataSource,
    private val validation: InputValidation
) : ViewModel() {

    private val _errorMessage = MutableLiveEvent<Int>()
    val errorMessage: LiveEvent<Int> get() = _errorMessage

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> get() = _loading

    private val _dialogInfoSuccess = MutableLiveData<String?>()
    val dialogInfoSuccess: LiveData<String?> get() = _dialogInfoSuccess

    private val _dialogInfoError = MutableLiveData<String?>()
    val dialogInfoError: LiveData<String?> get() = _dialogInfoError

    private val _userData = MutableLiveData<User>()

    var imageFile: File? = null
    var location: Location? = null
    var initialize = false
        private set

    fun initialize() {
        getUser()
        initialize = true
    }

    fun getUser() = viewModelScope.launch {
        userPreference.getUser().collect(_userData::setValue)
    }

    fun addStory(description: String?) = viewModelScope.launch {
        val token = _userData.value?.token

        val inputAddStory = InputAddStory(
            token,
            imageFile,
            description,
            lat = location?.latitude,
            lon = location?.longitude
        )
        val messageValidation = validation.validate(inputAddStory)
        if (messageValidation != null) {
            _errorMessage.value = Event(messageValidation)
            return@launch
        }

        Timber.d(inputAddStory.toString())

        remoteDataSource.addStory(inputAddStory)
            .onStart { _loading.postValue(true) }
            .onCompletion { _loading.postValue(false) }
            .collect {
                Timber.d(it.toString())
                val isError = it.error ?: true
                if (isError) {
                    val message = it.message ?: ERROR_EMPTY
                    _dialogInfoError.postValue(message)
                    Timber.e(message)
                } else {
                    val message = it.message ?: SUCCESS_EMPTY
                    _dialogInfoSuccess.postValue(message)
                }
            }
    }

    fun dismissSuccessDialog() {
        _dialogInfoSuccess.value = null
    }

    fun dismissErrorDialog() {
        _dialogInfoError.value = null
    }
}