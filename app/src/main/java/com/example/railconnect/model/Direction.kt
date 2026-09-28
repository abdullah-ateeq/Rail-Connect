package com.example.railconnect.model

enum class Direction {
    UP,
    RIGHT,
    DOWN,
    LEFT;

    fun opposite(): Direction = when (this) {
        UP -> DOWN
        RIGHT -> LEFT
        DOWN -> UP
        LEFT -> RIGHT
    }

    fun rotateClockwise(): Direction = when (this) {
        UP -> RIGHT
        RIGHT -> DOWN
        DOWN -> LEFT
        LEFT -> UP
    }

    fun rotateCounterClockwise(): Direction = when (this) {
        UP -> LEFT
        RIGHT -> UP
        DOWN -> RIGHT
        LEFT -> DOWN
    }
}

fun opposite(direction: Direction): Direction = direction.opposite()
