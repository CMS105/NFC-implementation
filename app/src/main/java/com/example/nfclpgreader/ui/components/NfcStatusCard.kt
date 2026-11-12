package com.example.nfclpgreader.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nfclpgreader.R
import com.example.nfclpgreader.nfc.NfcStatus
import com.example.nfclpgreader.ui.theme.ErrorRed
import com.example.nfclpgreader.ui.theme.ErrorRedBg
import com.example.nfclpgreader.ui.theme.GasSevaGreen
import com.example.nfclpgreader.ui.theme.GasSevaGreenDark
import com.example.nfclpgreader.ui.theme.GasSevaGreenLight
import com.example.nfclpgreader.ui.theme.WarningAmber
import com.example.nfclpgreader.ui.theme.WarningAmberBg

@Composable
fun NfcStatusCard(
    nfcStatus: NfcStatus,
    onOpenNfcSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (statusTextRes, statusColor, statusBgColor, icon) = when (nfcStatus) {
        is NfcStatus.Ready -> Quadruple(
            R.string.nfc_status_ready,
            GasSevaGreenDark,
            GasSevaGreenLight,
            Icons.Default.CheckCircle
        )
        is NfcStatus.Disabled -> Quadruple(
            R.string.nfc_status_disabled,
            WarningAmber,
            WarningAmberBg,
            Icons.Default.Warning
        )
        is NfcStatus.NotSupported -> Quadruple(
            R.string.nfc_status_unsupported,
            ErrorRed,
            ErrorRedBg,
            Icons.Default.Nfc
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(color = statusBgColor, shape = RoundedCornerShape(12.dp))
            .border(width = 1.dp, color = statusColor.copy(alpha = 0.3f), shape = RoundedCornerShape(12.dp))
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(color = statusColor, shape = CircleShape)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = statusColor,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = stringResource(id = statusTextRes),
                style = MaterialTheme.typography.titleMedium.copy(
                    color = statusColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            )
        }

        if (nfcStatus is NfcStatus.Disabled) {
            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onOpenNfcSettings,
                colors = ButtonDefaults.buttonColors(
                    containerColor = WarningAmber,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.enable_nfc_btn),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
