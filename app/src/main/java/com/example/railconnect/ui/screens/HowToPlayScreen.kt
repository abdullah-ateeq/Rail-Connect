package com.example.railconnect.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.railconnect.model.TrackKind
import com.example.railconnect.model.TrackTile
import com.example.railconnect.ui.components.TrackTilePreview
import com.example.railconnect.ui.theme.BurnishedBronze
import com.example.railconnect.ui.theme.IndustrialTealBright
import com.example.railconnect.ui.theme.LocalBoardPalette

@Composable
fun HowToPlayScreen(
    onBackClick: () -> Unit
) {
    val palette = LocalBoardPalette.current

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            // Top Navigation Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "HOW TO PLAY",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Track Types Gallery Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, palette.groundBorder, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "AVAILABLE TRACK TYPES",
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 1.4.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = BurnishedBronze
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            TrackKindPreviewItem(kind = TrackKind.STRAIGHT, label = "Straight")
                            TrackKindPreviewItem(kind = TrackKind.CURVE, label = "Curve")
                            TrackKindPreviewItem(kind = TrackKind.T_JUNCTION, label = "T-Junction")
                            TrackKindPreviewItem(kind = TrackKind.CROSS, label = "Cross")
                        }
                    }
                }

                // Step 1: Select & Rotate
                RuleCard(
                    stepNumber = "01",
                    title = "Select & Rotate",
                    description = "Choose 1 of the 3 available track pieces in your tray. Tap ROTATE to turn it 90° clockwise until it faces the desired direction."
                )

                // Step 2: Place on Ground
                RuleCard(
                    stepNumber = "02",
                    title = "Place on Empty Ground",
                    description = "Tap any buildable dark cell to lay down the selected tile. Tracks cannot be placed on stations, blocked terrain, or existing tracks."
                )

                // Step 3: Matching Rail Connections
                RuleCard(
                    stepNumber = "03",
                    title = "Matching Directional Rails",
                    description = "Two neighboring cells connect only if their rail exits match (e.g. a RIGHT exit must face a LEFT exit). Merely touching without matching exits will not connect!"
                )

                // Step 4: Fixed Stations & Network Goal
                RuleCard(
                    stepNumber = "04",
                    title = "Link All 4 Stations",
                    description = "All 4 terminal stations have fixed entry points. Your goal is to connect all four terminals into one unbroken railway network."
                )

                // Step 5: Branches, Loops & Tile Supply
                RuleCard(
                    stepNumber = "05",
                    title = "Branches, Loops & Reserve",
                    description = "Extra branches, loops, and dangling tracks are fully allowed and do not penalize you. Just be mindful of your 18-tile reserve!"
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun TrackKindPreviewItem(
    kind: TrackKind,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        TrackTilePreview(
            tile = TrackTile(id = 0, kind = kind, rotation = 0),
            isSelected = false,
            size = 54.dp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun RuleCard(
    stepNumber: String,
    title: String,
    description: String
) {
    val palette = LocalBoardPalette.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, palette.groundBorder, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, palette.selectedBorder.copy(alpha = 0.7f), RoundedCornerShape(8.dp)),
                color = palette.groundCell
            ) {
                Text(
                    text = stepNumber,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = IndustrialTealBright
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
