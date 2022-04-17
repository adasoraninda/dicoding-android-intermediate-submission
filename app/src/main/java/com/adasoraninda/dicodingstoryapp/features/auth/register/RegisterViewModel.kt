package com.adasoraninda.dicodingstoryapp.features.auth.register

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adasoraninda.dicodingstoryapp.common.validation.InputValidation
import com.adasoraninda.dicodingstoryapp.model.InputRegister
import com.adasoraninda.dicodingstoryapp.service.remote.RemoteDataSource
import com.adasoraninda.dicodingstoryapp.utils.*
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber

class RegisterViewModel(
    private val remoteDataSource: RemoteDataSource,
    private val validation: InputValidation
) : ViewModel() {

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> get() = _loading

    private val _errorMessage = MutableLiveEvent<Int>()
    val errorMessage: LiveEvent<Int> get() = _errorMessage

    private val _dialogInfoSuccess = MutableLiveData<String?>()
    val dialogInfoSuccess: LiveData<String?> get() = _dialogInfoSuccess

    private val _dialogInfoError = MutableLiveData<String?>()
    val dialogInfoError: LiveData<String?> get() = _dialogInfoError

    fun register(name: String?, email: String?, password: String?) = viewModelScope.launch {
        val isInputValid = checkInput(name, email, password)
        if (!isInputValid) return@launch

        remoteDataSource.register(name!!, email!!, password!!)
            .onStart { _loading.postValue(true) }
            .onCompletion { _loading.postValue(false) }
            .collect {
                Timber.d(it.toString())
                val isError = it.error ?: true
                if (isError) {
                    val message = it.message ?: EMPTY_ERROR
                    _dialogInfoError.postValue(message)
                    Timber.e(message)
                } else {
                    val message = it.message ?: EMPTY_SUCCESS
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

    @VisibleForTesting
    fun checkInput(name: String?, email: String?, password: String?): Boolean {
        val inputRegister = InputRegister(name, email, password)
        val isInputValid = validation.validate(inputRegister) {
            _errorMessage.value = Event(it)
        }

        return isInputValid
    }

}