package com.example.railconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.railconnect.ui.navigation.RailNavGraph
import com.example.railconnect.ui.theme.RailConnectTheme
import com.example.railconnect.viewmodel.GameViewModel
import com.example.railconnect.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {

    private val gameViewModel: GameViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settings by settingsViewModel.settings.collectAsState()

            RailConnectTheme(
                themeMode = settings.themeMode,
                boardTheme = settings.boardTheme
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding()
                ) {
                    val navController = rememberNavController()
                    RailNavGraph(
                        navController = navController,
                        gameViewModel = gameViewModel,
                        settingsViewModel = settingsViewModel
                    )
                }
            }
        }
    }
}