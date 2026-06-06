package com.safecoin.safecoin

import android.app.Application
import com.safecoin.safecoin.data.local.LocalePreferences
import com.safecoin.safecoin.di.AppContainer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class SafeCoinApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        runBlocking(Dispatchers.IO) {
            val language = container.settingsRepository.observeSettings().first().language
            LocalePreferences.saveLanguageCode(this@SafeCoinApplication, language.code)
        }
    }
}
