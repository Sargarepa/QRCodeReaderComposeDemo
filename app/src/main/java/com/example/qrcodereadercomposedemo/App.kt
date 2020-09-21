package com.example.qrcodereadercomposedemo

import android.app.Application
import com.example.qrcodereadercomposedemo.di.viewModelsModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin

class App : Application(), KoinComponent {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(viewModelsModule)
        }
    }

}