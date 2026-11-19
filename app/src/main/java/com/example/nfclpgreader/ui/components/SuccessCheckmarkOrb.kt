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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.nfclpgreader.ui.theme.EmeraldText
import com.example.nfclpgreader.ui.theme.GasSevaGreen
import com.example.nfclpgreader.ui.theme.GasSevaGreenDark
import com.example.nfclpgreader.ui.theme.ShiningGold
import com.example.nfclpgreader.util.SoundHelper
import kotlin.math.cos
import kotlin.math.sin

/** Google Pay Style 3D Glowing Emerald Checkmark Orb with Multi-Colored Confetti Burst & Crystal Victory Chime */
@Composable
fun SuccessCheckmarkOrb(
    modifier: Modifier = Modifier,
    onChimeTrigger: () -> Unit = {}
) {
    val context = LocalContext.current
    val soundHelper = remember { SoundHelper(context.applicationContext) }

    // 1. Google Pay style Spring Bouncy Entrance
    val orbScale = remember { Animatable(0f) }
    val tickScale = remember { Animatable(0f) }
    val burstProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        onChimeTrigger()
        soundHelper.playSuccessChime()

        // Orb Bouncy Scale Entrance (0 -> 1.15 -> 1.0)
        orbScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )

        // Tick Spring Pop-in (0 -> 1.2 -> 1.0)
        tickScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioHighBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )

        // Starburst Confetti Burst Animation
        burstProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(700, easing = LinearEasing)
        )
    }

    // Telemetry halo & pulse rotation
    val infiniteTransition = rememberInfiniteTransition(label = "google_pay_halo")
    val auraPulse by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura_pulse"
    )

    Box(
        modifier = modifier
            .size(260.dp)
            .scale(orbScale.value),
        contentAlignment = Alignment.Center
    ) {
        // Google Pay Multi-Colored Confetti / Starburst Burst Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val numParticles = 16
            val maxDistance = 118.dp.toPx() * burstProgress.value
            val alpha = (1f - burstProgress.value).coerceIn(0f, 1f)

            val colors = listOf(
                EmeraldText,
                ShiningGold,
                Color(0xFF38BDF8), // Cyan Blue
                Color.White
            )

            for (i in 0 until numParticles) {
                val angle = Math.toRadians((i * (360.0 / numParticles)))
                val color = colors[i % colors.size]
                val particleOffset = Offset(
                    x = center.x + (maxDistance * cos(angle)).toFloat(),
                    y = center.y + (maxDistance * sin(angle)).toFloat()
                )
                drawCircle(
                    color = color.copy(alpha = alpha * 0.95f),
                    radius = (4.dp.toPx() * (1f - burstProgress.value * 0.4f)),
                    center = particleOffset
                )
            }
        }

        // Radial Glowing Emerald Backdrop Aura
        Box(
            modifier = Modifier
                .size(175.dp * auraPulse)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            EmeraldText.copy(alpha = 0.55f),
                            GasSevaGreen.copy(alpha = 0.22f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Outer Gold Accent Halo Ring
        Canvas(modifier = Modifier.size(155.dp)) {
            drawCircle(
                color = ShiningGold.copy(alpha = 0.6f),
                radius = size.minDimension / 2,
                style = Stroke(width = 2.5f)
            )
        }

        // Main Google Pay 3D Glowing Emerald Sphere Orb
        Box(
            modifier = Modifier
                .size(126.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFA7F3D0), // Top Specular Light Highlight
                            EmeraldText,       // Bright Emerald
                            GasSevaGreen,      // Medium Emerald
                            GasSevaGreenDark,  // Deep Emerald Body
                            Color(0xFF047857)  // Deep Shadow Base
                        ),
                        center = Offset(126f * 0.35f, 126f * 0.35f),
                        radius = 126f * 0.85f
                    )
                )
                .border(3.5.dp, ShiningGold, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // 3D Glass Specular Reflection Oval Gloss
            Box(
                modifier = Modifier
                    .size(width = 52.dp, height = 26.dp)
                    .offset(x = (-18).dp, y = (-28).dp)
                    .clip(CircleShape)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.80f),
                                Color.White.copy(alpha = 0.05f)
                            )
                        )
                    )
            )

            // 3D Inner Rim Highlight
            Box(
                modifier = Modifier
                    .size(112.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, Color.White.copy(alpha = 0.45f), CircleShape)
            )

            // Spring Pop-In 3D White Checkmark Tick
            Box(
                modifier = Modifier.scale(tickScale.value),
                contentAlignment = Alignment.Center
            ) {
                // 3D Depth Shadow under Checkmark Tick
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = null,
                    tint = Color.Black.copy(alpha = 0.45f),
                    modifier = Modifier
                        .size(68.dp)
                        .offset(x = 2.5.dp, y = 3.5.dp)
                )

                // Primary Crisp White 3D Checkmark Tick
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = "Google Pay Style Verified 3D Tick",
                    tint = Color.White,
                    modifier = Modifier.size(68.dp)
                )
            }
        }
    }
}
