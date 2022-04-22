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
import kotlinx.coroutines.runBlocking
import timber.log.Timber

class ListStoryViewModel(
    private val remoteDataSource: RemoteDataSource,
    private val userPreference: UserPreference
) : ViewModel() {

    private val _userData = MutableLiveData<User>()

    private val _storiesData = MutableLiveData<List<Story>>()
    val storiesData: LiveData<List<Story>> get() = _storiesData

    private val _profileDialog = MutableLiveData<User?>()
    val profileDialog: LiveData<User?> get() = _profileDialog

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    val enableMenu
        get() = Transformations.map(_userData) {
            it.token.isNotEmpty()
        }

    init {
        runBlocking {
            getUser()
            getStories()
        }
    }

    @VisibleForTesting
    fun getUser() = viewModelScope.launch {
        userPreference.getUser().collect(_userData::setValue)
    }

    fun getStories() = viewModelScope.launch {
        val token = _userData.value?.token

        if (token == null) {
            _errorMessage.value = ERROR_TOKEN_EMPTY
            return@launch
        }

        Timber.d(token)

        remoteDataSource.getStories(token.formatToken())
            .onStart { _loading.postValue(true) }
            .onCompletion { _loading.postValue(false) }
            .collect { result ->
                Timber.d(result.toString())
                result.fold(
                    onFailure = {
                        _errorMessage.postValue(it.message)
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
        _profileDialog.value = user
    }

    fun dismissProfileDialog() {
        _profileDialog.value = null
    }

    fun logout() = viewModelScope.launch {
        userPreference.logout()
    }

}