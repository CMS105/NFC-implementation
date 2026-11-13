package com.example.nfclpgreader.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Sensors
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.nfclpgreader.ui.theme.CardBorder
import com.example.nfclpgreader.ui.theme.ChampagneGold
import com.example.nfclpgreader.ui.theme.CobaltGlow
import com.example.nfclpgreader.ui.theme.ElectricCobalt
import com.example.nfclpgreader.ui.theme.ObsidianContainer
import com.example.nfclpgreader.ui.theme.ObsidianContainerHigh

@Composable
fun CyberRadarScanner(
    modifier: Modifier = Modifier,
    isScanning: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "radar_transition")

    // Balanced 0.8s cadence sweep
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep_angle"
    )

    // Pulse expand animation
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Box(
        modifier = modifier.size(240.dp),
        contentAlignment = Alignment.Center
    ) {
        // Dynamic radar sweep canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val maxRadius = size.minDimension / 2

            // Concentric telemetry rings
            drawCircle(
                color = CardBorder,
                radius = maxRadius * 0.95f,
                style = Stroke(width = 1.5f)
            )
            drawCircle(
                color = CardBorder.copy(alpha = 0.6f),
                radius = maxRadius * 0.65f,
                style = Stroke(width = 1f)
            )
            drawCircle(
                color = ChampagneGold.copy(alpha = 0.35f),
                radius = maxRadius * 0.40f,
                style = Stroke(width = 1f)
            )

            // Scanning beacon gradient sweep
            if (isScanning) {
                drawArc(
                    brush = Brush.sweepGradient(
                        0.0f to Color.Transparent,
                        0.75f to ElectricCobalt.copy(alpha = 0.05f),
                        1.0f to CobaltGlow.copy(alpha = 0.45f)
                    ),
                    startAngle = sweepAngle - 90f,
                    sweepAngle = 90f,
                    useCenter = true
                )
            }
        }

        // Ambient radial blue glow
        Box(
            modifier = Modifier
                .size(140.dp * pulseScale)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            ElectricCobalt.copy(alpha = 0.35f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Center Hardware NFC Reader Hub
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(ObsidianContainerHigh, ObsidianContainer)
                    )
                )
                .border(2.dp, ChampagneGold.copy(alpha = 0.8f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Sensors,
                contentDescription = "NFC Sensor",
                tint = CobaltGlow,
                modifier = Modifier.size(46.dp)
            )
        }
    }
}
