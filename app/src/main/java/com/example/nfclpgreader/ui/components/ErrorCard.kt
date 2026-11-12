package com.example.nfclpgreader.ui.components

import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nfclpgreader.R
import com.example.nfclpgreader.ui.theme.ErrorRed
import com.example.nfclpgreader.ui.theme.ErrorRedBg
import com.example.nfclpgreader.ui.theme.GasSevaNavy

@Composable
fun ErrorCard(
    errorMessage: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        color = ErrorRedBg,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.5.dp, color = ErrorRed.copy(alpha = 0.4f), shape = RoundedCornerShape(16.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = "Error",
                tint = ErrorRed,
                modifier = Modifier.size(56.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Scanning Error",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = ErrorRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color(0xFF334155),
                    textAlign = TextAlign.Center,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GasSevaNavy,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth(0.85f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(id = R.string.btn_retry_scan),
                        style = MaterialTheme.typography.labelLarge.copy(fontSize = 15.sp)
                    )
                }
            }
        }
    }
}
