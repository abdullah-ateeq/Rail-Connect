package com.example.railconnect.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.example.railconnect.model.Direction
import com.example.railconnect.model.GridPosition
import com.example.railconnect.model.PuzzleConfig
import com.example.railconnect.model.RailCell
import com.example.railconnect.model.Station
import com.example.railconnect.model.TrackKind
import com.example.railconnect.model.TrackTile
import com.example.railconnect.ui.theme.BoardPalette
import com.example.railconnect.ui.theme.LocalBoardPalette
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun BoardCanvas(
    board: List<List<RailCell>>,
    stations: Map<Int, Station>,
    connectedPositions: Set<GridPosition>,
    showNetworkHighlight: Boolean,
    lastPlacedPosition: GridPosition?,
    onCellClick: (GridPosition) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalBoardPalette.current

    // Placement pop animation for newly placed tile
    val placeAnim = remember(lastPlacedPosition) { Animatable(0.82f) }
    LaunchedEffect(lastPlacedPosition) {
        if (lastPlacedPosition != null) {
            placeAnim.snapTo(0.82f)
            placeAnim.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(150, easing = FastOutSlowInEasing)
            )
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .pointerInput(board) {
                detectTapGestures { offset ->
                    val cellSize = size.width / PuzzleConfig.GRID_SIZE
                    val col = (offset.x / cellSize).toInt().coerceIn(0, PuzzleConfig.GRID_SIZE - 1)
                    val row = (offset.y / cellSize).toInt().coerceIn(0, PuzzleConfig.GRID_SIZE - 1)
                    onCellClick(GridPosition(row, col))
                }
            }
            .semantics {
                contentDescription = "Railway Grid, 6 by 6"
            }
    ) {
        val gridSize = PuzzleConfig.GRID_SIZE
        val cellSize = size.width / gridSize
        val padding = cellSize * 0.04f

        // Draw outer board background
        drawRoundRect(
            color = palette.boardBackground,
            cornerRadius = CornerRadius(cellSize * 0.15f, cellSize * 0.15f),
            size = size
        )

        // Draw grid cells
        for (r in 0 until gridSize) {
            for (c in 0 until gridSize) {
                val cellPos = GridPosition(r, c)
                val cell = board[r][c]
                val cellLeft = c * cellSize
                val cellTop = r * cellSize
                val cellBounds = Size(cellSize, cellSize)
                val isConnected = showNetworkHighlight && cellPos in connectedPositions
                val isJustPlaced = cellPos == lastPlacedPosition
                val scale = if (isJustPlaced) placeAnim.value else 1f

                drawCell(
                    palette = palette,
                    cell = cell,
                    position = cellPos,
                    stations = stations,
                    left = cellLeft,
                    top = cellTop,
                    cellSize = cellSize,
                    padding = padding,
                    isConnected = isConnected,
                    scale = scale
                )
            }
        }

        // Draw board grid boundary lines
        drawRoundRect(
            color = palette.gridBorder,
            cornerRadius = CornerRadius(cellSize * 0.15f, cellSize * 0.15f),
            size = size,
            style = Stroke(width = 2.5f)
        )
    }
}

private fun DrawScope.drawCell(
    palette: BoardPalette,
    cell: RailCell,
    position: GridPosition,
    stations: Map<Int, Station>,
    left: Float,
    top: Float,
    cellSize: Float,
    padding: Float,
    isConnected: Boolean,
    scale: Float
) {
    val innerLeft = left + padding
    val innerTop = top + padding
    val innerSize = cellSize - (padding * 2)

    when (cell) {
        is RailCell.Ground -> {
            // Empty buildable cell
            drawRoundRect(
                color = palette.groundCell,
                topLeft = Offset(innerLeft, innerTop),
                size = Size(innerSize, innerSize),
                cornerRadius = CornerRadius(innerSize * 0.12f, innerSize * 0.12f)
            )
            drawRoundRect(
                color = palette.groundBorder,
                topLeft = Offset(innerLeft, innerTop),
                size = Size(innerSize, innerSize),
                cornerRadius = CornerRadius(innerSize * 0.12f, innerSize * 0.12f),
                style = Stroke(width = 1f)
            )
            // Tiny center subtle point to invite placement
            drawCircle(
                color = palette.groundBorder.copy(alpha = 0.5f),
                radius = innerSize * 0.035f,
                center = Offset(left + cellSize * 0.5f, top + cellSize * 0.5f)
            )
        }

        is RailCell.Blocked -> {
            // Blocked terrain cell with cross-hatching
            drawRoundRect(
                color = palette.blockedCell,
                topLeft = Offset(innerLeft, innerTop),
                size = Size(innerSize, innerSize),
                cornerRadius = CornerRadius(innerSize * 0.12f, innerSize * 0.12f)
            )
            // Diagonal hatch lines
            val step = innerSize / 4f
            for (i in -3..6) {
                val startX = (innerLeft + i * step).coerceIn(innerLeft, innerLeft + innerSize)
                val startY = innerTop
                val endX = innerLeft
                val endY = (innerTop + i * step).coerceIn(innerTop, innerTop + innerSize)
                if (startX >= innerLeft && endY >= innerTop) {
                    drawLine(
                        color = palette.blockedHatch,
                        start = Offset(innerLeft + i * step, innerTop),
                        end = Offset(innerLeft + (i - 2) * step, innerTop + innerSize),
                        strokeWidth = 1.5f,
                        cap = StrokeCap.Round
                    )
                }
            }
            drawRoundRect(
                color = palette.blockedHatch.copy(alpha = 0.7f),
                topLeft = Offset(innerLeft, innerTop),
                size = Size(innerSize, innerSize),
                cornerRadius = CornerRadius(innerSize * 0.12f, innerSize * 0.12f),
                style = Stroke(width = 1.2f)
            )
        }

        is RailCell.StationCell -> {
            val station = stations[cell.stationId]
            drawStationCell(
                palette = palette,
                station = station,
                left = left,
                top = top,
                cellSize = cellSize,
                padding = padding,
                isConnected = isConnected
            )
        }

        is RailCell.Track -> {
            drawTrackCell(
                palette = palette,
                tile = cell.tile,
                left = left,
                top = top,
                cellSize = cellSize,
                padding = padding,
                isConnected = isConnected,
                scale = scale
            )
        }
    }
}

private fun DrawScope.drawStationCell(
    palette: BoardPalette,
    station: Station?,
    left: Float,
    top: Float,
    cellSize: Float,
    padding: Float,
    isConnected: Boolean
) {
    val innerLeft = left + padding
    val innerTop = top + padding
    val innerSize = cellSize - (padding * 2)
    val cx = left + cellSize * 0.5f
    val cy = top + cellSize * 0.5f

    // Station ground backing
    drawRoundRect(
        color = palette.groundCell,
        topLeft = Offset(innerLeft, innerTop),
        size = Size(innerSize, innerSize),
        cornerRadius = CornerRadius(innerSize * 0.12f, innerSize * 0.12f)
    )

    // Draw track entry notches/lines into station from the borders
    val railGauge = cellSize * 0.14f
    val railWidth = cellSize * 0.05f

    station?.connections?.forEach { dir ->
        val railColor = if (isConnected) palette.railHighlightColor else palette.railColor
        when (dir) {
            Direction.UP -> {
                drawLine(railColor, Offset(cx - railGauge, top), Offset(cx - railGauge, cy - innerSize * 0.25f), railWidth, StrokeCap.Square)
                drawLine(railColor, Offset(cx + railGauge, top), Offset(cx + railGauge, cy - innerSize * 0.25f), railWidth, StrokeCap.Square)
            }
            Direction.DOWN -> {
                drawLine(railColor, Offset(cx - railGauge, top + cellSize), Offset(cx - railGauge, cy + innerSize * 0.25f), railWidth, StrokeCap.Square)
                drawLine(railColor, Offset(cx + railGauge, top + cellSize), Offset(cx + railGauge, cy + innerSize * 0.25f), railWidth, StrokeCap.Square)
            }
            Direction.LEFT -> {
                drawLine(railColor, Offset(left, cy - railGauge), Offset(cx - innerSize * 0.25f, cy - railGauge), railWidth, StrokeCap.Square)
                drawLine(railColor, Offset(left, cy + railGauge), Offset(cx - innerSize * 0.25f, cy + railGauge), railWidth, StrokeCap.Square)
            }
            Direction.RIGHT -> {
                drawLine(railColor, Offset(left + cellSize, cy - railGauge), Offset(cx + innerSize * 0.25f, cy - railGauge), railWidth, StrokeCap.Square)
                drawLine(railColor, Offset(left + cellSize, cy + railGauge), Offset(cx + innerSize * 0.25f, cy + railGauge), railWidth, StrokeCap.Square)
            }
        }
    }

    // Platform base (central elevated terminal building)
    val platformRadius = innerSize * 0.32f
    val platformColor = palette.stationPlatform
    val borderColor = if (isConnected) palette.networkHighlight else palette.stationBorder

    if (isConnected) {
        // Soft glow if connected
        drawCircle(
            color = palette.networkGlow,
            radius = platformRadius * 1.35f,
            center = Offset(cx, cy)
        )
    }

    drawCircle(
        color = platformColor,
        radius = platformRadius,
        center = Offset(cx, cy)
    )

    drawCircle(
        color = borderColor,
        radius = platformRadius,
        center = Offset(cx, cy),
        style = Stroke(width = if (isConnected) 3.5f else 2.5f)
    )

    // Inner marker core
    val markerColor = if (isConnected) palette.railHighlightColor else palette.stationMarker
    drawCircle(
        color = markerColor,
        radius = platformRadius * 0.38f,
        center = Offset(cx, cy)
    )
}

fun DrawScope.drawTrackCell(
    palette: BoardPalette,
    tile: TrackTile,
    left: Float,
    top: Float,
    cellSize: Float,
    padding: Float,
    isConnected: Boolean,
    scale: Float = 1.0f
) {
    val innerLeft = left + padding
    val innerTop = top + padding
    val innerSize = cellSize - (padding * 2)
    val cx = left + cellSize * 0.5f
    val cy = top + cellSize * 0.5f

    // Draw track bed ground
    drawRoundRect(
        color = palette.groundCell,
        topLeft = Offset(innerLeft, innerTop),
        size = Size(innerSize, innerSize),
        cornerRadius = CornerRadius(innerSize * 0.12f, innerSize * 0.12f)
    )

    val railGauge = cellSize * 0.15f * scale
    val railWidth = cellSize * 0.052f * scale
    val sleeperColor = palette.sleeperColor
    val railColor = if (isConnected) palette.railHighlightColor else palette.railColor
    val tieHalfLen = railGauge * 1.6f
    val tieWidth = cellSize * 0.065f * scale

    // Network connection subtle underglow
    if (isConnected) {
        drawTrackPath(
            tile = tile,
            left = left,
            top = top,
            cellSize = cellSize,
            cx = cx,
            cy = cy,
            offsetGauge = 0f,
            color = palette.networkGlow,
            strokeWidth = railGauge * 2.2f
        )
    }

    // 1. Draw sleepers (ties)
    drawSleepers(
        tile = tile,
        left = left,
        top = top,
        cellSize = cellSize,
        cx = cx,
        cy = cy,
        tieHalfLen = tieHalfLen,
        tieWidth = tieWidth,
        sleeperColor = sleeperColor
    )

    // 2. Draw dual steel rails
    drawDualRails(
        tile = tile,
        left = left,
        top = top,
        cellSize = cellSize,
        cx = cx,
        cy = cy,
        railGauge = railGauge,
        railWidth = railWidth,
        railColor = railColor
    )

    // 3. Center teal rail accent if connected
    if (isConnected) {
        drawTrackPath(
            tile = tile,
            left = left,
            top = top,
            cellSize = cellSize,
            cx = cx,
            cy = cy,
            offsetGauge = 0f,
            color = palette.networkHighlight,
            strokeWidth = railWidth * 0.8f
        )
    }
}

private fun DrawScope.drawSleepers(
    tile: TrackTile,
    left: Float,
    top: Float,
    cellSize: Float,
    cx: Float,
    cy: Float,
    tieHalfLen: Float,
    tieWidth: Float,
    sleeperColor: Color
) {
    when (tile.kind) {
        TrackKind.STRAIGHT -> {
            val isHorizontal = tile.rotation % 2 == 0
            val steps = listOf(0.2f, 0.4f, 0.6f, 0.8f)
            for (step in steps) {
                if (isHorizontal) {
                    val x = left + cellSize * step
                    drawLine(sleeperColor, Offset(x, cy - tieHalfLen), Offset(x, cy + tieHalfLen), tieWidth, StrokeCap.Square)
                } else {
                    val y = top + cellSize * step
                    drawLine(sleeperColor, Offset(cx - tieHalfLen, y), Offset(cx + tieHalfLen, y), tieWidth, StrokeCap.Square)
                }
            }
        }

        TrackKind.CURVE -> {
            // Draw 4 radial ties along the 90 degree curve arc
            val (cornerX, cornerY, startAngle) = when (tile.rotation % 4) {
                0 -> Triple(left + cellSize, top, 90f)          // UP, RIGHT (center top-right)
                1 -> Triple(left + cellSize, top + cellSize, 180f) // RIGHT, DOWN (center bottom-right)
                2 -> Triple(left, top + cellSize, 270f)          // DOWN, LEFT (center bottom-left)
                else -> Triple(left, top, 0f)                    // LEFT, UP (center top-left)
            }
            val radius = cellSize * 0.5f
            val angles = listOf(18f, 36f, 54f, 72f)
            for (deg in angles) {
                val rad = Math.toRadians((startAngle + deg).toDouble())
                val midX = cornerX + (radius * cos(rad)).toFloat()
                val midY = cornerY + (radius * sin(rad)).toFloat()
                val dx = (cos(rad)).toFloat() * tieHalfLen
                val dy = (sin(rad)).toFloat() * tieHalfLen
                drawLine(sleeperColor, Offset(midX - dx, midY - dy), Offset(midX + dx, midY + dy), tieWidth, StrokeCap.Square)
            }
        }

        TrackKind.T_JUNCTION -> {
            // Sleepers for main straight through and branch
            when (tile.rotation % 4) {
                0 -> { // UP, LEFT, RIGHT
                    listOf(0.2f, 0.4f, 0.6f, 0.8f).forEach { s ->
                        drawLine(sleeperColor, Offset(left + cellSize * s, cy - tieHalfLen), Offset(left + cellSize * s, cy + tieHalfLen), tieWidth, StrokeCap.Square)
                    }
                    drawLine(sleeperColor, Offset(cx - tieHalfLen, top + cellSize * 0.25f), Offset(cx + tieHalfLen, top + cellSize * 0.25f), tieWidth, StrokeCap.Square)
                }
                1 -> { // UP, RIGHT, DOWN
                    listOf(0.2f, 0.4f, 0.6f, 0.8f).forEach { s ->
                        drawLine(sleeperColor, Offset(cx - tieHalfLen, top + cellSize * s), Offset(cx + tieHalfLen, top + cellSize * s), tieWidth, StrokeCap.Square)
                    }
                    drawLine(sleeperColor, Offset(left + cellSize * 0.75f, cy - tieHalfLen), Offset(left + cellSize * 0.75f, cy + tieHalfLen), tieWidth, StrokeCap.Square)
                }
                2 -> { // RIGHT, DOWN, LEFT
                    listOf(0.2f, 0.4f, 0.6f, 0.8f).forEach { s ->
                        drawLine(sleeperColor, Offset(left + cellSize * s, cy - tieHalfLen), Offset(left + cellSize * s, cy + tieHalfLen), tieWidth, StrokeCap.Square)
                    }
                    drawLine(sleeperColor, Offset(cx - tieHalfLen, top + cellSize * 0.75f), Offset(cx + tieHalfLen, top + cellSize * 0.75f), tieWidth, StrokeCap.Square)
                }
                3 -> { // DOWN, LEFT, UP
                    listOf(0.2f, 0.4f, 0.6f, 0.8f).forEach { s ->
                        drawLine(sleeperColor, Offset(cx - tieHalfLen, top + cellSize * s), Offset(cx + tieHalfLen, top + cellSize * s), tieWidth, StrokeCap.Square)
                    }
                    drawLine(sleeperColor, Offset(left + cellSize * 0.25f, cy - tieHalfLen), Offset(left + cellSize * 0.25f, cy + tieHalfLen), tieWidth, StrokeCap.Square)
                }
            }
        }

        TrackKind.CROSS -> {
            // Crossing horizontal and vertical ties
            listOf(0.2f, 0.35f, 0.65f, 0.8f).forEach { s ->
                drawLine(sleeperColor, Offset(left + cellSize * s, cy - tieHalfLen), Offset(left + cellSize * s, cy + tieHalfLen), tieWidth, StrokeCap.Square)
                drawLine(sleeperColor, Offset(cx - tieHalfLen, top + cellSize * s), Offset(cx + tieHalfLen, top + cellSize * s), tieWidth, StrokeCap.Square)
            }
        }
    }
}

private fun DrawScope.drawDualRails(
    tile: TrackTile,
    left: Float,
    top: Float,
    cellSize: Float,
    cx: Float,
    cy: Float,
    railGauge: Float,
    railWidth: Float,
    railColor: Color
) {
    drawTrackPath(tile, left, top, cellSize, cx, cy, -railGauge, railColor, railWidth)
    drawTrackPath(tile, left, top, cellSize, cx, cy, +railGauge, railColor, railWidth)
}

private fun DrawScope.drawTrackPath(
    tile: TrackTile,
    left: Float,
    top: Float,
    cellSize: Float,
    cx: Float,
    cy: Float,
    offsetGauge: Float,
    color: Color,
    strokeWidth: Float
) {
    when (tile.kind) {
        TrackKind.STRAIGHT -> {
            val isHorizontal = tile.rotation % 2 == 0
            if (isHorizontal) {
                val y = cy + offsetGauge
                drawLine(color, Offset(left, y), Offset(left + cellSize, y), strokeWidth, StrokeCap.Square)
            } else {
                val x = cx + offsetGauge
                drawLine(color, Offset(x, top), Offset(x, top + cellSize), strokeWidth, StrokeCap.Square)
            }
        }

        TrackKind.CURVE -> {
            val (cornerX, cornerY, startAngle) = when (tile.rotation % 4) {
                0 -> Triple(left + cellSize, top, 90f)          // UP, RIGHT
                1 -> Triple(left + cellSize, top + cellSize, 180f) // RIGHT, DOWN
                2 -> Triple(left, top + cellSize, 270f)          // DOWN, LEFT
                else -> Triple(left, top, 0f)                    // LEFT, UP
            }
            val radius = (cellSize * 0.5f) + offsetGauge
            if (radius > 0) {
                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = 90f,
                    useCenter = false,
                    topLeft = Offset(cornerX - radius, cornerY - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Square)
                )
            }
        }

        TrackKind.T_JUNCTION -> {
            when (tile.rotation % 4) {
                0 -> { // UP, LEFT, RIGHT
                    val y = cy + offsetGauge
                    drawLine(color, Offset(left, y), Offset(left + cellSize, y), strokeWidth, StrokeCap.Square)
                    val x = cx + offsetGauge
                    drawLine(color, Offset(x, top), Offset(x, cy), strokeWidth, StrokeCap.Square)
                }
                1 -> { // UP, RIGHT, DOWN
                    val x = cx + offsetGauge
                    drawLine(color, Offset(x, top), Offset(x, top + cellSize), strokeWidth, StrokeCap.Square)
                    val y = cy + offsetGauge
                    drawLine(color, Offset(cx, y), Offset(left + cellSize, y), strokeWidth, StrokeCap.Square)
                }
                2 -> { // RIGHT, DOWN, LEFT
                    val y = cy + offsetGauge
                    drawLine(color, Offset(left, y), Offset(left + cellSize, y), strokeWidth, StrokeCap.Square)
                    val x = cx + offsetGauge
                    drawLine(color, Offset(x, cy), Offset(x, top + cellSize), strokeWidth, StrokeCap.Square)
                }
                3 -> { // DOWN, LEFT, UP
                    val x = cx + offsetGauge
                    drawLine(color, Offset(x, top), Offset(x, top + cellSize), strokeWidth, StrokeCap.Square)
                    val y = cy + offsetGauge
                    drawLine(color, Offset(left, y), Offset(cx, y), strokeWidth, StrokeCap.Square)
                }
            }
        }

        TrackKind.CROSS -> {
            val y = cy + offsetGauge
            drawLine(color, Offset(left, y), Offset(left + cellSize, y), strokeWidth, StrokeCap.Square)
            val x = cx + offsetGauge
            drawLine(color, Offset(x, top), Offset(x, top + cellSize), strokeWidth, StrokeCap.Square)
        }
    }
}
