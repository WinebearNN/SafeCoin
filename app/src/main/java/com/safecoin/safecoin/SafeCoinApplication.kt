package com.safecoin.safecoin

import android.app.Application
import com.safecoin.safecoin.di.AppContainer

class SafeCoinApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
