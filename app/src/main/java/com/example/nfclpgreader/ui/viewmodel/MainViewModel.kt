package com.example.nfclpgreader.ui.viewmodel

import android.content.Intent
import android.nfc.Tag
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nfclpgreader.data.model.Customer
import com.example.nfclpgreader.data.repository.CustomerRepository
import com.example.nfclpgreader.data.repository.LocalCustomerRepositoryImpl
import com.example.nfclpgreader.nfc.NfcParser
import com.example.nfclpgreader.nfc.NfcStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * UI State representing the NFC Identity Reader workflow.
 */
sealed class ScanUiState {
    data class Idle(val nfcStatus: NfcStatus = NfcStatus.Ready) : ScanUiState()
    object Reading : ScanUiState()
    data class Success(val customer: Customer) : ScanUiState()
    data class Error(val message: String) : ScanUiState()
}

/**
 * Main ViewModel managing NFC tag scanning, customer lookup, and UI state transitions.
 */
class MainViewModel(
    private val repository: CustomerRepository = LocalCustomerRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow<ScanUiState>(ScanUiState.Idle())
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    private var currentNfcStatus: NfcStatus = NfcStatus.Ready

    fun updateNfcStatus(status: NfcStatus) {
        currentNfcStatus = status
        val current = _uiState.value
        if (current is ScanUiState.Idle) {
            _uiState.value = ScanUiState.Idle(nfcStatus = status)
        }
    }

    /**
     * Processes an NFC intent received via onNewIntent or onCreate.
     */
    fun processNfcIntent(intent: Intent) {
        val result = NfcParser.extractCustomerIdFromIntent(intent)
        handleExtractionResult(result)
    }

    /**
     * Processes an NFC Tag object received via ReaderMode callback.
     */
    fun processNfcTag(tag: Tag) {
        val result = NfcParser.parseTag(tag)
        handleExtractionResult(result)
    }

    /**
     * Process raw ID or URL input directly (e.g. for testing / demo simulation).
     */
    fun processRawInput(input: String) {
        val extractedId = NfcParser.extractCustomerIdFromString(input)
        if (extractedId != null) {
            lookupCustomer(extractedId)
        } else {
            _uiState.value = ScanUiState.Error("Invalid input format. Unable to extract valid Customer ID.")
        }
    }

    private fun handleExtractionResult(result: Result<String>) {
        result.fold(
            onSuccess = { customerId ->
                lookupCustomer(customerId)
            },
            onFailure = { throwable ->
                val errorMessage = throwable.localizedMessage ?: "Invalid or empty NFC tag scanned."
                _uiState.value = ScanUiState.Error(errorMessage)
            }
        )
    }

    /**
     * Validates and queries repository for customer identity.
     */
    private fun lookupCustomer(customerId: String) {
        _uiState.value = ScanUiState.Reading
        viewModelScope.launch {
            repository.getCustomerById(customerId).fold(
                onSuccess = { customer ->
                    _uiState.value = ScanUiState.Success(customer)
                },
                onFailure = {
                    _uiState.value = ScanUiState.Error(
                        "Customer ID '$customerId' not found in Gas Seva database."
                    )
                }
            )
        }
    }

    /**
     * Resets scanner state to scan next passbook.
     */
    fun resetScan() {
        _uiState.value = ScanUiState.Idle(nfcStatus = currentNfcStatus)
    }
}
