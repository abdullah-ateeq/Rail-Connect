package com.example.railconnect.engine

import com.example.railconnect.model.Direction
import com.example.railconnect.model.GridPosition
import com.example.railconnect.model.RailCell
import com.example.railconnect.model.Station
import java.util.ArrayDeque

data class NetworkSearchResult(
    val connectedStationIds: Set<Int>,
    val connectedPositions: Set<GridPosition>
)

object NetworkEngine {

    const val GRID_SIZE = 6

    fun neighbor(position: GridPosition, direction: Direction): GridPosition? {
        return position.neighbor(direction, GRID_SIZE)
    }

    fun getCellConnections(
        board: List<List<RailCell>>,
        stations: Map<Int, Station>,
        position: GridPosition
    ): Set<Direction> {
        if (position.row !in 0 until GRID_SIZE || position.col !in 0 until GRID_SIZE) {
            return emptySet()
        }
        return when (val cell = board[position.row][position.col]) {
            is RailCell.StationCell -> {
                stations[cell.stationId]?.connections ?: emptySet()
            }
            is RailCell.Track -> {
                cell.tile.connections
            }
            RailCell.Ground, RailCell.Blocked -> {
                emptySet()
            }
        }
    }

    /**
     * Traverses the railway network starting from the first station (or any designated start station).
     * Two adjacent cells connect if and only if the current cell has an exit towards the neighbor
     * and the neighbor has the opposite matching rail exit.
     */
    fun findConnectedNetwork(
        board: List<List<RailCell>>,
        stations: Map<Int, Station>,
        startStationId: Int = 1
    ): NetworkSearchResult {
        val startStation = stations[startStationId] ?: return NetworkSearchResult(emptySet(), emptySet())

        val visitedPositions = mutableSetOf<GridPosition>()
        val reachedStationIds = mutableSetOf<Int>()
        val queue = ArrayDeque<GridPosition>()

        // Initialize BFS from start station
        queue.add(startStation.position)
        visitedPositions.add(startStation.position)
        reachedStationIds.add(startStation.id)

        while (queue.isNotEmpty()) {
            val currentPos = queue.poll() ?: break
            val currentExits = getCellConnections(board, stations, currentPos)

            for (direction in currentExits) {
                val nextPos = neighbor(currentPos, direction) ?: continue
                if (nextPos in visitedPositions) continue

                val neighborExits = getCellConnections(board, stations, nextPos)
                val requiredOpposite = direction.opposite()

                // Rule: Two adjacent cells connect only if current cell has rail exit toward neighbor
                // and neighbor has the opposite matching rail exit.
                if (requiredOpposite in neighborExits) {
                    visitedPositions.add(nextPos)
                    queue.add(nextPos)

                    val cell = board[nextPos.row][nextPos.col]
                    if (cell is RailCell.StationCell) {
                        reachedStationIds.add(cell.stationId)
                    }
                }
            }
        }

        return NetworkSearchResult(
            connectedStationIds = reachedStationIds,
            connectedPositions = visitedPositions
        )
    }

    fun isNetworkComplete(
        board: List<List<RailCell>>,
        stations: Map<Int, Station>,
        totalStationsCount: Int = 4
    ): Boolean {
        val result = findConnectedNetwork(board, stations)
        return result.connectedStationIds.size >= totalStationsCount
    }
}
