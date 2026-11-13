package com.example.nfclpgreader.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.nfclpgreader.R
import com.example.nfclpgreader.ui.components.GasSevaHeader
import com.example.nfclpgreader.ui.theme.BackgroundWhite
import com.example.nfclpgreader.ui.theme.CardBorder
import com.example.nfclpgreader.ui.theme.ErrorRed
import com.example.nfclpgreader.ui.theme.ErrorRedBg
import com.example.nfclpgreader.ui.theme.GasSevaGreen
import com.example.nfclpgreader.ui.theme.GasSevaNavy
import com.example.nfclpgreader.ui.theme.SurfaceOffWhite
import com.example.nfclpgreader.ui.theme.TextSecondary
import java.io.File

@Composable
fun RegisterCustomerScreen(
    scannedTagValue: String,
    suggestedCustomerId: String,
    onSaveCustomer: (customerId: String, fullName: String, photoUriStr: String, tagValue: String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var customerIdInput by remember { mutableStateOf(suggestedCustomerId) }
    var fullNameInput by remember { mutableStateOf("") }
    var selectedPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var cameraTempUri by remember { mutableStateOf<Uri?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraTempUri != null) {
            selectedPhotoUri = cameraTempUri
        }
    }

    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            selectedPhotoUri = uri
        }
    }

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
            // Header with Back button
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
                    text = "New Customer Registration",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = GasSevaNavy,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Customer Photo Frame
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .clip(CircleShape)
                            .border(width = 3.dp, color = GasSevaGreen, shape = CircleShape)
                            .background(SurfaceOffWhite),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedPhotoUri != null) {
                            AsyncImage(
                                model = selectedPhotoUri,
                                contentDescription = "Selected Customer Photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.ic_avatar_ravi),
                                contentDescription = "Default Avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Buttons for Take Photo / Select from Gallery
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        OutlinedButton(
                            onClick = {
                                try {
                                    val tempFile = File.createTempFile("camera_photo_", ".jpg", context.cacheDir)
                                    val uri = FileProvider.getUriForFile(
                                        context,
                                        "${context.packageName}.fileprovider",
                                        tempFile
                                    )
                                    cameraTempUri = uri
                                    cameraLauncher.launch(uri)
                                } catch (_: Exception) {
                                    // Fallback to gallery if camera fails
                                    galleryLauncher.launch("image/*")
                                }
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Take Photo", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { galleryLauncher.launch("image/*") },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Gallery", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Customer ID (Auto-filled from NFC tag)
                    OutlinedTextField(
                        value = customerIdInput,
                        onValueChange = { customerIdInput = it },
                        label = { Text("Customer ID (From NFC Tag)") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = GasSevaNavy)
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GasSevaNavy,
                            unfocusedBorderColor = CardBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Customer Full Name
                    OutlinedTextField(
                        value = fullNameInput,
                        onValueChange = {
                            fullNameInput = it
                            errorMessage = null
                        },
                        label = { Text("Customer Full Name") },
                        placeholder = { Text("e.g. Ramesh Chandra") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GasSevaNavy,
                            unfocusedBorderColor = CardBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ErrorRedBg,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = errorMessage!!,
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

                    // Save Customer Button
                    Button(
                        onClick = {
                            if (customerIdInput.trim().isEmpty()) {
                                errorMessage = "Customer ID cannot be empty"
                            } else if (fullNameInput.trim().isEmpty()) {
                                errorMessage = "Please enter the customer's full name"
                            } else {
                                errorMessage = null
                                onSaveCustomer(
                                    customerIdInput.trim(),
                                    fullNameInput.trim(),
                                    selectedPhotoUri?.toString().orEmpty(),
                                    scannedTagValue
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GasSevaGreen,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Save Customer",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
