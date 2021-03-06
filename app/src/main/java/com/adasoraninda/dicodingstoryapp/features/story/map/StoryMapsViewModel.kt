package com.adasoraninda.dicodingstoryapp.features.story.map

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adasoraninda.dicodingstoryapp.model.Story
import com.adasoraninda.dicodingstoryapp.model.UserPreference
import com.adasoraninda.dicodingstoryapp.service.remote.IRemoteDataSource
import com.adasoraninda.dicodingstoryapp.utils.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber

class StoryMapsViewModel(
    private val remoteDataSource: IRemoteDataSource,
    private val userPreference: UserPreference
) : ViewModel() {

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> get() = _loading

    private val _storiesData = MutableLiveData<List<Story>>()
    val storiesData: LiveData<List<Story>> get() = _storiesData

    private val _errorMessage = MutableLiveEvent<String?>()
    val errorMessage: LiveEvent<String?> get() = _errorMessage

    private val _showDialog = MutableLiveData<Boolean>()
    val showDialog: LiveData<Boolean> get() = _showDialog

    private val _cameraFocus = MutableLiveData<Array<Double>>()
    val cameraFocus: LiveData<Array<Double>> get() = _cameraFocus

    private var page: Int = 0
    private val location: Int = 1
    var initialize = false
        private set

    fun initialize() {
        getAllStories()
        initialize = true
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getAllStories() = viewModelScope.launch {
        userPreference.getUser()
            .map { it.token }
            .flatMapLatest { token ->
                remoteDataSource.getStories(token.formatToken(), page = ++page, location = location)
                    .onStart { _loading.postValue(true) }
                    .onCompletion { _loading.postValue(false) }
            }
            .collect { result ->
                Timber.d(result.toString())
                result.fold(
                    onFailure = {
                        _errorMessage.postValue(Event(it.message))
                    },
                    onSuccess = {
                        Timber.d("Page: $page")

                        val storiesRes = it.listStory ?: emptyList()

                        if (storiesRes.isEmpty()) {
                            page -= 1
                            _errorMessage.value = Event(SUCCESS_EMPTY)
                        } else {
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

                            val storiesData = calculateListData(
                                _storiesData.value ?: emptyList(),
                                stories
                            )

                            _storiesData.postValue(storiesData)
                        }
                    }
                )
            }
    }

    @VisibleForTesting
    fun <T> calculateListData(oldList: List<T>, newList: List<T>): List<T> {
        if (oldList.isEmpty()) return newList
        if (newList.isEmpty()) return oldList

        val listData = mutableSetOf<T>()

        runCatching {
            listData.addAll(oldList + newList)
        }.onFailure {
            it.printStackTrace()
            Timber.e(it.message)
        }

        return listData.toList()
    }

    fun setCameraFocus(lat: Double, lon: Double) {
        _cameraFocus.value = arrayOf(lat, lon)
    }

    fun showDialog() {
        _showDialog.value = true
    }

    fun dismissDialog() {
        _showDialog.value = false
    }

}