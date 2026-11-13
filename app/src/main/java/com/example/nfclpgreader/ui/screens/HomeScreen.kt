package com.example.nfclpgreader.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nfclpgreader.ui.components.GasSevaHeader
import com.example.nfclpgreader.ui.theme.BackgroundWhite
import com.example.nfclpgreader.ui.theme.CardBorder
import com.example.nfclpgreader.ui.theme.GasSevaGreen
import com.example.nfclpgreader.ui.theme.GasSevaGreenLight
import com.example.nfclpgreader.ui.theme.GasSevaNavy
import com.example.nfclpgreader.ui.theme.SurfaceOffWhite
import com.example.nfclpgreader.ui.theme.TextPrimary
import com.example.nfclpgreader.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    onNavigateToScan: () -> Unit,
    onNavigateToSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome to Gas Seva",
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = GasSevaNavy,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Select an operation to manage LPG passbook identity records",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Option 1: Scan NFC & Store
            ActionCard(
                title = "Scan NFC & Store",
                description = "Tap customer passbook NFC sticker to view profile or register new customer",
                icon = Icons.Default.Nfc,
                accentColor = GasSevaGreen,
                accentBg = GasSevaGreenLight,
                onClick = onNavigateToScan
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Option 2: Search Customer
            ActionCard(
                title = "Search Customer",
                description = "Lookup customer identity manually by entering Customer ID",
                icon = Icons.Default.PersonSearch,
                accentColor = GasSevaNavy,
                accentBg = SurfaceOffWhite,
                onClick = onNavigateToSearch
            )
        }
    }
}

@Composable
private fun ActionCard(
    title: String,
    description: String,
    icon: ImageVector,
    accentColor: Color,
    accentBg: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = accentBg),
                modifier = Modifier.size(54.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
