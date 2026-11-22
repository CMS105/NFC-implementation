package com.example.nfclpgreader

import android.content.Intent
import android.nfc.NfcAdapter
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.nfclpgreader.nfc.NfcManager
import com.example.nfclpgreader.nfc.NfcStatus
import com.example.nfclpgreader.ui.components.ScanPhase
import com.example.nfclpgreader.ui.screens.CustomerNotFoundScreen
import com.example.nfclpgreader.ui.screens.CustomerProfileScreen
import com.example.nfclpgreader.ui.screens.HomeScreen
import com.example.nfclpgreader.ui.screens.RegisterCustomerScreen
import com.example.nfclpgreader.ui.screens.ScanFailedScreen
import com.example.nfclpgreader.ui.screens.ScanScreen
import com.example.nfclpgreader.ui.screens.SearchCustomerScreen
import com.example.nfclpgreader.ui.theme.BackgroundWhite
import com.example.nfclpgreader.ui.theme.NFCLPGreaderTheme
import com.example.nfclpgreader.ui.viewmodel.AppScreen
import com.example.nfclpgreader.ui.viewmodel.MainViewModel
import com.example.nfclpgreader.ui.viewmodel.ScanUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var nfcManager: NfcManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        nfcManager = NfcManager(this)

        val action = intent?.action
        if (isNfcIntent(action)) {
            viewModel.navigateTo(AppScreen.ScanNfc)
            viewModel.processNfcIntent(intent)
        }

        setContent {
            NFCLPGreaderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BackgroundWhite
                ) {
                    GasSevaApp(
                        activity = this,
                        nfcManager = nfcManager,
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

        // Always enable Reader Mode & Foreground Dispatch when activity is in foreground
        enableNfcReaderMode()
    }

    override fun onPause() {
        super.onPause()
        disableNfcReaderMode()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (isNfcIntent(intent.action)) {
            viewModel.navigateTo(AppScreen.ScanNfc)
            viewModel.processNfcIntent(intent)
        }
    }

    fun enableNfcReaderMode() {
        nfcManager.enableReaderMode(this) { tag ->
            runOnUiThread {
                viewModel.navigateTo(AppScreen.ScanNfc)
                viewModel.processNfcTag(tag)
            }
        }
        nfcManager.enableForegroundDispatch(this)
    }

    fun disableNfcReaderMode() {
        nfcManager.disableReaderMode(this)
        nfcManager.disableForegroundDispatch(this)
    }

    private fun isNfcIntent(action: String?): Boolean {
        return action == NfcAdapter.ACTION_NDEF_DISCOVERED ||
               action == NfcAdapter.ACTION_TECH_DISCOVERED ||
               action == NfcAdapter.ACTION_TAG_DISCOVERED
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
    activity: MainActivity,
    nfcManager: NfcManager,
    viewModel: MainViewModel,
    onOpenNfcSettings: () -> Unit
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val searchError by viewModel.searchError.collectAsState()

    var celebrated by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        activity.enableNfcReaderMode()
        onDispose {
            activity.disableNfcReaderMode()
        }
    }

    LaunchedEffect(uiState) {
        celebrated = false
    }

    Crossfade(targetState = currentScreen, label = "app_screen_transition") { screen ->
        when (screen) {
            is AppScreen.Home -> {
                HomeScreen(
                    onScanPassbookClick = {
                        viewModel.navigateTo(AppScreen.ScanNfc)
                    },
                    onManualSearchClick = {
                        viewModel.navigateTo(AppScreen.SearchCustomer)
                    },
                    onSimulateScanSuccess = {
                        viewModel.navigateTo(AppScreen.ScanNfc)
                        viewModel.processRawInput("LPGPASSBOOK\nID:CUS001\nNAME:M S CHIRAG\nCODE:YHWP84PENQ")
                    },
                    onSimulateScanFailed = {
                        viewModel.navigateTo(AppScreen.ScanNfc)
                        viewModel.simulateError()
                    }
                )
            }
            is AppScreen.ScanNfc -> {
                val showProfile = uiState is ScanUiState.Success && celebrated
                if (showProfile && uiState is ScanUiState.Success) {
                    CustomerProfileScreen(
                        customer = (uiState as ScanUiState.Success).customer,
                        onBackToScan = {
                            celebrated = false
                            viewModel.navigateTo(AppScreen.Home)
                        }
                    )
                } else {
                    when (val state = uiState) {
                        is ScanUiState.Idle -> {
                            ScanScreen(
                                nfcStatus = state.nfcStatus,
                                scanPhase = ScanPhase.Idle,
                                onOpenNfcSettings = onOpenNfcSettings,
                                onSimulateScan = { rawInput -> viewModel.processRawInput(rawInput) }
                            )
                        }
                        is ScanUiState.Reading -> {
                            ScanScreen(
                                nfcStatus = NfcStatus.Ready,
                                scanPhase = ScanPhase.Reading,
                                onOpenNfcSettings = onOpenNfcSettings,
                                onSimulateScan = { rawInput -> viewModel.processRawInput(rawInput) }
                            )
                        }
                        is ScanUiState.Success -> {
                            ScanScreen(
                                nfcStatus = NfcStatus.Ready,
                                scanPhase = ScanPhase.Success,
                                successCustomerId = state.customer.customerId,
                                onOpenNfcSettings = onOpenNfcSettings,
                                onSimulateScan = { rawInput -> viewModel.processRawInput(rawInput) },
                                onResolved = { p ->
                                    if (p == ScanPhase.Success) {
                                        scope.launch {
                                            delay(900)
                                            celebrated = true
                                        }
                                    }
                                }
                            )
                        }
                        is ScanUiState.NewTag -> {
                            // Handled by AppScreen.CustomerNotFound
                        }
                        is ScanUiState.Error -> {
                            ScanFailedScreen(
                                errorMessage = state.message,
                                onTryAgain = { viewModel.navigateTo(AppScreen.Home) },
                                onManualLookup = { viewModel.navigateTo(AppScreen.SearchCustomer) }
                            )
                        }
                    }
                }
            }
            is AppScreen.CustomerNotFound -> {
                CustomerNotFoundScreen(
                    customerId = screen.customerId,
                    onRegisterNewCustomer = {
                        viewModel.navigateTo(
                            AppScreen.RegisterCustomer(
                                scannedTagValue = screen.tagValue,
                                suggestedCustomerId = screen.customerId,
                                suggestedCustomerName = screen.customerName
                            )
                        )
                    },
                    onScanAnotherPassbook = {
                        viewModel.navigateTo(AppScreen.ScanNfc)
                    }
                )
            }
            is AppScreen.RegisterCustomer -> {
                RegisterCustomerScreen(
                    scannedTagValue = screen.scannedTagValue,
                    suggestedCustomerId = screen.suggestedCustomerId,
                    suggestedCustomerName = screen.suggestedCustomerName,
                    onSaveCustomer = { id, name, photoUri, tagVal ->
                        viewModel.registerCustomer(id, name, photoUri, tagVal)
                    },
                    onBack = { viewModel.navigateTo(AppScreen.Home) }
                )
            }
            is AppScreen.SearchCustomer -> {
                SearchCustomerScreen(
                    onSearch = { customerId -> viewModel.searchCustomerById(customerId) },
                    onBack = { viewModel.navigateTo(AppScreen.Home) },
                    searchError = searchError
                )
            }
            is AppScreen.CustomerDetails -> {
                CustomerProfileScreen(
                    customer = screen.customer,
                    onBackToScan = { viewModel.navigateTo(AppScreen.Home) }
                )
            }
        }
    }
}
