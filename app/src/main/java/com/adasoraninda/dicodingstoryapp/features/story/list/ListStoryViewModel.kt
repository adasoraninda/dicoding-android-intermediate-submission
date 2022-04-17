package com.adasoraninda.dicodingstoryapp.features.story.list

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.*
import com.adasoraninda.dicodingstoryapp.model.Story
import com.adasoraninda.dicodingstoryapp.model.User
import com.adasoraninda.dicodingstoryapp.model.UserPreference
import com.adasoraninda.dicodingstoryapp.service.remote.RemoteDataSource
import com.adasoraninda.dicodingstoryapp.utils.*
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber

class ListStoryViewModel(
    private val remoteDataSource: RemoteDataSource,
    private val userPreference: UserPreference
) : ViewModel() {

    private val _userData = MutableLiveData<User>()

    private val _storiesData = MutableLiveData<List<Story>>()
    val storiesData: LiveData<List<Story>> get() = _storiesData

    private val _profileDialog = MutableLiveEvent<User?>()
    val profileDialog: LiveEvent<User?> get() = _profileDialog

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    val enableMenu
        get() = Transformations.map(_userData) {
            it.token.isNotEmpty()
        }

    init {
        initialize()
        getStories()
    }

    @VisibleForTesting
    fun initialize() = viewModelScope.launch {
        userPreference.getUser().collect(_userData::setValue)
    }

    fun getStories() = viewModelScope.launch {
        val token = _userData.value?.token

        if (token == null) {
            _errorMessage.value = EMPTY_TOKEN_ERROR
            return@launch
        }

        remoteDataSource.getStories(token.formatToken())
            .onStart { _loading.postValue(true) }
            .onCompletion { _loading.postValue(false) }
            .collect { result ->
                Timber.d(result.toString())
                result.fold(
                    onFailure = {
                        _errorMessage.value = it.message
                    },
                    onSuccess = {
                        val storiesRes = it.listStory ?: emptyList()
                        val stories = storiesRes.map { s ->
                            Story(
                                s.id.orEmpty(),
                                s.name.orEmpty(),
                                s.description.orEmpty(),
                                s.photoUrl.orEmpty(),
                                s.createdAt.orEmpty(),
                                s.latitude.orEmpty(),
                                s.longitude.orEmpty()
                            )
                        }
                        _storiesData.postValue(stories)
                    }
                )
            }
    }

    fun showProfileDialog() {
        val user = _userData.value ?: return
        _profileDialog.value = Event(user)
    }

    fun logout() = viewModelScope.launch {
        userPreference.logout()
    }

}