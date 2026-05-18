package com.safecoin.safecoin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.safecoin.safecoin.presentation.SafeCoinApp

class MainActivity : ComponentActivity() {
      override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            val container = (application as SafeCoinApplication).container
            setContent {
                  Scaffold(modifier = Modifier.fillMaxSize()) {
                        Box(
                              modifier = Modifier
                                  .padding()
                                  .fillMaxSize()
                                  .navigationBarsPadding(),
                        ) {
                              SafeCoinApp(container = container)
                        }
                  }
            }
      }
}

