package com.example.railconnect.model

object PuzzleConfig {
    const val GRID_SIZE = 6
    const val TRAY_SIZE = 3
    const val TOTAL_STATIONS = 4

    val stations: Map<Int, Station> = mapOf(
        1 to Station(
            id = 1,
            name = "North Terminal",
            code = "A",
            position = GridPosition(0, 1),
            connections = setOf(Direction.RIGHT, Direction.DOWN)
        ),
        2 to Station(
            id = 2,
            name = "East Terminal",
            code = "B",
            position = GridPosition(0, 5),
            connections = setOf(Direction.LEFT, Direction.DOWN)
        ),
        3 to Station(
            id = 3,
            name = "West Terminal",
            code = "C",
            position = GridPosition(4, 0),
            connections = setOf(Direction.RIGHT, Direction.UP)
        ),
        4 to Station(
            id = 4,
            name = "Central Depot",
            code = "D",
            position = GridPosition(5, 4),
            connections = setOf(Direction.LEFT, Direction.UP)
        )
    )

    val blockedPositions: Set<GridPosition> = setOf(
        GridPosition(0, 3),
        GridPosition(2, 2),
        GridPosition(2, 5),
        GridPosition(4, 2),
        GridPosition(5, 1)
    )

    /**
     * Deterministic tile supply: 18 tiles total
     * (6 Straight, 6 Curve, 4 T-Junction, 2 Cross)
     * Arranged to give a versatile initial tray and rewarding progression.
     */
    fun createInitialTileSupply(): List<TrackTile> {
        val kinds = listOf(
            // Initial 3 for tray:
            TrackKind.T_JUNCTION, // ID 1
            TrackKind.STRAIGHT,   // ID 2
            TrackKind.CURVE,      // ID 3

            // Subsequent bag draw order:
            TrackKind.CURVE,      // ID 4
            TrackKind.STRAIGHT,   // ID 5
            TrackKind.CROSS,       // ID 6
            TrackKind.STRAIGHT,   // ID 7
            TrackKind.T_JUNCTION, // ID 8
            TrackKind.CURVE,      // ID 9
            TrackKind.STRAIGHT,   // ID 10
            TrackKind.CURVE,      // ID 11
            TrackKind.T_JUNCTION, // ID 12
            TrackKind.STRAIGHT,   // ID 13
            TrackKind.CURVE,      // ID 14
            TrackKind.CROSS,       // ID 15
            TrackKind.STRAIGHT,   // ID 16
            TrackKind.T_JUNCTION, // ID 17
            TrackKind.CURVE       // ID 18
        )

        return kinds.mapIndexed { index, kind ->
            TrackTile(id = index + 1, kind = kind, rotation = 0)
        }
    }

    fun createInitialBoard(): List<List<RailCell>> {
        return List(GRID_SIZE) { r ->
            List(GRID_SIZE) { c ->
                val pos = GridPosition(r, c)
                val station = stations.values.find { it.position == pos }
                when {
                    station != null -> RailCell.StationCell(station.id)
                    pos in blockedPositions -> RailCell.Blocked
                    else -> RailCell.Ground
                }
            }
        }
    }
}
