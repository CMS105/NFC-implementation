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
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.nfclpgreader.ui.theme.ChampagneGold
import com.example.nfclpgreader.ui.theme.EmeraldText
import com.example.nfclpgreader.ui.theme.GasSevaGreen
import com.example.nfclpgreader.ui.theme.GasSevaGreenDark
import com.example.nfclpgreader.ui.theme.ShiningGold
import com.example.nfclpgreader.util.SoundHelper
import kotlin.math.cos
import kotlin.math.sin

/** Ultra-Luxury 3D Glowing Emerald Orb with Starburst Sparkles, Specular Lighting, Metallic Gold Rim & Crystal Chime */
@Composable
fun SuccessCheckmarkOrb(
    modifier: Modifier = Modifier,
    onChimeTrigger: () -> Unit = {}
) {
    val context = LocalContext.current
    val soundHelper = remember { SoundHelper(context.applicationContext) }

    // 1. Spring scale-in entrance with bounce
    val scale = remember { Animatable(0f) }
    val burstProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        onChimeTrigger()
        soundHelper.playSuccessChime()

        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        burstProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(600, easing = LinearEasing)
        )
    }

    // 2. Telemetry halo rotation
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
            .size(240.dp)
            .scale(scale.value),
        contentAlignment = Alignment.Center
    ) {
        // Outer Sparkling Gold Burst Particles Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val numParticles = 12
            val maxDistance = 95.dp.toPx() * burstProgress.value
            val alpha = (1f - burstProgress.value).coerceIn(0f, 1f)

            for (i in 0 until numParticles) {
                val angle = Math.toRadians((i * (360.0 / numParticles)))
                val particleOffset = Offset(
                    x = center.x + (maxDistance * cos(angle)).toFloat(),
                    y = center.y + (maxDistance * sin(angle)).toFloat()
                )
                drawCircle(
                    color = ShiningGold.copy(alpha = alpha * 0.85f),
                    radius = (3.dp.toPx() * (1f - burstProgress.value * 0.5f)),
                    center = particleOffset
                )
            }
        }

        // Rotating outer metallic gold telemetry ring
        Box(
            modifier = Modifier
                .size(190.dp)
                .rotate(orbitAngle)
                .border(2.dp, ChampagneGold.copy(alpha = 0.85f), CircleShape)
        )

        // Counter-rotating dashed accent ring
        Canvas(
            modifier = Modifier
                .size(170.dp)
                .rotate(-orbitAngle * 1.2f)
        ) {
            drawCircle(
                color = EmeraldText.copy(alpha = 0.5f),
                radius = size.minDimension / 2,
                style = Stroke(width = 2f)
            )
        }

        // Radial high-voltage glowing emerald aura
        Box(
            modifier = Modifier
                .size(150.dp * auraPulse)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            EmeraldText.copy(alpha = 0.5f),
                            GasSevaGreen.copy(alpha = 0.25f),
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
                            GasSevaGreen,      // Medium Emerald Body
                            GasSevaGreenDark,  // Deep Emerald Body
                            Color(0xFF064E3B)  // Shadow Base
                        ),
                        center = Offset(116f * 0.35f, 116f * 0.35f),
                        radius = 116f * 0.85f
                    )
                )
                .border(3.dp, ShiningGold, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // 3D Glass Specular Reflection Highlight
            Box(
                modifier = Modifier
                    .size(width = 42.dp, height = 20.dp)
                    .offset(x = (-16).dp, y = (-22).dp)
                    .clip(CircleShape)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.7f),
                                Color.White.copy(alpha = 0.05f)
                            )
                        )
                    )
            )

            // 3D Depth Shadow under Checkmark Tick
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = null,
                tint = Color.Black.copy(alpha = 0.4f),
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
