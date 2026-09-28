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
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.railconnect.data.AppThemeMode
import com.example.railconnect.data.BoardThemeMode
import com.example.railconnect.ui.components.ResetStatsDialog
import com.example.railconnect.ui.theme.BurnishedBronze
import com.example.railconnect.ui.theme.IndustrialTealBright
import com.example.railconnect.ui.theme.LocalBoardPalette
import com.example.railconnect.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBackClick: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()
    val palette = LocalBoardPalette.current
    var showResetDialog by remember { mutableStateOf(false) }

    if (showResetDialog) {
        ResetStatsDialog(
            onConfirm = { viewModel.resetStatistics() },
            onDismiss = { showResetDialog = false }
        )
    }

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
                    text = "SETTINGS",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section: Appearance
                SettingsCard(title = "APPEARANCE") {
                    Text(
                        text = "App Theme",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AppThemeMode.entries.forEach { mode ->
                            val isSelected = settings.themeMode == mode
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setThemeMode(mode) },
                                label = { Text(mode.name.lowercase().replaceFirstChar { it.uppercase() }) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BurnishedBronze,
                                    selectedLabelColor = MaterialTheme.colorScheme.background
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Board Palette",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BoardThemeMode.entries.forEach { mode ->
                            val isSelected = settings.boardTheme == mode
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setBoardTheme(mode) },
                                label = { Text(if (mode == BoardThemeMode.CLASSIC) "Classic Slate" else "Midnight Cyan") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = IndustrialTealBright,
                                    selectedLabelColor = MaterialTheme.colorScheme.background
                                )
                            )
                        }
                    }
                }

                // Section: Audio & Feedback
                SettingsCard(title = "FEEDBACK & AUDIO") {
                    SettingsSwitchRow(
                        title = "Sound Effects",
                        description = "Play tactile clicks, thuds, and completion chimes",
                        checked = settings.soundEffects,
                        onCheckedChange = { viewModel.setSoundEffects(it) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    SettingsSwitchRow(
                        title = "Haptic Feedback",
                        description = "Tactile vibration when rotating or placing tracks",
                        checked = settings.hapticFeedback,
                        onCheckedChange = { viewModel.setHapticFeedback(it) }
                    )
                }

                // Section: Game Display
                SettingsCard(title = "GAMEPLAY DISPLAY") {
                    SettingsSwitchRow(
                        title = "Show Connected Network",
                        description = "Highlight active rail tracks linked to the terminal",
                        checked = settings.showConnectedNetwork,
                        onCheckedChange = { viewModel.setShowConnectedNetwork(it) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    SettingsSwitchRow(
                        title = "Show Timer",
                        description = "Display game clock on the top HUD",
                        checked = settings.showTimer,
                        onCheckedChange = { viewModel.setShowTimer(it) }
                    )
                }

                // Section: Data & Records
                SettingsCard(title = "DATA MANAGEMENT") {
                    Button(
                        onClick = { showResetDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f),
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "RESET STATISTICS",
                            style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 1.sp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun SettingsCard(
    title: String,
    content: @Composable () -> Unit
) {
    val palette = LocalBoardPalette.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, palette.groundBorder, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = BurnishedBronze
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = IndustrialTealBright,
                checkedTrackColor = IndustrialTealBright.copy(alpha = 0.5f)
            )
        )
    }
}
