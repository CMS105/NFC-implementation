package com.example.nfclpgreader.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.nfclpgreader.ui.theme.ChampagneGold
import com.example.nfclpgreader.ui.theme.CobaltGlow
import com.example.nfclpgreader.ui.theme.ElectricCobalt
import com.example.nfclpgreader.ui.theme.GasSevaGreen
import com.example.nfclpgreader.ui.theme.GasSevaGreenDark

/** Ultra-Rich 3D Glowing Emerald Orb with Specular Lighting, Metallic Gold Rim & 3D Checkmark Tick */
@Composable
fun SuccessCheckmarkOrb(
    modifier: Modifier = Modifier,
    onChimeTrigger: () -> Unit = {}
) {
    // 1. Spring scale-in entrance with bounce
    val scale = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        onChimeTrigger()
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    // 2. Slow telemetry halo rotation
    val infiniteTransition = rememberInfiniteTransition(label = "halo_rotation")
    val orbitAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbit_angle"
    )

    // Pulse expand aura
    val auraPulse by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura_pulse"
    )

    Box(
        modifier = modifier
            .size(220.dp)
            .scale(scale.value),
        contentAlignment = Alignment.Center
    ) {
        // Rotating outer metallic gold telemetry ring
        Box(
            modifier = Modifier
                .size(190.dp)
                .rotate(orbitAngle)
                .border(2.dp, ChampagneGold.copy(alpha = 0.75f), CircleShape)
        )

        // Counter-rotating dashed accent ring
        Canvas(
            modifier = Modifier
                .size(170.dp)
                .rotate(-orbitAngle * 1.2f)
        ) {
            drawCircle(
                color = ElectricCobalt.copy(alpha = 0.5f),
                radius = size.minDimension / 2,
                style = Stroke(width = 2f)
            )
        }

        // Radial high-voltage glowing aura
        Box(
            modifier = Modifier
                .size(150.dp * auraPulse)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            GasSevaGreen.copy(alpha = 0.45f),
                            CobaltGlow.copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    )
                )
        )

        // 3D Metallic Emerald Sphere Orb
        Box(
            modifier = Modifier
                .size(116.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFA7F3D0), // Top Specular Emerald Light
                            GasSevaGreen,      // Medium Emerald
                            GasSevaGreenDark,  // Deep Emerald Body
                            Color(0xFF064E3B)  // Shadow Base
                        ),
                        center = Offset(116f * 0.35f, 116f * 0.35f),
                        radius = 116f * 0.85f
                    )
                )
                .border(3.dp, ChampagneGold, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // 3D Glass Specular Reflection Highlight (Upper-Left Oval Gloss)
            Box(
                modifier = Modifier
                    .size(width = 42.dp, height = 20.dp)
                    .offset(x = (-16).dp, y = (-22).dp)
                    .clip(CircleShape)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.65f),
                                Color.White.copy(alpha = 0.05f)
                            )
                        )
                    )
            )

            // 3D Depth Shadow under Checkmark Tick
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = null,
                tint = Color.Black.copy(alpha = 0.35f),
                modifier = Modifier
                    .size(68.dp)
                    .offset(x = 2.dp, y = 3.dp)
            )

            // Primary Crisp White 3D Checkmark Tick
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = "Verified 3D Tick",
                tint = Color.White,
                modifier = Modifier.size(68.dp)
            )
        }
    }
}
