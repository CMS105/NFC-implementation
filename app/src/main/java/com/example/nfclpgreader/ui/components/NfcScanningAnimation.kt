package com.example.nfclpgreader.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.nfclpgreader.ui.theme.GasSevaGreen
import com.example.nfclpgreader.ui.theme.GasSevaNavy

@Composable
fun NfcScanningAnimation(
    modifier: Modifier = Modifier,
    isScanning: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "nfc_pulse")

    val scale1 by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scale1"
    )

    val alpha1 by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha1"
    )

    val scale2 by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, delayMillis = 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scale2"
    )

    val alpha2 by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, delayMillis = 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha2"
    )

    Box(
        modifier = modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isScanning) {
            Canvas(modifier = Modifier.size(200.dp)) {
                val centerRadius = size.minDimension / 2.5f

                // Outer animated ring 1
                drawCircle(
                    color = GasSevaGreen.copy(alpha = alpha1),
                    radius = centerRadius * scale1,
                    style = Stroke(width = 4.dp.toPx())
                )

                // Outer animated ring 2
                drawCircle(
                    color = GasSevaNavy.copy(alpha = alpha2),
                    radius = centerRadius * scale2,
                    style = Stroke(width = 3.dp.toPx())
                )
            }
        }

        // Inner solid circle with NFC Icon
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(GasSevaNavy)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Nfc,
                contentDescription = "NFC Scanner",
                tint = GasSevaGreen,
                modifier = Modifier.size(64.dp)
            )
        }
    }
}
