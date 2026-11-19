package com.example.nfclpgreader.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nfclpgreader.identity.CreatorIdentity
import com.example.nfclpgreader.identity.CreatorManifestVerifier
import com.example.nfclpgreader.ui.theme.CardBorder
import com.example.nfclpgreader.ui.theme.ChampagneGold
import com.example.nfclpgreader.ui.theme.ElectricCobalt
import com.example.nfclpgreader.ui.theme.GasSevaGreen
import com.example.nfclpgreader.ui.theme.ObsidianContainer
import com.example.nfclpgreader.ui.theme.SurfaceOffWhite
import com.example.nfclpgreader.ui.theme.TextPrimary
import com.example.nfclpgreader.ui.theme.TextSecondary

/**
 * Standard, clean, non-intrusive About GasSeva dialog displaying project attribution and authenticity verification.
 */
@Composable
fun CreatorAboutDialog(
    onDismiss: () -> Unit
) {
    val isVerified = CreatorManifestVerifier.verifyManifest()

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(ElectricCobalt),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.LocalFireDepartment,
                    contentDescription = null,
                    tint = ChampagneGold,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${CreatorIdentity.PROJECT_NAME} v${CreatorIdentity.APP_VERSION}",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "LPG Passbook Identity Terminal",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SurfaceOffWhite,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Original Creator", color = TextSecondary, fontSize = 12.sp)
                            Text(CreatorIdentity.CREATOR_NAME, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Creator ID", color = TextSecondary, fontSize = 12.sp)
                            Text(CreatorIdentity.CREATOR_ID, color = ChampagneGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Copyright", color = TextSecondary, fontSize = 12.sp)
                            Text(CreatorIdentity.COPYRIGHT, color = TextPrimary, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Subtle Authenticity Verification Status Badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = GasSevaGreen.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GasSevaGreen.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = GasSevaGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isVerified) "Authenticity Verified" else "Authenticity Check Failed",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = GasSevaGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = ElectricCobalt),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Close", color = Color.White)
            }
        },
        containerColor = ObsidianContainer,
        shape = RoundedCornerShape(16.dp)
    )
}
