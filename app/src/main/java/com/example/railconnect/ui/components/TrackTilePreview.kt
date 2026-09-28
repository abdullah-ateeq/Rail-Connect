package com.example.railconnect.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.railconnect.model.TrackTile
import com.example.railconnect.ui.theme.LocalBoardPalette

@Composable
fun TrackTilePreview(
    tile: TrackTile?,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null,
    size: Dp = 68.dp,
    modifier: Modifier = Modifier
) {
    val palette = LocalBoardPalette.current
    val shape = RoundedCornerShape(12.dp)

    // Quick smooth rotation animation
    val animatedAngle by animateFloatAsState(
        targetValue = (tile?.rotation ?: 0) * 90f,
        animationSpec = tween(durationMillis = 130),
        label = "tile_rotation"
    )

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .size(size)
            .offset(y = if (isSelected) (-4).dp else 0.dp)
            .shadow(
                elevation = if (isSelected) 8.dp else 2.dp,
                shape = shape,
                spotColor = if (isSelected) palette.selectedBorder else Color.Black
            )
            .clip(shape)
            .background(palette.groundCell)
            .border(
                width = if (isSelected) 2.5.dp else 1.dp,
                color = if (isSelected) palette.selectedBorder else palette.groundBorder,
                shape = shape
            )
            .then(
                if (onClick != null && tile != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick
                    )
                } else Modifier
            )
            .semantics {
                role = Role.Button
                this.selected = isSelected
                contentDescription = tile?.contentDescription ?: "Empty tray slot"
            },
        contentAlignment = Alignment.Center
    ) {
        if (tile != null) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(5.dp)
                    // Render base unrotated tile, then apply rotation via Canvas transform or drawTrackCell with rotation 0
                    .rotate(animatedAngle)
            ) {
                // Using base tile (rotation = 0) because .rotate(animatedAngle) rotates the canvas
                val baseTile = tile.copy(rotation = 0)
                drawTrackCell(
                    palette = palette,
                    tile = baseTile,
                    left = 0f,
                    top = 0f,
                    cellSize = this.size.width,
                    padding = 0f,
                    isConnected = isSelected,
                    scale = 0.95f
                )
            }
        }
    }
}
