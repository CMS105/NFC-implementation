package com.example.nfclpgreader.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.rounded.Contactless
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Sensors
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nfclpgreader.ui.components.CreatorAboutDialog
import com.example.nfclpgreader.ui.theme.CardBorder
import com.example.nfclpgreader.ui.theme.CardGradBot
import com.example.nfclpgreader.ui.theme.CardGradTop
import com.example.nfclpgreader.ui.theme.EmeraldBg
import com.example.nfclpgreader.ui.theme.EmeraldBorder
import com.example.nfclpgreader.ui.theme.EmeraldText
import com.example.nfclpgreader.ui.theme.GoldBorder
import com.example.nfclpgreader.ui.theme.GoldDeep
import com.example.nfclpgreader.ui.theme.GoldInk
import com.example.nfclpgreader.ui.theme.GoldLight
import com.example.nfclpgreader.ui.theme.GoldMid
import com.example.nfclpgreader.ui.theme.GoldTintBg
import com.example.nfclpgreader.ui.theme.ObsidianBg
import com.example.nfclpgreader.ui.theme.ObsidianContainer
import com.example.nfclpgreader.ui.theme.StatusError
import com.example.nfclpgreader.ui.theme.TextMuted
import com.example.nfclpgreader.ui.theme.TextPrimary
import com.example.nfclpgreader.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    onScanPassbookClick: () -> Unit,
    onManualSearchClick: () -> Unit,
    onSimulateScanSuccess: () -> Unit,
    onSimulateScanFailed: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAboutDialog by remember { mutableStateOf(false) }

    // Metallic sheen sweep animation
    val infiniteTransition = rememberInfiniteTransition(label = "sheen_transition")
    val sheenOffset by infiniteTransition.animateFloat(
        initialValue = -300f,
        targetValue = 600f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sheen_offset"
    )

    // Concentric radar ring expansion
    val radarExpand1 by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radar_expand_1"
    )

    Scaffold(
        containerColor = ObsidianBg,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Flame Emblem & App Title
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(GoldLight, GoldMid, GoldDeep)
                                )
                            )
                            .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.LocalFireDepartment,
                            contentDescription = null,
                            tint = GoldInk,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "GasSeva",
                                color = TextPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Rounded.Shield,
                                contentDescription = null,
                                tint = GoldMid,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Text(
                            text = "LPG Passbook Terminal",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Right Status Controls: READY Pill & Info Button
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = EmeraldBg,
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldText)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "READY",
                                color = EmeraldText,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(GoldTintBg)
                            .border(1.dp, GoldBorder, CircleShape)
                            .clickable { showAboutDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "About Creator",
                            tint = GoldMid,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Encrypted Terminal Unit Badge
                Surface(
                    shape = RoundedCornerShape(50),
                    color = GoldTintBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(GoldMid)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ENCRYPTED TERMINAL • UNIT #04",
                            color = GoldMid,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "LPG Customer Identification",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Welcome to GasSeva. Select an option below to identify customer & verify LPG passbook",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Interactive Virtual Smart Passbook Card (Brushed Luxury Hardware)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(28.dp))
                        .clickable { onScanPassbookClick() },
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = ObsidianContainer),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(CardGradTop, CardGradBot)
                                )
                            )
                            .padding(20.dp)
                    ) {
                        // Diagonal Metallic Gold Sheen Layer
                        Canvas(modifier = Modifier.matchParentSize()) {
                            drawRect(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        GoldLight.copy(alpha = 0.08f),
                                        GoldLight.copy(alpha = 0.18f),
                                        Color.Transparent
                                    ),
                                    start = Offset(sheenOffset, 0f),
                                    end = Offset(sheenOffset + 250f, size.height)
                                )
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Top Hardware Ticks
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = GoldTintBg,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(GoldMid)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "13.56 MHz RF READY",
                                            color = GoldMid,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Text(
                                    text = "ISO 14443-A",
                                    color = TextMuted,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Concentric Gold Radar Pulse Rings & Solid Contactless Medallion
                            Box(
                                modifier = Modifier.size(150.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                // Expanding Radar Ring 1
                                Canvas(modifier = Modifier.size(130.dp * radarExpand1)) {
                                    drawCircle(
                                        color = GoldMid.copy(alpha = (1.35f - radarExpand1).coerceIn(0f, 0.6f)),
                                        radius = size.minDimension / 2,
                                        style = Stroke(width = 1.5f)
                                    )
                                }

                                // Outer Brushed Halo
                                Surface(
                                    shape = CircleShape,
                                    color = GoldTintBg,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder),
                                    modifier = Modifier.size(108.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        // Solid Champagne Gold Contactless Medallion
                                        Box(
                                            modifier = Modifier
                                                .size(68.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    Brush.linearGradient(
                                                        colors = listOf(GoldLight, GoldMid, GoldDeep)
                                                    )
                                                )
                                                .border(1.dp, Color.White.copy(alpha = 0.45f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Contactless,
                                                contentDescription = null,
                                                tint = GoldInk,
                                                modifier = Modifier.size(38.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "NTAG213 Smart Passbook Ledger",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // 13.56 MHz Encrypted Verification Badge
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = EmeraldBg,
                                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.VerifiedUser,
                                        contentDescription = null,
                                        tint = EmeraldText,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "13.56 MHz Encrypted Verification",
                                        color = EmeraldText,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Tap Hint Footer
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Tap card surface to initiate contactless read",
                                    color = GoldMid,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = GoldMid,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Primary Actions: Action A (Scan NFC Passbook) & Action B (Enter Customer ID Manually)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                // Action A: Lustrous Champagne Gold Gradient Button (btn-champagne)
                Button(
                    onClick = onScanPassbookClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.45f), RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = GoldInk
                    ),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(GoldLight, GoldMid, GoldDeep)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Rounded.Sensors, contentDescription = null, tint = GoldInk, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Scan NFC Passbook",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = GoldInk,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action B: Charcoal Glass Secondary Button (btn-secondary-glass)
                OutlinedButton(
                    onClick = onManualSearchClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = ObsidianContainer),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                ) {
                    Icon(Icons.Rounded.Search, contentDescription = null, tint = GoldMid, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Enter Customer ID Manually",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Simulator triggers for testing without physical tags
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onSimulateScanSuccess,
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = ObsidianContainer),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                    ) {
                        Text("Simulate Success", color = GoldMid, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                    OutlinedButton(
                        onClick = onSimulateScanFailed,
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = ObsidianContainer),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                    ) {
                        Text("Simulate Error", color = StatusError, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // About GasSeva & Creator Attribution Modal Dialog
            if (showAboutDialog) {
                CreatorAboutDialog(onDismiss = { showAboutDialog = false })
            }
        }
    }
}
