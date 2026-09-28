package com.example.railconnect.model

data class Station(
    val id: Int,
    val name: String,
    val code: String,
    val position: GridPosition,
    val connections: Set<Direction>
)
