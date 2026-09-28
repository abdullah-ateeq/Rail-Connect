package com.example.railconnect.model

data class GridPosition(
    val row: Int,
    val col: Int
) {
    fun neighbor(direction: Direction, gridSize: Int = 6): GridPosition? {
        val newRow = when (direction) {
            Direction.UP -> row - 1
            Direction.DOWN -> row + 1
            Direction.LEFT, Direction.RIGHT -> row
        }
        val newCol = when (direction) {
            Direction.LEFT -> col - 1
            Direction.RIGHT -> col + 1
            Direction.UP, Direction.DOWN -> col
        }
        return if (newRow in 0 until gridSize && newCol in 0 until gridSize) {
            GridPosition(newRow, newCol)
        } else {
            null
        }
    }
}
