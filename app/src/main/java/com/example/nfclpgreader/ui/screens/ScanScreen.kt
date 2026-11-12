package com.example.nfclpgreader.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nfclpgreader.R
import com.example.nfclpgreader.data.SampleCustomerData
import com.example.nfclpgreader.nfc.NfcStatus
import com.example.nfclpgreader.ui.components.GasSevaHeader
import com.example.nfclpgreader.ui.components.NfcScanningAnimation
import com.example.nfclpgreader.ui.components.NfcStatusCard
import com.example.nfclpgreader.ui.theme.BackgroundWhite
import com.example.nfclpgreader.ui.theme.CardBorder
import com.example.nfclpgreader.ui.theme.GasSevaGreen
import com.example.nfclpgreader.ui.theme.GasSevaNavy
import com.example.nfclpgreader.ui.theme.SurfaceOffWhite
import com.example.nfclpgreader.ui.theme.TextPrimary
import com.example.nfclpgreader.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ScanScreen(
    nfcStatus: NfcStatus,
    onOpenNfcSettings: () -> Unit,
    onSimulateScan: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDemoSection by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { GasSevaHeader() },
        containerColor = BackgroundWhite,
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
                // Status Badge
                NfcStatusCard(
                    nfcStatus = nfcStatus,
                    onOpenNfcSettings = onOpenNfcSettings
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Pulsing NFC scanning graphic
                NfcScanningAnimation(isScanning = nfcStatus is NfcStatus.Ready)

                Spacer(modifier = Modifier.height(28.dp))

                // Action Title
                Text(
                    text = stringResource(id = R.string.tap_passbook_title),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = GasSevaNavy,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Scanning Instructions Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = SurfaceOffWhite,
                    border = CardBorder.let { androidx.compose.foundation.BorderStroke(1.dp, it) }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = GasSevaNavy,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(id = R.string.tap_passbook_instructions),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextSecondary,
                                fontSize = 13.5.sp,
                                lineHeight = 19.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Demo / Test scan panel for emulator & device testing
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { showDemoSection = !showDemoSection }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.BugReport,
                        contentDescription = "Demo Mode",
                        tint = GasSevaNavy.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (showDemoSection) "Hide Sample Tag Scanner" else "Test Scan (No Physical Tag)",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = GasSevaNavy.copy(alpha = 0.85f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                AnimatedVisibility(
                    visible = showDemoSection,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceOffWhite),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Simulate NFC Tag Tap",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 14.sp,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Select a sample customer NFC tag URL or ID to verify scanning flow:",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    textAlign = TextAlign.Center
                                )
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                maxItemsInEachRow = 3
                            ) {
                                SampleCustomerData.customers.forEach { customer ->
                                    Box(
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(GasSevaNavy)
                                            .clickable {
                                                onSimulateScan("https://example.com/customer/${customer.customerId}")
                                            }
                                            .padding(horizontal = 12.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = "${customer.customerId} (${customer.fullName.split(" ").first()})",
                                            style = MaterialTheme.typography.labelLarge.copy(
                                                color = Color.White,
                                                fontSize = 12.sp
                                            )
                                        )
                                    }
                                }

                                // Invalid tag button for testing error handling
                                Box(
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFEF4444))
                                        .clickable {
                                            onSimulateScan("https://example.com/customer/CUS999")
                                        }
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = "CUS999 (Invalid)",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            color = Color.White,
                                            fontSize = 12.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
