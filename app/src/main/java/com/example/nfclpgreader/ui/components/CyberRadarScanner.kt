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
import com.example.nfclpgreader.ui.theme.ShiningGold

/** Immersive Full-Screen Immersive 3D Cyber Radar Scanner */
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
        initialValue = 0.88f,
        targetValue = 1.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Box(
        modifier = modifier.size(290.dp),
        contentAlignment = Alignment.Center
    ) {
        // Dynamic full-width radar sweep canvas with 3D telemetry rings
        Canvas(modifier = Modifier.fillMaxSize()) {
            val maxRadius = size.minDimension / 2

            // Outer metallic gold rim
            drawCircle(
                color = ShiningGold.copy(alpha = 0.65f),
                radius = maxRadius * 0.98f,
                style = Stroke(width = 2.5f)
            )
            // Concentric telemetry rings
            drawCircle(
                color = CardBorder.copy(alpha = 0.8f),
                radius = maxRadius * 0.82f,
                style = Stroke(width = 1.5f)
            )
            drawCircle(
                color = ElectricCobalt.copy(alpha = 0.45f),
                radius = maxRadius * 0.62f,
                style = Stroke(width = 1.2f)
            )
            drawCircle(
                color = ChampagneGold.copy(alpha = 0.40f),
                radius = maxRadius * 0.40f,
                style = Stroke(width = 1.2f)
            )

            // Scanning beacon gradient sweep
            if (isScanning) {
                drawArc(
                    brush = Brush.sweepGradient(
                        0.0f to Color.Transparent,
                        0.70f to ElectricCobalt.copy(alpha = 0.10f),
                        0.90f to CobaltGlow.copy(alpha = 0.40f),
                        1.0f to ShiningGold.copy(alpha = 0.75f)
                    ),
                    startAngle = sweepAngle - 90f,
                    sweepAngle = 90f,
                    useCenter = true
                )
            }
        }

        // Ambient radial 3D glow aura
        Box(
            modifier = Modifier
                .size(180.dp * pulseScale)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            ElectricCobalt.copy(alpha = 0.45f),
                            CobaltGlow.copy(alpha = 0.18f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Center 3D Metallic NFC Reader Hub (Large 110dp Hub)
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            ObsidianContainerHigh,
                            ObsidianContainer,
                            Color(0xFF04060A)
                        ),
                        center = Offset(55f, 28f),
                        radius = 95f
                    )
                )
                .border(3.dp, ShiningGold, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // 3D Glass Gloss Reflection
            Box(
                modifier = Modifier
                    .size(width = 44.dp, height = 20.dp)
                    .offset(x = (-16).dp, y = (-22).dp)
                    .clip(CircleShape)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.45f),
                                Color.White.copy(alpha = 0.02f)
                            )
                        )
                    )
            )

            Icon(
                imageVector = Icons.Rounded.Sensors,
                contentDescription = "NFC Sensor",
                tint = CobaltGlow,
                modifier = Modifier.size(56.dp)
            )
        }
    }
}
