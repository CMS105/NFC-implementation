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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.nfclpgreader.ui.theme.ChampagneGold
import com.example.nfclpgreader.ui.theme.CobaltGlow
import com.example.nfclpgreader.ui.theme.RoyalCobalt

@Composable
fun SuccessCheckmarkOrb(
    modifier: Modifier = Modifier,
    onChimeTrigger: () -> Unit = {}
) {
    // 1. Spring scale-in entrance
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

    // 2. Slow gold telemetry halo rotation
    val infiniteTransition = rememberInfiniteTransition(label = "halo_rotation")
    val orbitAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbit_angle"
    )

    Box(
        modifier = modifier
            .size(200.dp)
            .scale(scale.value),
        contentAlignment = Alignment.Center
    ) {
        // Rotating outer champagne ring
        Box(
            modifier = Modifier
                .size(180.dp)
                .rotate(orbitAngle)
                .border(1.5.dp, ChampagneGold.copy(alpha = 0.55f), CircleShape)
        )

        // Radial high-voltage aura
        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            CobaltGlow.copy(alpha = 0.55f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Big Primary Cobalt Checkmark Orb
        Box(
            modifier = Modifier
                .size(108.dp)
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(CobaltGlow, RoyalCobalt)
                    )
                )
                .border(2.5.dp, Color.White.copy(alpha = 0.85f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = "Decoded Checkmark",
                tint = Color.White,
                modifier = Modifier.size(62.dp)
            )
        }
    }
}
