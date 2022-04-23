package com.adasoraninda.dicodingstoryapp.features.story.list

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.adasoraninda.dicodingstoryapp.model.Story
import com.adasoraninda.dicodingstoryapp.model.User
import com.adasoraninda.dicodingstoryapp.model.UserPreference
import com.adasoraninda.dicodingstoryapp.service.remote.IRemoteDataSource
import com.adasoraninda.dicodingstoryapp.utils.ERROR_EMPTY
import com.adasoraninda.dicodingstoryapp.utils.ERROR_TOKEN_EMPTY
import com.adasoraninda.dicodingstoryapp.utils.formatToken
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber

class ListStoryViewModel(
    private val remoteDataSource: IRemoteDataSource,
    private val userPreference: UserPreference
) : ViewModel() {

    private val _userData = MutableLiveData<User>()

    private val _storiesData = MutableLiveData<PagingData<Story>>()
    val storiesData: LiveData<PagingData<Story>> get() = _storiesData

    private val _profileDialog = MutableLiveData<User?>()
    val profileDialog: LiveData<User?> get() = _profileDialog

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

    @VisibleForTesting
    fun getStories() = viewModelScope.launch {
        val token = _userData.value?.token

        if (token == null) {
            _errorMessage.value = ERROR_TOKEN_EMPTY
            return@launch
        }

        Timber.d(token)

        remoteDataSource.getPagingStories(token.formatToken())
            .cachedIn(this)
            .catch { _errorMessage.value = ERROR_EMPTY }
            .collect { result ->
                Timber.d(result.toString())

                val stories = result.map { s ->
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