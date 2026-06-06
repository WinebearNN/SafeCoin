package com.safecoin.safecoin.presentation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.safecoin.safecoin.design.theme.SafeCoinLayout
import com.safecoin.safecoin.design.theme.SafeCoinTheme
import com.safecoin.safecoin.domain.model.ThemeMode
import com.safecoin.safecoin.presentation.navigation.SafeCoinBottomBar
import com.safecoin.safecoin.presentation.navigation.SafeCoinNavHost
import kotlinx.coroutines.flow.map

@Composable
fun SafeCoinApp(container: SafeCoinDependencies) {
    val themeMode by container.settingsRepository
        .observeSettings()
        .map { it.darkTheme }
        .collectAsState(initial = ThemeMode.SYSTEM)

    SafeCoinTheme(themeMode = themeMode) {
        val navController = rememberNavController()
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = { SafeCoinBottomBar(navController) },
        ) {
            SafeCoinNavHost(
                container = container,
                navController = navController,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(top = SafeCoinLayout.ScreenTopPadding)
                    .padding(bottom = SafeCoinLayout.BottomNavigationHeight),
            )
        }
    }
}
