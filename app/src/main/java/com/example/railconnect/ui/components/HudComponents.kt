package com.example.railconnect.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.RotateRight
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.railconnect.ui.theme.BurnishedBronze
import com.example.railconnect.ui.theme.IndustrialTealBright
import com.example.railconnect.ui.theme.LocalBoardPalette
import java.util.Locale

@Composable
fun GameHud(
    connectedStations: Int,
    totalStations: Int,
    tracksLeft: Int,
    moves: Int,
    elapsedMillis: Long,
    showTimer: Boolean,
    onBackClick: () -> Unit,
    onRestartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalBoardPalette.current

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, palette.groundBorder, RoundedCornerShape(14.dp)),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            // Top row: Back button, Title / status, Restart button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Menu",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "RAIL CONNECT",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                IconButton(onClick = onRestartClick) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Restart Puzzle",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                HudStatItem(
                    label = "STATIONS",
                    value = "$connectedStations / $totalStations",
                    highlight = connectedStations == totalStations
                )

                HudDivider()

                HudStatItem(
                    label = "TRACKS LEFT",
                    value = "$tracksLeft",
                    highlight = false
                )

                HudDivider()

                HudStatItem(
                    label = "MOVES",
                    value = "$moves",
                    highlight = false
                )

                if (showTimer) {
                    HudDivider()
                    val totalSec = elapsedMillis / 1000
                    val min = totalSec / 60
                    val sec = totalSec % 60
                    val timeFormatted = String.format(Locale.US, "%02d:%02d", min, sec)
                    HudStatItem(
                        label = "TIME",
                        value = timeFormatted,
                        highlight = false
                    )
                }
            }
        }
    }
}

@Composable
private fun HudStatItem(
    label: String,
    value: String,
    highlight: Boolean
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            ),
            color = if (highlight) IndustrialTealBright else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun HudDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(24.dp)
            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
    )
}

@Composable
fun StatusBanner(
    message: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)),
        color = IndustrialTealBright.copy(alpha = 0.18f),
        border = androidx.compose.foundation.BorderStroke(1.dp, IndustrialTealBright.copy(alpha = 0.7f))
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp),
            style = MaterialTheme.typography.labelLarge.copy(
                letterSpacing = 1.8.sp,
                fontWeight = FontWeight.Bold
            ),
            color = IndustrialTealBright,
            maxLines = 1
        )
    }
}

@Composable
fun ActionControls(
    canRotate: Boolean,
    canUndo: Boolean,
    onRotateClick: () -> Unit,
    onUndoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Rotate button (primary tactical action)
        Button(
            onClick = onRotateClick,
            enabled = canRotate,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BurnishedBronze,
                contentColor = Color(0xFF231405),
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.RotateRight,
                contentDescription = null,
                modifier = Modifier.padding(end = 6.dp)
            )
            Text(
                text = "ROTATE",
                style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 1.2.sp)
            )
        }

        // Undo button
        OutlinedButton(
            onClick = onUndoClick,
            enabled = canUndo,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurface,
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Undo,
                contentDescription = null,
                modifier = Modifier.padding(end = 6.dp)
            )
            Text(
                text = "UNDO",
                style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 1.2.sp)
            )
        }
    }
}
