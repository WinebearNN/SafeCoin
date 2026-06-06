package com.safecoin.safecoin

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.safecoin.safecoin.locale.LocaleManager
import com.safecoin.safecoin.presentation.SafeCoinApp
import kotlinx.coroutines.flow.map

class MainActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleManager.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = (application as SafeCoinApplication).container
        setContent {
            val language by container.settingsRepository
                .observeSettings()
                .map { it.language.code }
                .collectAsState(initial = LocaleManager.readLanguageCode(this))

            var appliedLanguage by remember { mutableStateOf(language) }
            LaunchedEffect(language) {
                if (language != appliedLanguage) {
                    appliedLanguage = language
                    recreate()
                }
            }

            Scaffold(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .padding(it)
                        .fillMaxSize()
                        .navigationBarsPadding(),
                ) {
                    SafeCoinApp(container = container)
                }
            }
        }
    }
}

