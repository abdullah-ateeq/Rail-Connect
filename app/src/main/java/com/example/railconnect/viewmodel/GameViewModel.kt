package com.example.railconnect.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.railconnect.audio.SoundManager
import com.example.railconnect.data.GamePreferences
import com.example.railconnect.data.GameSettings
import com.example.railconnect.data.GameStatistics
import com.example.railconnect.engine.NetworkEngine
import com.example.railconnect.model.GridPosition
import com.example.railconnect.model.PuzzleConfig
import com.example.railconnect.model.RailCell
import com.example.railconnect.model.RailConnectSnapshot
import com.example.railconnect.model.RailConnectState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class GameUiEvent {
    data object HapticSelection : GameUiEvent()
    data object HapticRotation : GameUiEvent()
    data object HapticPlacement : GameUiEvent()
    data class HapticStationConnected(val count: Int) : GameUiEvent()
    data object HapticComplete : GameUiEvent()
    data object NavigateToResult : GameUiEvent()
}

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val preferences = GamePreferences(application.applicationContext)
    val soundManager = SoundManager()

    val settings: StateFlow<GameSettings> = preferences.settingsFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, GameSettings())

    val statistics: StateFlow<GameStatistics> = preferences.statisticsFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, GameStatistics())

    private val _uiState = MutableStateFlow(RailConnectState.initial())
    val uiState: StateFlow<RailConnectState> = _uiState.asStateFlow()

    private val undoStack = mutableListOf<RailConnectSnapshot>()

    private val _canUndo = MutableStateFlow(false)
    val canUndo: StateFlow<Boolean> = _canUndo.asStateFlow()

    private val _events = MutableSharedFlow<GameUiEvent>()
    val events: SharedFlow<GameUiEvent> = _events.asSharedFlow()

    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            settings.collect { s ->
                soundManager.isEnabled = s.soundEffects
            }
        }
        startTimer()
        recordGameStart()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                val current = _uiState.value
                if (!current.isComplete && !current.isFailed) {
                    _uiState.update { it.copy(elapsedMillis = it.elapsedMillis + 1000L) }
                }
            }
        }
    }

    private fun recordGameStart() {
        viewModelScope.launch {
            preferences.recordGameStarted()
        }
    }

    fun selectTile(tileId: Int) {
        val current = _uiState.value
        if (current.isComplete || current.isFailed || current.isAnimating) return
        if (current.tray.any { it.id == tileId }) {
            _uiState.update { it.copy(selectedTileId = tileId) }
            soundManager.playTileSelect()
            viewModelScope.launch { _events.emit(GameUiEvent.HapticSelection) }
        }
    }

    fun rotateSelectedTile() {
        val current = _uiState.value
        if (current.isComplete || current.isFailed || current.isAnimating) return
        val selectedId = current.selectedTileId ?: return

        val newTray = current.tray.map { tile ->
            if (tile.id == selectedId) tile.rotateClockwise() else tile
        }
        _uiState.update { it.copy(tray = newTray) }
        soundManager.playRotate()
        viewModelScope.launch { _events.emit(GameUiEvent.HapticRotation) }
    }

    fun placeTrack(position: GridPosition) {
        val current = _uiState.value
        // 1. Verify game active and not animating
        if (current.isComplete || current.isFailed || current.isAnimating) return

        // 2. Verify selected tile exists
        val selectedTile = current.selectedTile ?: return

        // 3. Verify destination is buildable GROUND
        if (position.row !in 0 until PuzzleConfig.GRID_SIZE || position.col !in 0 until PuzzleConfig.GRID_SIZE) return
        val targetCell = current.board[position.row][position.col]
        if (!targetCell.isBuildable) return

        // 4. Save Undo snapshot
        undoStack.add(current.toSnapshot())
        _canUndo.value = true

        // 5. Lock input with animation flag
        _uiState.update { it.copy(isAnimating = true) }

        // 6. Place selected tile on board
        val newBoard = current.board.mapIndexed { r, rowList ->
            if (r == position.row) {
                rowList.mapIndexed { c, cell ->
                    if (c == position.col) RailCell.Track(selectedTile) else cell
                }
            } else {
                rowList
            }
        }

        // 7. Remove tile from tray
        val remainingTray = current.tray.filterNot { it.id == selectedTile.id }.toMutableList()

        // 8. Increment move count
        val newMoveCount = current.moveCount + 1

        // 9. Draw replacement from bag if available
        var remainingBag = current.bag
        if (remainingBag.isNotEmpty()) {
            val drawnTile = remainingBag.first()
            remainingTray.add(drawnTile)
            remainingBag = remainingBag.drop(1)
        }

        // Auto-select first available tile in tray
        val nextSelectedId = remainingTray.firstOrNull()?.id

        // 10. Recalculate connected network
        val networkResult = NetworkEngine.findConnectedNetwork(newBoard, PuzzleConfig.stations)
        val newConnectedStations = networkResult.connectedStationIds
        val stationsIncreased = newConnectedStations.size > current.connectedStationIds.size

        // 11. Check win or fail
        val isWin = newConnectedStations.size >= PuzzleConfig.TOTAL_STATIONS
        val isFail = !isWin && remainingTray.isEmpty() && remainingBag.isEmpty()

        val banner = when {
            isWin -> "NETWORK COMPLETE"
            isFail -> "NETWORK INCOMPLETE"
            stationsIncreased -> "STATION CONNECTED (${newConnectedStations.size} / ${PuzzleConfig.TOTAL_STATIONS})"
            else -> null
        }

        _uiState.update {
            it.copy(
                board = newBoard,
                tray = remainingTray,
                bag = remainingBag,
                selectedTileId = nextSelectedId,
                connectedStationIds = newConnectedStations,
                connectedPositions = networkResult.connectedPositions,
                moveCount = newMoveCount,
                isComplete = isWin,
                isFailed = isFail,
                bannerMessage = banner,
                lastPlacedPosition = position
            )
        }

        // Audio & Haptic feedback
        if (isWin) {
            soundManager.playNetworkComplete()
            viewModelScope.launch {
                _events.emit(GameUiEvent.HapticComplete)
                preferences.recordGameWon(
                    moves = newMoveCount,
                    timeSeconds = current.elapsedMillis / 1000L,
                    tracksRemaining = remainingTray.size + remainingBag.size
                )
                delay(1200)
                _events.emit(GameUiEvent.NavigateToResult)
            }
        } else if (isFail) {
            soundManager.playFailure()
            viewModelScope.launch {
                delay(1000)
                _events.emit(GameUiEvent.NavigateToResult)
            }
        } else if (stationsIncreased) {
            soundManager.playStationConnected()
            viewModelScope.launch {
                _events.emit(GameUiEvent.HapticStationConnected(newConnectedStations.size))
            }
        } else {
            soundManager.playPlacement()
            viewModelScope.launch {
                _events.emit(GameUiEvent.HapticPlacement)
            }
        }

        // 14. Unlock input after short placement animation duration (150ms)
        viewModelScope.launch {
            delay(150)
            _uiState.update { it.copy(isAnimating = false) }
        }
    }

    fun undo() {
        val current = _uiState.value
        if (current.isAnimating || undoStack.isEmpty()) return

        val snapshot = undoStack.removeAt(undoStack.lastIndex)
        _canUndo.value = undoStack.isNotEmpty()

        _uiState.update {
            it.copy(
                board = snapshot.board,
                tray = snapshot.tray,
                bag = snapshot.bag,
                selectedTileId = snapshot.selectedTileId,
                connectedStationIds = snapshot.connectedStationIds,
                connectedPositions = snapshot.connectedPositions,
                moveCount = snapshot.moveCount,
                isComplete = snapshot.isComplete,
                isFailed = snapshot.isFailed,
                bannerMessage = null,
                lastPlacedPosition = null
            )
        }
        soundManager.playRotate()
    }

    fun restartPuzzle() {
        undoStack.clear()
        _canUndo.value = false
        _uiState.value = RailConnectState.initial()
        recordGameStart()
        startTimer()
    }
}
