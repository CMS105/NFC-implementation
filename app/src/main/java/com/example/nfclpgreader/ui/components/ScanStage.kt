package com.example.nfclpgreader.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/** Ring + status text + sound. Drop this where NfcScanningAnimation is used today. */
@Composable
fun ScanStage(
    phase: ScanPhase,
    modifier: Modifier = Modifier,
    soundEnabled: Boolean = true,
    idleTitle: String = "Tap Customer NFC Passbook",
    idleHint: String = "Hold the back of your phone near the NFC tag on the LPG passbook",
    readingTitle: String = "Reading...",
    successTitle: String = "Card Verified",
    successHint: String = "",
    errorTitle: String = "Couldn't Read Card",
    errorHint: String = "Hold it steady and try again",
    onResolved: (ScanPhase) -> Unit = {},
) {
    val context = LocalContext.current
    val feedback = remember { ScanFeedback(context.applicationContext) }
    DisposableEffect(Unit) { onDispose { feedback.release() } }
    feedback.soundEnabled = soundEnabled

    var ignitePlayed by remember { mutableStateOf(false) }
    LaunchedEffect(phase) {
        if (phase == ScanPhase.Idle) {
            ignitePlayed = false
        } else if (!ignitePlayed) {
            ignitePlayed = true
            feedback.onTagDetected()
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        FlameRingScanner(phase) { p ->
            if (p == ScanPhase.Success) {
                feedback.onSuccess()
            } else if (p == ScanPhase.Error) {
                feedback.onError()
            }
            onResolved(p)
        }

        Spacer(Modifier.height(20.dp))

        Crossfade(phase, label = "scanText") { p ->
            val title = when (p) {
                ScanPhase.Idle -> idleTitle
                ScanPhase.Reading -> readingTitle
                ScanPhase.Success -> successTitle
                ScanPhase.Error -> errorTitle
            }
            val hint = when (p) {
                ScanPhase.Idle -> idleHint
                ScanPhase.Reading -> ""
                ScanPhase.Success -> successHint
                ScanPhase.Error -> errorHint
            }
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .semantics { liveRegion = LiveRegionMode.Polite },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
                if (hint.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = hint,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        fontFamily = if (p == ScanPhase.Success) FontFamily.Monospace else FontFamily.Default,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
