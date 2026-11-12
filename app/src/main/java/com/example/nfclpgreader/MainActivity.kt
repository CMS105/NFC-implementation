package com.example.nfclpgreader

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.nfclpgreader.nfc.NfcManager
import com.example.nfclpgreader.nfc.NfcStatus
import com.example.nfclpgreader.ui.components.ErrorCard
import com.example.nfclpgreader.ui.screens.CustomerProfileScreen
import com.example.nfclpgreader.ui.screens.ScanScreen
import com.example.nfclpgreader.ui.theme.BackgroundWhite
import com.example.nfclpgreader.ui.theme.NFCLPGreaderTheme
import com.example.nfclpgreader.ui.viewmodel.MainViewModel
import com.example.nfclpgreader.ui.viewmodel.ScanUiState

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var nfcManager: NfcManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        nfcManager = NfcManager(this)

        // Process NFC intent if app was launched directly by tapping an NFC tag
        if (intent != null) {
            viewModel.processNfcIntent(intent)
        }

        setContent {
            NFCLPGreaderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BackgroundWhite
                ) {
                    GasSevaApp(
                        viewModel = viewModel,
                        onOpenNfcSettings = { openNfcSettings() }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val nfcStatus = nfcManager.getNfcStatus()
        viewModel.updateNfcStatus(nfcStatus)

        // Enable Reader mode for high-performance background reading
        nfcManager.enableReaderMode(this) { tag ->
            runOnUiThread {
                viewModel.processNfcTag(tag)
            }
        }

        // Enable traditional foreground dispatch fallback
        nfcManager.enableForegroundDispatch(this)
    }

    override fun onPause() {
        super.onPause()
        nfcManager.disableReaderMode(this)
        nfcManager.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        viewModel.processNfcIntent(intent)
    }

    private fun openNfcSettings() {
        try {
            val intent = Intent(Settings.ACTION_NFC_SETTINGS)
            startActivity(intent)
        } catch (_: Exception) {
            val intent = Intent(Settings.ACTION_SETTINGS)
            startActivity(intent)
        }
    }
}

@Composable
fun GasSevaApp(
    viewModel: MainViewModel,
    onOpenNfcSettings: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Crossfade(targetState = uiState, label = "screen_transition") { state ->
        when (state) {
            is ScanUiState.Idle -> {
                ScanScreen(
                    nfcStatus = state.nfcStatus,
                    onOpenNfcSettings = onOpenNfcSettings,
                    onSimulateScan = { rawInput ->
                        viewModel.processRawInput(rawInput)
                    }
                )
            }
            is ScanUiState.Reading -> {
                ScanScreen(
                    nfcStatus = NfcStatus.Ready,
                    onOpenNfcSettings = onOpenNfcSettings,
                    onSimulateScan = { rawInput ->
                        viewModel.processRawInput(rawInput)
                    }
                )
            }
            is ScanUiState.Success -> {
                CustomerProfileScreen(
                    customer = state.customer,
                    onBackToScan = { viewModel.resetScan() }
                )
            }
            is ScanUiState.Error -> {
                ErrorCard(
                    errorMessage = state.message,
                    onRetry = { viewModel.resetScan() }
                )
            }
        }
    }
}
