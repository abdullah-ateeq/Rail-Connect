package com.example.railconnect.model

sealed class RailCell {
    data object Ground : RailCell()
    data object Blocked : RailCell()
    data class StationCell(val stationId: Int) : RailCell()
    data class Track(val tile: TrackTile) : RailCell()

    val isBuildable: Boolean
        get() = this is Ground

    val isStation: Boolean
        get() = this is StationCell

    val isTrack: Boolean
        get() = this is Track

    val isBlocked: Boolean
        get() = this is Blocked
}
