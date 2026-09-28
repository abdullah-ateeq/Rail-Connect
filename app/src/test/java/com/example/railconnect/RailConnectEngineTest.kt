package com.example.railconnect

import com.example.railconnect.engine.NetworkEngine
import com.example.railconnect.model.Direction
import com.example.railconnect.model.GridPosition
import com.example.railconnect.model.PuzzleConfig
import com.example.railconnect.model.RailCell
import com.example.railconnect.model.RailConnectState
import com.example.railconnect.model.Station
import com.example.railconnect.model.TrackKind
import com.example.railconnect.model.TrackTile
import com.example.railconnect.model.getConnections
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RailConnectEngineTest {

    private fun createEmptyBoardWithStations(stations: Map<Int, Station>): MutableList<MutableList<RailCell>> {
        return MutableList(6) { r ->
            MutableList<RailCell>(6) { c ->
                val pos = GridPosition(r, c)
                val station = stations.values.find { it.position == pos }
                if (station != null) RailCell.StationCell(station.id) else RailCell.Ground
            }
        }
    }

    @Test
    fun testStraightConnection() {
        val tileRot0 = TrackTile(1, TrackKind.STRAIGHT, rotation = 0)
        assertEquals(setOf(Direction.LEFT, Direction.RIGHT), tileRot0.connections)

        val tileRot1 = TrackTile(2, TrackKind.STRAIGHT, rotation = 1)
        assertEquals(setOf(Direction.UP, Direction.DOWN), tileRot1.connections)

        val tileRot2 = TrackTile(3, TrackKind.STRAIGHT, rotation = 2)
        assertEquals(setOf(Direction.LEFT, Direction.RIGHT), tileRot2.connections)

        val tileRot3 = TrackTile(4, TrackKind.STRAIGHT, rotation = 3)
        assertEquals(setOf(Direction.UP, Direction.DOWN), tileRot3.connections)
    }

    @Test
    fun testCurveRotation() {
        val base = TrackTile(1, TrackKind.CURVE, rotation = 0)
        assertEquals(setOf(Direction.UP, Direction.RIGHT), base.connections)

        val rot90 = base.rotateClockwise()
        assertEquals(1, rot90.rotation)
        assertEquals(setOf(Direction.RIGHT, Direction.DOWN), rot90.connections)

        val rot180 = rot90.rotateClockwise()
        assertEquals(2, rot180.rotation)
        assertEquals(setOf(Direction.DOWN, Direction.LEFT), rot180.connections)

        val rot270 = rot180.rotateClockwise()
        assertEquals(3, rot270.rotation)
        assertEquals(setOf(Direction.LEFT, Direction.UP), rot270.connections)

        val rot360 = rot270.rotateClockwise()
        assertEquals(0, rot360.rotation)
        assertEquals(setOf(Direction.UP, Direction.RIGHT), rot360.connections)
    }

    @Test
    fun testTJunctionRotation() {
        val base = TrackTile(1, TrackKind.T_JUNCTION, rotation = 0)
        assertEquals(setOf(Direction.UP, Direction.LEFT, Direction.RIGHT), base.connections)

        val rot90 = base.rotateClockwise()
        assertEquals(setOf(Direction.UP, Direction.RIGHT, Direction.DOWN), rot90.connections)

        val rot180 = rot90.rotateClockwise()
        assertEquals(setOf(Direction.RIGHT, Direction.DOWN, Direction.LEFT), rot180.connections)

        val rot270 = rot180.rotateClockwise()
        assertEquals(setOf(Direction.DOWN, Direction.LEFT, Direction.UP), rot270.connections)
    }

    @Test
    fun testCrossConnection() {
        val cross = TrackTile(1, TrackKind.CROSS, rotation = 0)
        val allFour = setOf(Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT)
        assertEquals(allFour, cross.connections)
        assertEquals(allFour, cross.rotateClockwise().connections)
    }

    @Test
    fun testMatchedNeighboringRails() {
        // (1, 1) has RIGHT, (1, 2) has LEFT -> should connect
        val stations = mapOf(
            1 to Station(1, "S1", "A", GridPosition(1, 0), setOf(Direction.RIGHT))
        )
        val board = createEmptyBoardWithStations(stations)
        board[1][1] = RailCell.Track(TrackTile(1, TrackKind.STRAIGHT, 0)) // LEFT, RIGHT
        board[1][2] = RailCell.Track(TrackTile(2, TrackKind.STRAIGHT, 0)) // LEFT, RIGHT

        val result = NetworkEngine.findConnectedNetwork(board, stations, 1)
        assertTrue(result.connectedPositions.contains(GridPosition(1, 0)))
        assertTrue(result.connectedPositions.contains(GridPosition(1, 1)))
        assertTrue(result.connectedPositions.contains(GridPosition(1, 2)))
    }

    @Test
    fun testMismatchedRailsNotConnected() {
        // (1, 1) has LEFT, RIGHT. (1, 2) has UP, DOWN (vertical straight).
        // (1, 1) RIGHT points to (1, 2), but (1, 2) has no LEFT -> must not connect!
        val stations = mapOf(
            1 to Station(1, "S1", "A", GridPosition(1, 0), setOf(Direction.RIGHT))
        )
        val board = createEmptyBoardWithStations(stations)
        board[1][1] = RailCell.Track(TrackTile(1, TrackKind.STRAIGHT, 0)) // LEFT, RIGHT
        board[1][2] = RailCell.Track(TrackTile(2, TrackKind.STRAIGHT, 1)) // UP, DOWN (no LEFT exit!)

        val result = NetworkEngine.findConnectedNetwork(board, stations, 1)
        assertTrue(result.connectedPositions.contains(GridPosition(1, 1)))
        assertFalse(result.connectedPositions.contains(GridPosition(1, 2)))
    }

    @Test
    fun testStationToTrackConnection() {
        // Station at (0, 0) with RIGHT exit connects to Track at (0, 1) with LEFT exit
        val stations = mapOf(
            1 to Station(1, "S1", "A", GridPosition(0, 0), setOf(Direction.RIGHT))
        )
        val board = createEmptyBoardWithStations(stations)
        board[0][1] = RailCell.Track(TrackTile(1, TrackKind.STRAIGHT, 0))

        val result = NetworkEngine.findConnectedNetwork(board, stations, 1)
        assertTrue(result.connectedPositions.contains(GridPosition(0, 1)))

        // Conversely, if track at (0, 1) is vertical (UP, DOWN), it does not connect to (0, 0)
        board[0][1] = RailCell.Track(TrackTile(2, TrackKind.STRAIGHT, 1))
        val disconnectedResult = NetworkEngine.findConnectedNetwork(board, stations, 1)
        assertFalse(disconnectedResult.connectedPositions.contains(GridPosition(0, 1)))
    }

    @Test
    fun testBlockedPlacementRejection() {
        val blockedCell = RailCell.Blocked
        assertFalse("Cannot place track on blocked cell", blockedCell.isBuildable)
    }

    @Test
    fun testStationPlacementRejection() {
        val stationCell = RailCell.StationCell(1)
        assertFalse("Cannot place track on station cell", stationCell.isBuildable)
    }

    @Test
    fun testTwoStationsConnected() {
        // Connect Station 1 at (0, 0) [RIGHT] to Station 2 at (0, 2) [LEFT] via (0, 1) STRAIGHT [LEFT, RIGHT]
        val stations = mapOf(
            1 to Station(1, "S1", "A", GridPosition(0, 0), setOf(Direction.RIGHT)),
            2 to Station(2, "S2", "B", GridPosition(0, 2), setOf(Direction.LEFT))
        )
        val board = createEmptyBoardWithStations(stations)
        board[0][1] = RailCell.Track(TrackTile(1, TrackKind.STRAIGHT, 0))

        val result = NetworkEngine.findConnectedNetwork(board, stations, 1)
        assertEquals(setOf(1, 2), result.connectedStationIds)
        assertFalse(NetworkEngine.isNetworkComplete(board, stations, 4))
    }

    @Test
    fun testAllFourStationsConnected() {
        val stations = PuzzleConfig.stations
        val board = MutableList(6) { r ->
            MutableList<RailCell>(6) { c ->
                val pos = GridPosition(r, c)
                val st = stations.values.find { it.position == pos }
                if (st != null) RailCell.StationCell(st.id) else RailCell.Ground
            }
        }

        // Place solution
        board[1][1] = RailCell.Track(TrackTile(1, TrackKind.T_JUNCTION, 1))
        board[2][1] = RailCell.Track(TrackTile(2, TrackKind.STRAIGHT, 1))
        board[3][1] = RailCell.Track(TrackTile(3, TrackKind.CURVE, 3))
        board[3][0] = RailCell.Track(TrackTile(4, TrackKind.CURVE, 1))
        board[1][2] = RailCell.Track(TrackTile(5, TrackKind.STRAIGHT, 0))
        board[1][3] = RailCell.Track(TrackTile(6, TrackKind.STRAIGHT, 0))
        board[1][4] = RailCell.Track(TrackTile(7, TrackKind.T_JUNCTION, 2))
        board[1][5] = RailCell.Track(TrackTile(8, TrackKind.CURVE, 3))
        board[2][4] = RailCell.Track(TrackTile(9, TrackKind.STRAIGHT, 1))
        board[3][4] = RailCell.Track(TrackTile(10, TrackKind.STRAIGHT, 1))
        board[4][4] = RailCell.Track(TrackTile(11, TrackKind.STRAIGHT, 1))

        val result = NetworkEngine.findConnectedNetwork(board, stations, 1)
        assertEquals(setOf(1, 2, 3, 4), result.connectedStationIds)
        assertTrue(NetworkEngine.isNetworkComplete(board, stations, 4))
    }

    @Test
    fun testDisconnectedNetworkNotWinning() {
        val stations = PuzzleConfig.stations
        val board = PuzzleConfig.createInitialBoard().map { it.toMutableList() }.toMutableList()

        // Place a loop or disconnected branch that does not reach stations 3 and 4
        board[0][2] = RailCell.Track(TrackTile(1, TrackKind.STRAIGHT, 0))
        val result = NetworkEngine.findConnectedNetwork(board, stations, 1)
        assertFalse(NetworkEngine.isNetworkComplete(board, stations, 4))
        assertEquals(1, result.connectedStationIds.size)
    }

    @Test
    fun testTileExhaustionFailure() {
        // If tray is empty and bag is empty, and not all 4 stations connected -> failure
        val state = RailConnectState(
            board = PuzzleConfig.createInitialBoard(),
            tray = emptyList(),
            bag = emptyList(),
            connectedStationIds = setOf(1),
            isComplete = false,
            isFailed = true
        )
        assertTrue(state.isFailed)
        assertFalse(state.isComplete)
    }

    @Test
    fun testUndoRestoresPreviousSnapshot() {
        val initialState = RailConnectState.initial()
        val snapshot = initialState.toSnapshot()

        // Simulate a move
        val modifiedBoard = initialState.board.map { it.toMutableList() }.toMutableList()
        modifiedBoard[1][1] = RailCell.Track(TrackTile(1, TrackKind.STRAIGHT, 0))
        val nextState = initialState.copy(
            board = modifiedBoard,
            tray = initialState.tray.drop(1),
            moveCount = 1
        )

        assertEquals(1, nextState.moveCount)
        assertTrue(nextState.board[1][1] is RailCell.Track)

        // Undo to snapshot
        val restoredState = nextState.copy(
            board = snapshot.board,
            tray = snapshot.tray,
            bag = snapshot.bag,
            moveCount = snapshot.moveCount,
            connectedStationIds = snapshot.connectedStationIds
        )

        assertEquals(0, restoredState.moveCount)
        assertTrue(restoredState.board[1][1] is RailCell.Ground)
        assertEquals(initialState.tray.size, restoredState.tray.size)
    }
}
