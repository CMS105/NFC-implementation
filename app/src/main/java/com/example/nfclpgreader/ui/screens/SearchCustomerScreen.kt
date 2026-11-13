package com.example.nfclpgreader.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nfclpgreader.R
import com.example.nfclpgreader.ui.components.GasSevaHeader
import com.example.nfclpgreader.ui.theme.BackgroundWhite
import com.example.nfclpgreader.ui.theme.CardBorder
import com.example.nfclpgreader.ui.theme.ErrorRed
import com.example.nfclpgreader.ui.theme.ErrorRedBg
import com.example.nfclpgreader.ui.theme.GasSevaNavy
import com.example.nfclpgreader.ui.theme.TextPrimary
import com.example.nfclpgreader.ui.theme.TextSecondary

@Composable
fun SearchCustomerScreen(
    onSearch: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    searchError: String? = null
) {
    var searchInput by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header bar with back button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = GasSevaNavy
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Search Customer",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = GasSevaNavy,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Enter Customer ID",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Enter the unique Customer ID printed on passbook (e.g. CUS001)",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = searchInput,
                        onValueChange = {
                            searchInput = it
                            localError = null
                        },
                        label = { Text("Customer ID") },
                        placeholder = { Text("e.g. CUS001") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = GasSevaNavy
                            )
                        },
                        trailingIcon = {
                            if (searchInput.isNotEmpty()) {
                                IconButton(onClick = { searchInput = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear text"
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GasSevaNavy,
                            unfocusedBorderColor = CardBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    val activeError = localError ?: searchError
                    if (activeError != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ErrorRedBg,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = activeError,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = ErrorRed,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (searchInput.trim().isEmpty()) {
                                localError = "Please enter a Customer ID"
                            } else {
                                localError = null
                                onSearch(searchInput.trim())
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GasSevaNavy,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Search Database",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
