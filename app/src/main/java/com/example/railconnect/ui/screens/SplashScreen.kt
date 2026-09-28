package com.example.railconnect.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.railconnect.ui.theme.BurnishedBronze
import com.example.railconnect.ui.theme.IndustrialTealBright
import com.example.railconnect.ui.theme.LocalBoardPalette
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    val palette = LocalBoardPalette.current

    val gridAlpha = remember { Animatable(0f) }
    val stationsScale = remember { Animatable(0f) }
    val tracksProgress = remember { Animatable(0f) }
    val pulseAlpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // 1. Grid fades in (0-300ms)
        gridAlpha.animateTo(1f, tween(300, easing = FastOutSlowInEasing))

        // 2. Stations appear (300-600ms)
        stationsScale.animateTo(1f, tween(300, easing = FastOutSlowInEasing))

        // 3. Track pieces place themselves (600-1000ms)
        tracksProgress.animateTo(1f, tween(400, easing = FastOutSlowInEasing))

        // 4. Rail path pulses (1000-1300ms)
        pulseAlpha.animateTo(1f, tween(200))
        pulseAlpha.animateTo(0.3f, tween(150))

        // 5. Title appears (1200-1500ms)
        textAlpha.animateTo(1f, tween(300))

        delay(300)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onSplashFinished
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated Railway Emblem Canvas
            Canvas(modifier = Modifier.size(170.dp)) {
                val canvasSize = size.width
                val cellSize = canvasSize / 3f
                val corner = CornerRadius(16f, 16f)

                // 1. Draw mini grid
                if (gridAlpha.value > 0f) {
                    for (r in 0 until 3) {
                        for (c in 0 until 3) {
                            val x = c * cellSize + 4f
                            val y = r * cellSize + 4f
                            val s = cellSize - 8f
                            drawRoundRect(
                                color = palette.groundCell.copy(alpha = gridAlpha.value),
                                topLeft = Offset(x, y),
                                size = Size(s, s),
                                cornerRadius = corner
                            )
                            drawRoundRect(
                                color = palette.groundBorder.copy(alpha = gridAlpha.value * 0.7f),
                                topLeft = Offset(x, y),
                                size = Size(s, s),
                                cornerRadius = corner,
                                style = Stroke(width = 1.2f)
                            )
                        }
                    }
                }

                // 2. Draw tracks connecting cells
                if (tracksProgress.value > 0f) {
                    val p = tracksProgress.value
                    val railGauge = cellSize * 0.16f
                    val strokeW = 4f
                    val midY = cellSize * 1.5f
                    val startX = cellSize * 0.5f
                    val endX = startX + (cellSize * 2f * p)

                    // Sleepers
                    val sleeperColor = palette.sleeperColor.copy(alpha = p)
                    for (i in 1..5) {
                        val sx = startX + (cellSize * 0.35f * i)
                        if (sx <= endX) {
                            drawLine(
                                color = sleeperColor,
                                start = Offset(sx, midY - railGauge * 1.6f),
                                end = Offset(sx, midY + railGauge * 1.6f),
                                strokeWidth = 5f,
                                cap = StrokeCap.Square
                            )
                        }
                    }

                    // Dual rails
                    val railColor = if (pulseAlpha.value > 0f) {
                        palette.railHighlightColor
                    } else {
                        palette.railColor
                    }

                    drawLine(
                        color = railColor,
                        start = Offset(startX, midY - railGauge),
                        end = Offset(endX, midY - railGauge),
                        strokeWidth = strokeW,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = railColor,
                        start = Offset(startX, midY + railGauge),
                        end = Offset(endX, midY + railGauge),
                        strokeWidth = strokeW,
                        cap = StrokeCap.Round
                    )

                    // Network pulse line
                    if (pulseAlpha.value > 0f) {
                        drawLine(
                            color = IndustrialTealBright.copy(alpha = pulseAlpha.value),
                            start = Offset(startX, midY),
                            end = Offset(endX, midY),
                            strokeWidth = 3f,
                            cap = StrokeCap.Round
                        )
                    }
                }

                // 3. Draw Stations on left and right
                if (stationsScale.value > 0f) {
                    val r = (cellSize * 0.3f) * stationsScale.value
                    val leftCenter = Offset(cellSize * 0.5f, cellSize * 1.5f)
                    val rightCenter = Offset(cellSize * 2.5f, cellSize * 1.5f)

                    listOf(leftCenter, rightCenter).forEach { center ->
                        drawCircle(
                            color = palette.stationPlatform,
                            radius = r,
                            center = center
                        )
                        drawCircle(
                            color = if (pulseAlpha.value > 0f) palette.networkHighlight else palette.stationBorder,
                            radius = r,
                            center = center,
                            style = Stroke(width = 3f)
                        )
                        drawCircle(
                            color = palette.stationMarker,
                            radius = r * 0.4f,
                            center = center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Title & Subtitle
            Text(
                text = "RAIL CONNECT",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp
                ),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = textAlpha.value)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "SYSTEM READY",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 3.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = BurnishedBronze.copy(alpha = textAlpha.value * 0.9f)
            )
        }
    }
}
