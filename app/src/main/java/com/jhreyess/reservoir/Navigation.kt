package com.jhreyess.reservoir

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jhreyess.reservoir.Destinations.HOME_ROUTE
import com.jhreyess.reservoir.Destinations.INFO_ROUTE
import com.jhreyess.reservoir.presentation.HomeViewModel
import com.jhreyess.reservoir.presentation.InformationScreen
import com.jhreyess.reservoir.presentation.MainScreen

object Destinations {
    const val HOME_ROUTE = "home"
    const val INFO_ROUTE = "info"
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = HOME_ROUTE,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        composable(HOME_ROUTE) {
            val appContainer = (LocalContext.current.applicationContext as App).container
            val viewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.provideFactory(
                    appContainer.recordsRepository,
                    appContainer.damRepository,
                    appContainer.dataStore
                )
            )
            val state by viewModel.state.collectAsState()
            MainScreen(
                state = state,
                onEvent = viewModel::onEvent,
                onInfoClick = { navController.navigate(INFO_ROUTE) }
            )
        }

        composable(INFO_ROUTE) {
            InformationScreen(
                onBackPressed = { navController.navigateUp() }
            )
        }
    }
}