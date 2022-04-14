package com.adasoraninda.dicodingstoryapp

import android.app.Application
import androidx.viewbinding.BuildConfig
import com.adasoraninda.dicodingstoryapp.common.di.Injector
import timber.log.Timber

class DicodingStoryApp : Application() {

    val injector by lazy { Injector(this) }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

}