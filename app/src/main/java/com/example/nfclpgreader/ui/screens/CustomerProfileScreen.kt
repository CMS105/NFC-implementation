package com.example.nfclpgreader.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.nfclpgreader.R
import com.example.nfclpgreader.data.model.Customer
import com.example.nfclpgreader.ui.components.GasSevaHeader
import com.example.nfclpgreader.ui.theme.BackgroundWhite
import com.example.nfclpgreader.ui.theme.CardBorder
import com.example.nfclpgreader.ui.theme.GasSevaGreen
import com.example.nfclpgreader.ui.theme.GasSevaGreenDark
import com.example.nfclpgreader.ui.theme.GasSevaGreenLight
import com.example.nfclpgreader.ui.theme.GasSevaNavy
import com.example.nfclpgreader.ui.theme.SurfaceOffWhite
import com.example.nfclpgreader.ui.theme.TextPrimary
import com.example.nfclpgreader.ui.theme.TextSecondary

@Composable
fun CustomerProfileScreen(
    customer: Customer,
    onBackToScan: () -> Unit,
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
                .padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // "CUSTOMER VERIFIED" Header Badge
                Surface(
                    shape = RoundedCornerShape(50),
                    color = GasSevaGreenLight,
                    border = androidx.compose.foundation.BorderStroke(1.dp, GasSevaGreen.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = GasSevaGreenDark,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.customer_verified),
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = GasSevaGreenDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Customer Identity Card containing ONLY Photo, Full Name, and Customer ID
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 1. Large Customer Photograph Frame
                        Box(
                            modifier = Modifier
                                .size(160.dp)
                                .clip(CircleShape)
                                .border(width = 4.dp, color = GasSevaGreen, shape = CircleShape)
                                .background(SurfaceOffWhite),
                            contentAlignment = Alignment.Center
                        ) {
                            if (customer.photoPath.isNotEmpty()) {
                                AsyncImage(
                                    model = customer.photoPath,
                                    contentDescription = "Photograph of ${customer.fullName}",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                )
                            } else {
                                val drawableId = if (customer.photoResId != 0) customer.photoResId else R.drawable.ic_avatar_ravi
                                Image(
                                    painter = painterResource(id = drawableId),
                                    contentDescription = "Photograph of ${customer.fullName}",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // 2. Customer Full Name Field
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = SurfaceOffWhite,
                            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.label_name),
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        color = TextSecondary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = customer.fullName,
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // 3. Customer ID Field
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = SurfaceOffWhite,
                            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.label_customer_id),
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        color = TextSecondary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = customer.customerId,
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        color = GasSevaNavy,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 22.sp,
                                        letterSpacing = 1.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Back Button
            Button(
                onClick = onBackToScan,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GasSevaNavy,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Back",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )
                }
            }
        }
    }
}
