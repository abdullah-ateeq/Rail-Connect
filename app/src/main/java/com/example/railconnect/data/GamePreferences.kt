package com.example.railconnect.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "rail_connect_prefs")

enum class AppThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

enum class BoardThemeMode {
    CLASSIC,
    MIDNIGHT
}

data class GameSettings(
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val boardTheme: BoardThemeMode = BoardThemeMode.CLASSIC,
    val soundEffects: Boolean = true,
    val hapticFeedback: Boolean = true,
    val showConnectedNetwork: Boolean = true,
    val showTimer: Boolean = true
)

data class GameStatistics(
    val gamesPlayed: Int = 0,
    val networksCompleted: Int = 0,
    val bestMoves: Int? = null,
    val bestTimeSeconds: Long? = null,
    val mostTracksRemaining: Int = 0
)

class GamePreferences(private val context: Context) {

    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val BOARD_THEME = stringPreferencesKey("board_theme")
        val SOUND_EFFECTS = booleanPreferencesKey("sound_effects")
        val HAPTIC_FEEDBACK = booleanPreferencesKey("haptic_feedback")
        val SHOW_CONNECTED_NETWORK = booleanPreferencesKey("show_connected_network")
        val SHOW_TIMER = booleanPreferencesKey("show_timer")

        val GAMES_PLAYED = intPreferencesKey("games_played")
        val NETWORKS_COMPLETED = intPreferencesKey("networks_completed")
        val BEST_MOVES = intPreferencesKey("best_moves")
        val BEST_TIME_SECONDS = longPreferencesKey("best_time_seconds")
        val MOST_TRACKS_REMAINING = intPreferencesKey("most_tracks_remaining")
    }

    val settingsFlow: Flow<GameSettings> = context.dataStore.data.map { preferences ->
        val themeModeStr = preferences[PreferencesKeys.THEME_MODE] ?: AppThemeMode.SYSTEM.name
        val boardThemeStr = preferences[PreferencesKeys.BOARD_THEME] ?: BoardThemeMode.CLASSIC.name
        GameSettings(
            themeMode = runCatching { AppThemeMode.valueOf(themeModeStr) }.getOrDefault(AppThemeMode.SYSTEM),
            boardTheme = runCatching { BoardThemeMode.valueOf(boardThemeStr) }.getOrDefault(BoardThemeMode.CLASSIC),
            soundEffects = preferences[PreferencesKeys.SOUND_EFFECTS] ?: true,
            hapticFeedback = preferences[PreferencesKeys.HAPTIC_FEEDBACK] ?: true,
            showConnectedNetwork = preferences[PreferencesKeys.SHOW_CONNECTED_NETWORK] ?: true,
            showTimer = preferences[PreferencesKeys.SHOW_TIMER] ?: true
        )
    }

    val statisticsFlow: Flow<GameStatistics> = context.dataStore.data.map { preferences ->
        val bestMovesRaw = preferences[PreferencesKeys.BEST_MOVES]
        val bestTimeRaw = preferences[PreferencesKeys.BEST_TIME_SECONDS]
        GameStatistics(
            gamesPlayed = preferences[PreferencesKeys.GAMES_PLAYED] ?: 0,
            networksCompleted = preferences[PreferencesKeys.NETWORKS_COMPLETED] ?: 0,
            bestMoves = if (bestMovesRaw != null && bestMovesRaw > 0) bestMovesRaw else null,
            bestTimeSeconds = if (bestTimeRaw != null && bestTimeRaw > 0) bestTimeRaw else null,
            mostTracksRemaining = preferences[PreferencesKeys.MOST_TRACKS_REMAINING] ?: 0
        )
    }

    suspend fun setThemeMode(themeMode: AppThemeMode) {
        context.dataStore.edit { it[PreferencesKeys.THEME_MODE] = themeMode.name }
    }

    suspend fun setBoardTheme(boardTheme: BoardThemeMode) {
        context.dataStore.edit { it[PreferencesKeys.BOARD_THEME] = boardTheme.name }
    }

    suspend fun setSoundEffects(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.SOUND_EFFECTS] = enabled }
    }

    suspend fun setHapticFeedback(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.HAPTIC_FEEDBACK] = enabled }
    }

    suspend fun setShowConnectedNetwork(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.SHOW_CONNECTED_NETWORK] = enabled }
    }

    suspend fun setShowTimer(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.SHOW_TIMER] = enabled }
    }

    suspend fun recordGameStarted() {
        context.dataStore.edit { prefs ->
            val current = prefs[PreferencesKeys.GAMES_PLAYED] ?: 0
            prefs[PreferencesKeys.GAMES_PLAYED] = current + 1
        }
    }

    suspend fun recordGameWon(moves: Int, timeSeconds: Long, tracksRemaining: Int) {
        context.dataStore.edit { prefs ->
            val completed = (prefs[PreferencesKeys.NETWORKS_COMPLETED] ?: 0) + 1
            prefs[PreferencesKeys.NETWORKS_COMPLETED] = completed

            val currentBestMoves = prefs[PreferencesKeys.BEST_MOVES]
            if (currentBestMoves == null || moves < currentBestMoves) {
                prefs[PreferencesKeys.BEST_MOVES] = moves
            }

            val currentBestTime = prefs[PreferencesKeys.BEST_TIME_SECONDS]
            if (currentBestTime == null || timeSeconds < currentBestTime) {
                prefs[PreferencesKeys.BEST_TIME_SECONDS] = timeSeconds
            }

            val currentMostRemaining = prefs[PreferencesKeys.MOST_TRACKS_REMAINING] ?: 0
            if (tracksRemaining > currentMostRemaining) {
                prefs[PreferencesKeys.MOST_TRACKS_REMAINING] = tracksRemaining
            }
        }
    }

    suspend fun resetStatistics() {
        context.dataStore.edit { prefs ->
            prefs.remove(PreferencesKeys.GAMES_PLAYED)
            prefs.remove(PreferencesKeys.NETWORKS_COMPLETED)
            prefs.remove(PreferencesKeys.BEST_MOVES)
            prefs.remove(PreferencesKeys.BEST_TIME_SECONDS)
            prefs.remove(PreferencesKeys.MOST_TRACKS_REMAINING)
        }
    }
}
