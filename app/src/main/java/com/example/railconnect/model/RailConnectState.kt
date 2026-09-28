package com.example.railconnect.model

data class RailConnectSnapshot(
    val board: List<List<RailCell>>,
    val tray: List<TrackTile>,
    val bag: List<TrackTile>,
    val selectedTileId: Int?,
    val connectedStationIds: Set<Int>,
    val connectedPositions: Set<GridPosition>,
    val moveCount: Int,
    val isComplete: Boolean,
    val isFailed: Boolean
)

data class RailConnectState(
    val board: List<List<RailCell>> = PuzzleConfig.createInitialBoard(),
    val tray: List<TrackTile> = emptyList(),
    val bag: List<TrackTile> = emptyList(),
    val selectedTileId: Int? = null,
    val connectedStationIds: Set<Int> = setOf(1), // Starts connected to first station
    val connectedPositions: Set<GridPosition> = setOf(PuzzleConfig.stations[1]!!.position),
    val moveCount: Int = 0,
    val isComplete: Boolean = false,
    val isFailed: Boolean = false,
    val elapsedMillis: Long = 0L,
    val isAnimating: Boolean = false,
    val bannerMessage: String? = null,
    val lastPlacedPosition: GridPosition? = null
) {
    val selectedTile: TrackTile?
        get() = tray.find { it.id == selectedTileId }

    val tracksRemaining: Int
        get() = tray.size + bag.size

    val tracksPlaced: Int
        get() = 18 - tracksRemaining

    val connectedStationsCount: Int
        get() = connectedStationIds.size

    fun toSnapshot(): RailConnectSnapshot {
        return RailConnectSnapshot(
            board = board.map { it.toList() },
            tray = tray.toList(),
            bag = bag.toList(),
            selectedTileId = selectedTileId,
            connectedStationIds = connectedStationIds.toSet(),
            connectedPositions = connectedPositions.toSet(),
            moveCount = moveCount,
            isComplete = isComplete,
            isFailed = isFailed
        )
    }

    companion object {
        fun initial(): RailConnectState {
            val fullSupply = PuzzleConfig.createInitialTileSupply()
            val initialTray = fullSupply.take(PuzzleConfig.TRAY_SIZE)
            val initialBag = fullSupply.drop(PuzzleConfig.TRAY_SIZE)
            val initialBoard = PuzzleConfig.createInitialBoard()
            val startStationPos = PuzzleConfig.stations[1]!!.position

            return RailConnectState(
                board = initialBoard,
                tray = initialTray,
                bag = initialBag,
                selectedTileId = initialTray.firstOrNull()?.id,
                connectedStationIds = setOf(1),
                connectedPositions = setOf(startStationPos),
                moveCount = 0,
                isComplete = false,
                isFailed = false,
                elapsedMillis = 0L,
                isAnimating = false,
                bannerMessage = null
            )
        }
    }
}
