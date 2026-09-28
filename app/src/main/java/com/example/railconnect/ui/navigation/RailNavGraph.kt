package com.example.railconnect.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.railconnect.ui.screens.AboutScreen
import com.example.railconnect.ui.screens.GameScreen
import com.example.railconnect.ui.screens.HomeScreen
import com.example.railconnect.ui.screens.HowToPlayScreen
import com.example.railconnect.ui.screens.ResultScreen
import com.example.railconnect.ui.screens.SettingsScreen
import com.example.railconnect.ui.screens.SplashScreen
import com.example.railconnect.viewmodel.GameViewModel
import com.example.railconnect.viewmodel.SettingsViewModel

@Composable
fun RailNavGraph(
    navController: NavHostController,
    gameViewModel: GameViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // 1. Splash
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // 2. Home
        composable(Screen.Home.route) {
            HomeScreen(
                statisticsState = settingsViewModel.statistics,
                onPlayClick = {
                    gameViewModel.restartPuzzle()
                    navController.navigate(Screen.Game.route)
                },
                onHowToPlayClick = { navController.navigate(Screen.HowToPlay.route) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
                onAboutClick = { navController.navigate(Screen.About.route) }
            )
        }

        // 3. Game
        composable(Screen.Game.route) {
            GameScreen(
                viewModel = gameViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToResult = { isWin, moves, timeSec, tracksRem, tracksPlc, stnConn ->
                    val route = Screen.Result.createRoute(
                        isWin = isWin,
                        moves = moves,
                        timeSeconds = timeSec,
                        tracksRemaining = tracksRem,
                        tracksPlaced = tracksPlc,
                        stationsConnected = stnConn
                    )
                    navController.navigate(route) {
                        popUpTo(Screen.Game.route) { inclusive = true }
                    }
                }
            )
        }

        // 4. Result
        composable(
            route = Screen.Result.route,
            arguments = listOf(
                navArgument("isWin") { type = NavType.BoolType },
                navArgument("moves") { type = NavType.IntType },
                navArgument("timeSeconds") { type = NavType.LongType },
                navArgument("tracksRemaining") { type = NavType.IntType },
                navArgument("tracksPlaced") { type = NavType.IntType },
                navArgument("stationsConnected") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val isWin = backStackEntry.arguments?.getBoolean("isWin") ?: false
            val moves = backStackEntry.arguments?.getInt("moves") ?: 0
            val timeSeconds = backStackEntry.arguments?.getLong("timeSeconds") ?: 0L
            val tracksRemaining = backStackEntry.arguments?.getInt("tracksRemaining") ?: 0
            val tracksPlaced = backStackEntry.arguments?.getInt("tracksPlaced") ?: 0
            val stationsConnected = backStackEntry.arguments?.getInt("stationsConnected") ?: 0

            ResultScreen(
                isWin = isWin,
                moves = moves,
                timeSeconds = timeSeconds,
                tracksRemaining = tracksRemaining,
                tracksPlaced = tracksPlaced,
                stationsConnected = stationsConnected,
                onPlayAgainClick = {
                    gameViewModel.restartPuzzle()
                    navController.navigate(Screen.Game.route) {
                        popUpTo(Screen.Result.route) { inclusive = true }
                    }
                },
                onHomeClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                }
            )
        }

        // 5. How to Play
        composable(Screen.HowToPlay.route) {
            HowToPlayScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // 6. Settings
        composable(Screen.Settings.route) {
            SettingsScreen(
                viewModel = settingsViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        // 7. About
        composable(Screen.About.route) {
            AboutScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
