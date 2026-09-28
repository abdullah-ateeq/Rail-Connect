package com.example.railconnect

import com.example.railconnect.engine.NetworkEngine
import com.example.railconnect.model.Direction
import com.example.railconnect.model.GridPosition
import com.example.railconnect.model.RailCell
import com.example.railconnect.model.Station
import com.example.railconnect.model.TrackKind
import com.example.railconnect.model.TrackTile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PuzzleSolvabilityTest {

    @Test
    fun testHandcraftedPuzzleHasValidSolution() {
        val stations = mapOf(
            1 to Station(1, "North Terminal", "A", GridPosition(0, 1), setOf(Direction.RIGHT, Direction.DOWN)),
            2 to Station(2, "East Terminal", "B", GridPosition(0, 5), setOf(Direction.LEFT, Direction.DOWN)),
            3 to Station(3, "West Terminal", "C", GridPosition(4, 0), setOf(Direction.RIGHT, Direction.UP)),
            4 to Station(4, "Central Depot", "D", GridPosition(5, 4), setOf(Direction.LEFT, Direction.UP))
        )

        val blockedPositions = setOf(
            GridPosition(0, 3),
            GridPosition(2, 2),
            GridPosition(2, 5),
            GridPosition(4, 2),
            GridPosition(5, 1)
        )

        // Initialize board
        val board = MutableList(6) { r ->
            MutableList<RailCell>(6) { c ->
                val pos = GridPosition(r, c)
                when {
                    stations.values.any { it.position == pos } -> {
                        val st = stations.values.first { it.position == pos }
                        RailCell.StationCell(st.id)
                    }
                    pos in blockedPositions -> RailCell.Blocked
                    else -> RailCell.Ground
                }
            }
        }

        // Place the 11 solution tiles:
        // 1. (1, 1): T_JUNCTION (rotation 1: UP, RIGHT, DOWN)
        board[1][1] = RailCell.Track(TrackTile(1, TrackKind.T_JUNCTION, 1))
        // 2. (2, 1): STRAIGHT (rotation 1: UP, DOWN)
        board[2][1] = RailCell.Track(TrackTile(2, TrackKind.STRAIGHT, 1))
        // 3. (3, 1): CURVE (rotation 3: LEFT, UP)
        board[3][1] = RailCell.Track(TrackTile(3, TrackKind.CURVE, 3))
        // 4. (3, 0): CURVE (rotation 1: RIGHT, DOWN)
        board[3][0] = RailCell.Track(TrackTile(4, TrackKind.CURVE, 1))
        // 5. (1, 2): STRAIGHT (rotation 0: LEFT, RIGHT)
        board[1][2] = RailCell.Track(TrackTile(5, TrackKind.STRAIGHT, 0))
        // 6. (1, 3): STRAIGHT (rotation 0: LEFT, RIGHT)
        board[1][3] = RailCell.Track(TrackTile(6, TrackKind.STRAIGHT, 0))
        // 7. (1, 4): T_JUNCTION (rotation 2: LEFT, DOWN, RIGHT)
        // Wait, T_JUNCTION base: UP, LEFT, RIGHT
        // rot 1: UP, RIGHT, DOWN
        // rot 2: RIGHT, DOWN, LEFT
        board[1][4] = RailCell.Track(TrackTile(7, TrackKind.T_JUNCTION, 2))
        // 8. (1, 5): CURVE (rotation 3: LEFT, UP)
        board[1][5] = RailCell.Track(TrackTile(8, TrackKind.CURVE, 3))
        // 9. (2, 4): STRAIGHT (rotation 1: UP, DOWN)
        board[2][4] = RailCell.Track(TrackTile(9, TrackKind.STRAIGHT, 1))
        // 10. (3, 4): STRAIGHT (rotation 1: UP, DOWN)
        board[3][4] = RailCell.Track(TrackTile(10, TrackKind.STRAIGHT, 1))
        // 11. (4, 4): STRAIGHT (rotation 1: UP, DOWN)
        board[4][4] = RailCell.Track(TrackTile(11, TrackKind.STRAIGHT, 1))

        // Check BFS traversal starting from Station 1
        val result = NetworkEngine.findConnectedNetwork(board, stations, startStationId = 1)

        assertEquals("All 4 stations should be connected", setOf(1, 2, 3, 4), result.connectedStationIds)
        assertTrue(NetworkEngine.isNetworkComplete(board, stations, 4))

    }
}
