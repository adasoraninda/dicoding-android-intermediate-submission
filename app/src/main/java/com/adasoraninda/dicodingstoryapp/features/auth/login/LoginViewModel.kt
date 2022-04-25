package com.adasoraninda.dicodingstoryapp.features.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adasoraninda.dicodingstoryapp.R
import com.adasoraninda.dicodingstoryapp.common.validation.InputValidation
import com.adasoraninda.dicodingstoryapp.model.InputLogin
import com.adasoraninda.dicodingstoryapp.model.User
import com.adasoraninda.dicodingstoryapp.model.UserPreference
import com.adasoraninda.dicodingstoryapp.service.remote.IRemoteDataSource
import com.adasoraninda.dicodingstoryapp.utils.ERROR_EMPTY
import com.adasoraninda.dicodingstoryapp.utils.Event
import com.adasoraninda.dicodingstoryapp.utils.LiveEvent
import com.adasoraninda.dicodingstoryapp.utils.MutableLiveEvent
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber

class LoginViewModel(
    private val remoteDataSource: IRemoteDataSource,
    private val userPreference: UserPreference,
    private val validation: InputValidation
) : ViewModel() {

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> get() = _loading

    private val _errorMessage = MutableLiveEvent<Int>()
    val errorMessage: LiveEvent<Int> get() = _errorMessage

    private val _dialogInfoError = MutableLiveData<String?>()
    val dialogInfoError: LiveData<String?> get() = _dialogInfoError

    private val _loginSuccess = MutableLiveEvent<Unit>()
    val loginSuccess: LiveEvent<Unit> get() = _loginSuccess

    fun login(email: String?, password: String?) = viewModelScope.launch {
        val messageValidation = validation.validate(InputLogin(email, password))

        if (messageValidation != null) {
            _errorMessage.value = Event(messageValidation)
            return@launch
        }

        remoteDataSource.login(email!!, password!!)
            .onStart { _loading.postValue(true) }
            .onCompletion { _loading.postValue(false) }
            .collect {
                Timber.d(it.toString())
                val isError = it.error ?: true
                val message = it.message ?: ERROR_EMPTY
                if (isError) {
                    _dialogInfoError.postValue(message)
                    Timber.e(message)
                } else {
                    val id = it.loginResult?.userId.orEmpty()
                    val name = it.loginResult?.name.orEmpty()
                    val token = it.loginResult?.token.orEmpty()

                    if (token.isEmpty()) {
                        _errorMessage.value = Event(R.string.error_occurred)
                    } else {
                        val user = User(id, name, token)
                        userPreference.saveUser(user)
                        _loginSuccess.postValue(Event(Unit))
                    }
                }
            }
    }

    fun dismissErrorDialog() {
        _dialogInfoError.value = null
    }

}