package com.example.railconnect.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.railconnect.data.AppThemeMode
import com.example.railconnect.data.BoardThemeMode
import com.example.railconnect.data.GamePreferences
import com.example.railconnect.data.GameSettings
import com.example.railconnect.data.GameStatistics
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val preferences = GamePreferences(application.applicationContext)

    val settings: StateFlow<GameSettings> = preferences.settingsFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, GameSettings())

    val statistics: StateFlow<GameStatistics> = preferences.statisticsFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, GameStatistics())

    fun setThemeMode(mode: AppThemeMode) {
        viewModelScope.launch { preferences.setThemeMode(mode) }
    }

    fun setBoardTheme(theme: BoardThemeMode) {
        viewModelScope.launch { preferences.setBoardTheme(theme) }
    }

    fun setSoundEffects(enabled: Boolean) {
        viewModelScope.launch { preferences.setSoundEffects(enabled) }
    }

    fun setHapticFeedback(enabled: Boolean) {
        viewModelScope.launch { preferences.setHapticFeedback(enabled) }
    }

    fun setShowConnectedNetwork(enabled: Boolean) {
        viewModelScope.launch { preferences.setShowConnectedNetwork(enabled) }
    }

    fun setShowTimer(enabled: Boolean) {
        viewModelScope.launch { preferences.setShowTimer(enabled) }
    }

    fun resetStatistics() {
        viewModelScope.launch { preferences.resetStatistics() }
    }
}
