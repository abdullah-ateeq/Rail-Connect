package com.example.railconnect.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.railconnect.model.PuzzleConfig
import com.example.railconnect.ui.components.ActionControls
import com.example.railconnect.ui.components.BoardCanvas
import com.example.railconnect.ui.components.GameHud
import com.example.railconnect.ui.components.RestartConfirmDialog
import com.example.railconnect.ui.components.StatusBanner
import com.example.railconnect.ui.components.TrackTilePreview
import com.example.railconnect.ui.theme.BurnishedBronze
import com.example.railconnect.ui.theme.LocalBoardPalette
import com.example.railconnect.viewmodel.GameUiEvent
import com.example.railconnect.viewmodel.GameViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToResult: (Boolean, Int, Long, Int, Int, Int) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val canUndo by viewModel.canUndo.collectAsState()
    val palette = LocalBoardPalette.current
    val haptic = LocalHapticFeedback.current

    var showRestartDialog by remember { mutableStateOf(false) }

    // Haptic feedback listener
    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            if (settings.hapticFeedback) {
                when (event) {
                    is GameUiEvent.HapticSelection -> {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    }
                    is GameUiEvent.HapticRotation -> {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    }
                    is GameUiEvent.HapticPlacement -> {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                    is GameUiEvent.HapticStationConnected -> {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                    is GameUiEvent.HapticComplete -> {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                    is GameUiEvent.NavigateToResult -> {
                        onNavigateToResult(
                            state.isComplete,
                            state.moveCount,
                            state.elapsedMillis / 1000L,
                            state.tracksRemaining,
                            state.tracksPlaced,
                            state.connectedStationsCount
                        )
                    }
                }
            } else {
                if (event is GameUiEvent.NavigateToResult) {
                    onNavigateToResult(
                        state.isComplete,
                        state.moveCount,
                        state.elapsedMillis / 1000L,
                        state.tracksRemaining,
                        state.tracksPlaced,
                        state.connectedStationsCount
                    )
                }
            }
        }
    }

    if (showRestartDialog) {
        RestartConfirmDialog(
            onConfirm = { viewModel.restartPuzzle() },
            onDismiss = { showRestartDialog = false }
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isLandscape = maxWidth > maxHeight

            if (!isLandscape) {
                // Portrait Layout
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // 1. Top HUD
                    GameHud(
                        connectedStations = state.connectedStationsCount,
                        totalStations = PuzzleConfig.TOTAL_STATIONS,
                        tracksLeft = state.tracksRemaining,
                        moves = state.moveCount,
                        elapsedMillis = state.elapsedMillis,
                        showTimer = settings.showTimer,
                        onBackClick = onNavigateBack,
                        onRestartClick = { showRestartDialog = true }
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Status Banner (Station Connected, Network Complete, etc.)
                    AnimatedVisibility(
                        visible = state.bannerMessage != null,
                        enter = fadeIn() + slideInVertically(),
                        exit = fadeOut() + slideOutVertically()
                    ) {
                        state.bannerMessage?.let {
                            StatusBanner(message = it)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // 2. 6x6 Board Canvas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        BoardCanvas(
                            board = state.board,
                            stations = PuzzleConfig.stations,
                            connectedPositions = state.connectedPositions,
                            showNetworkHighlight = settings.showConnectedNetwork,
                            lastPlacedPosition = state.lastPlacedPosition,
                            onCellClick = { pos -> viewModel.placeTrack(pos) }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 3. Track Tile Tray
                    TileTraySection(
                        tray = state.tray,
                        selectedTileId = state.selectedTileId,
                        onTileSelect = { id -> viewModel.selectTile(id) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 4. Action Controls (Rotate & Undo)
                    ActionControls(
                        canRotate = state.selectedTileId != null && !state.isComplete && !state.isFailed && !state.isAnimating,
                        canUndo = canUndo && !state.isAnimating,
                        onRotateClick = { viewModel.rotateSelectedTile() },
                        onUndoClick = { viewModel.undo() }
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }
            } else {
                // Landscape / Tablet Layout
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: Board
                    Box(
                        modifier = Modifier
                            .weight(1.1f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        BoardCanvas(
                            board = state.board,
                            stations = PuzzleConfig.stations,
                            connectedPositions = state.connectedPositions,
                            showNetworkHighlight = settings.showConnectedNetwork,
                            lastPlacedPosition = state.lastPlacedPosition,
                            onCellClick = { pos -> viewModel.placeTrack(pos) }
                        )
                    }

                    // Right: Controls, HUD, Tray
                    Column(
                        modifier = Modifier
                            .weight(0.9f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        GameHud(
                            connectedStations = state.connectedStationsCount,
                            totalStations = PuzzleConfig.TOTAL_STATIONS,
                            tracksLeft = state.tracksRemaining,
                            moves = state.moveCount,
                            elapsedMillis = state.elapsedMillis,
                            showTimer = settings.showTimer,
                            onBackClick = onNavigateBack,
                            onRestartClick = { showRestartDialog = true }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        TileTraySection(
                            tray = state.tray,
                            selectedTileId = state.selectedTileId,
                            onTileSelect = { id -> viewModel.selectTile(id) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        ActionControls(
                            canRotate = state.selectedTileId != null && !state.isComplete && !state.isFailed && !state.isAnimating,
                            canUndo = canUndo && !state.isAnimating,
                            onRotateClick = { viewModel.rotateSelectedTile() },
                            onUndoClick = { viewModel.undo() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TileTraySection(
    tray: List<com.example.railconnect.model.TrackTile>,
    selectedTileId: Int?,
    onTileSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalBoardPalette.current

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, palette.groundBorder, RoundedCornerShape(14.dp)),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AVAILABLE TILES",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.4.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = BurnishedBronze
                )
                Text(
                    text = "${tray.size} IN TRAY",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 3 Tray Slots
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until PuzzleConfig.TRAY_SIZE) {
                    val tile = tray.getOrNull(i)
                    val isSelected = tile != null && tile.id == selectedTileId

                    TrackTilePreview(
                        tile = tile,
                        isSelected = isSelected,
                        onClick = if (tile != null) { { onTileSelect(tile.id) } } else null,
                        size = 72.dp
                    )
                }
            }
        }
    }
}
