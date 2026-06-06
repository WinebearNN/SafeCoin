package com.safecoin.safecoin.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.safecoin.safecoin.presentation.SafeCoinDependencies
import com.safecoin.safecoin.presentation.analytics.AnalyticsScreen
import com.safecoin.safecoin.presentation.analytics.AnalyticsViewModel
import com.safecoin.safecoin.presentation.home.HomeScreen
import com.safecoin.safecoin.presentation.home.HomeViewModel
import com.safecoin.safecoin.presentation.profile.ProfileScreen
import com.safecoin.safecoin.presentation.profile.ProfileViewModel

@Composable
fun SafeCoinNavHost(
    container: SafeCoinDependencies,
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier,
    ) {
        composable(Routes.HOME) {
            val viewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.factory(container.financeRepository),
            )
            HomeScreen(viewModel = viewModel)
        }
        composable(Routes.ANALYTICS) {
            val viewModel: AnalyticsViewModel = viewModel(
                factory = AnalyticsViewModel.factory(
                    container.financeRepository,
                    container.reportExporter,
                    container.aiAnalysisRepository,
                ),
            )
            AnalyticsScreen(viewModel = viewModel)
        }
        composable(Routes.PROFILE) {
            val viewModel: ProfileViewModel = viewModel(
                factory = ProfileViewModel.factory(container.settingsRepository),
            )
            ProfileScreen(viewModel = viewModel)
        }
    }
}
