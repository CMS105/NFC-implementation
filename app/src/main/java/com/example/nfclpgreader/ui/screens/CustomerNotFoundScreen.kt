package com.example.nfclpgreader.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nfclpgreader.ui.components.GasSevaHeader
import com.example.nfclpgreader.ui.theme.CardBorder
import com.example.nfclpgreader.ui.theme.ChampagneGold
import com.example.nfclpgreader.ui.theme.ElectricCobalt
import com.example.nfclpgreader.ui.theme.ObsidianBg
import com.example.nfclpgreader.ui.theme.ObsidianContainer
import com.example.nfclpgreader.ui.theme.StatusError
import com.example.nfclpgreader.ui.theme.TextPrimary
import com.example.nfclpgreader.ui.theme.TextSecondary

@Composable
fun CustomerNotFoundScreen(
    customerId: String,
    onRegisterNewCustomer: () -> Unit,
    onScanAnotherPassbook: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = { GasSevaHeader() },
        containerColor = ObsidianBg,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Warning Icon Container
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = StatusError.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StatusError.copy(alpha = 0.3f)),
                    modifier = Modifier.size(72.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.SearchOff,
                            contentDescription = null,
                            tint = StatusError,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Customer Not Found",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "No customer record was found for this NFC passbook.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextSecondary,
                        fontSize = 14.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Details Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ObsidianContainer),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Scanned Customer ID",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TextSecondary,
                                    fontSize = 13.sp
                                )
                            )
                            Text(
                                text = customerId.ifEmpty { "UNKNOWN" },
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = ChampagneGold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Registration Status",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TextSecondary,
                                    fontSize = 13.sp
                                )
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = StatusError.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "Not Registered",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        color = StatusError,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Action Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onRegisterNewCustomer,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricCobalt,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.PersonAdd,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Register New Customer",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        )
                    }
                }

                OutlinedButton(
                    onClick = onScanAnotherPassbook,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = ObsidianContainer,
                        contentColor = TextPrimary
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = TextSecondary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Scan Another Passbook",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
