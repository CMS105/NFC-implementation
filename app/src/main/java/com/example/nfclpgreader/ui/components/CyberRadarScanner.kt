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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.geometry.Offset
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

    // Smooth 360 degree 0.8s sweep
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep_angle"
    )

    // 3D Pulse expansion animation
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
        // Dynamic radar sweep canvas with 3D telemetry rings
        Canvas(modifier = Modifier.fillMaxSize()) {
            val maxRadius = size.minDimension / 2

            // Outer metallic gold ring
            drawCircle(
                color = ChampagneGold.copy(alpha = 0.5f),
                radius = maxRadius * 0.98f,
                style = Stroke(width = 2f)
            )
            // Concentric telemetry rings
            drawCircle(
                color = CardBorder,
                radius = maxRadius * 0.82f,
                style = Stroke(width = 1.5f)
            )
            drawCircle(
                color = ElectricCobalt.copy(alpha = 0.4f),
                radius = maxRadius * 0.58f,
                style = Stroke(width = 1f)
            )
            drawCircle(
                color = ChampagneGold.copy(alpha = 0.35f),
                radius = maxRadius * 0.36f,
                style = Stroke(width = 1f)
            )

            // Scanning beacon gradient sweep
            if (isScanning) {
                drawArc(
                    brush = Brush.sweepGradient(
                        0.0f to Color.Transparent,
                        0.70f to ElectricCobalt.copy(alpha = 0.08f),
                        0.90f to CobaltGlow.copy(alpha = 0.35f),
                        1.0f to ChampagneGold.copy(alpha = 0.65f)
                    ),
                    startAngle = sweepAngle - 90f,
                    sweepAngle = 90f,
                    useCenter = true
                )
            }
        }

        // Ambient radial blue 3D glow
        Box(
            modifier = Modifier
                .size(150.dp * pulseScale)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            ElectricCobalt.copy(alpha = 0.4f),
                            CobaltGlow.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Center 3D Metallic NFC Reader Hub
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            ObsidianContainerHigh,
                            ObsidianContainer,
                            Color(0xFF070C18)
                        ),
                        center = Offset(48f, 24f),
                        radius = 80f
                    )
                )
                .border(2.5.dp, ChampagneGold, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // 3D Glass Gloss Reflection
            Box(
                modifier = Modifier
                    .size(width = 36.dp, height = 16.dp)
                    .offset(x = (-12).dp, y = (-18).dp)
                    .clip(CircleShape)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.4f),
                                Color.White.copy(alpha = 0.02f)
                            )
                        )
                    )
            )

            Icon(
                imageVector = Icons.Rounded.Sensors,
                contentDescription = "NFC Sensor",
                tint = CobaltGlow,
                modifier = Modifier.size(50.dp)
            )
        }
    }
}
