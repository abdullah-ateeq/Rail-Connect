package com.example.railconnect.ui.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Home : Screen("home")
    data object Game : Screen("game")
    data object HowToPlay : Screen("how_to_play")
    data object Settings : Screen("settings")
    data object About : Screen("about")
    data object Result : Screen("result/{isWin}/{moves}/{timeSeconds}/{tracksRemaining}/{tracksPlaced}/{stationsConnected}") {
        fun createRoute(
            isWin: Boolean,
            moves: Int,
            timeSeconds: Long,
            tracksRemaining: Int,
            tracksPlaced: Int,
            stationsConnected: Int
        ): String {
            return "result/$isWin/$moves/$timeSeconds/$tracksRemaining/$tracksPlaced/$stationsConnected"
        }
    }
}
