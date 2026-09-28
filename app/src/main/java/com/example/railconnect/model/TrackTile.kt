package com.example.railconnect.model

enum class TrackKind {
    STRAIGHT,
    CURVE,
    T_JUNCTION,
    CROSS;

    val baseConnections: Set<Direction>
        get() = when (this) {
            STRAIGHT -> setOf(Direction.LEFT, Direction.RIGHT)
            CURVE -> setOf(Direction.UP, Direction.RIGHT)
            T_JUNCTION -> setOf(Direction.UP, Direction.LEFT, Direction.RIGHT)
            CROSS -> setOf(Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT)
        }

    val displayName: String
        get() = when (this) {
            STRAIGHT -> "Straight Track"
            CURVE -> "Curved Track"
            T_JUNCTION -> "T-Junction"
            CROSS -> "Crossing"
        }
}

data class TrackTile(
    val id: Int,
    val kind: TrackKind,
    val rotation: Int = 0 // 0 = 0 deg, 1 = 90 deg, 2 = 180 deg, 3 = 270 deg
) {
    val connections: Set<Direction>
        get() = getConnections(this)

    fun rotateClockwise(): TrackTile = copy(rotation = (rotation + 1) % 4)

    val contentDescription: String
        get() = "${kind.displayName}, rotated ${rotation * 90} degrees"
}

fun getConnections(tile: TrackTile): Set<Direction> {
    val effectiveRotation = ((tile.rotation % 4) + 4) % 4
    return tile.kind.baseConnections.map { dir ->
        var current = dir
        repeat(effectiveRotation) {
            current = current.rotateClockwise()
        }
        current
    }.toSet()
}
