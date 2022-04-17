package com.adasoraninda.dicodingstoryapp.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class Event<T>(value: T) {
    private var _value: T? = value

    fun get(): T? = _value.also { _value = null }
}

typealias MutableLiveEvent<T> = MutableLiveData<Event<T>>
typealias LiveEvent<T> = LiveData<Event<T>>