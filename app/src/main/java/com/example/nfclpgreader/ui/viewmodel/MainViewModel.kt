package com.example.nfclpgreader.ui.viewmodel

import android.app.Application
import android.content.Intent
import android.nfc.Tag
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.nfclpgreader.data.model.Customer
import com.example.nfclpgreader.data.repository.CustomerRepository
import com.example.nfclpgreader.data.repository.RoomCustomerRepositoryImpl
import com.example.nfclpgreader.nfc.NfcParser
import com.example.nfclpgreader.nfc.NfcStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Navigation screen destination states for Gas Seva App.
 */
sealed class AppScreen {
    object Home : AppScreen()
    object ScanNfc : AppScreen()
    data class RegisterCustomer(val scannedTagValue: String, val suggestedCustomerId: String) : AppScreen()
    object SearchCustomer : AppScreen()
    data class CustomerDetails(val customer: Customer) : AppScreen()
}

/**
 * UI State representing the NFC Tag Scanning status.
 */
sealed class ScanUiState {
    data class Idle(val nfcStatus: NfcStatus = NfcStatus.Ready) : ScanUiState()
    object Reading : ScanUiState()
    data class Success(val customer: Customer) : ScanUiState()
    data class NewTag(val tagValue: String, val extractedId: String) : ScanUiState()
    data class Error(val message: String) : ScanUiState()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CustomerRepository = RoomCustomerRepositoryImpl(application.applicationContext)

    private val _currentScreen = MutableStateFlow<AppScreen>(AppScreen.Home)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _uiState = MutableStateFlow<ScanUiState>(ScanUiState.Idle())
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    private val _searchError = MutableStateFlow<String?>(null)
    val searchError: StateFlow<String?> = _searchError.asStateFlow()

    private var currentNfcStatus: NfcStatus = NfcStatus.Ready

    fun navigateTo(screen: AppScreen) {
        _searchError.value = null
        if (screen is AppScreen.ScanNfc) {
            _uiState.value = ScanUiState.Idle(nfcStatus = currentNfcStatus)
        }
        _currentScreen.value = screen
    }

    fun updateNfcStatus(status: NfcStatus) {
        currentNfcStatus = status
        val current = _uiState.value
        if (current is ScanUiState.Idle) {
            _uiState.value = ScanUiState.Idle(nfcStatus = status)
        }
    }

    fun processNfcIntent(intent: Intent) {
        val result = NfcParser.extractCustomerIdFromIntent(intent)
        handleExtractionResult(result, rawInput = intent.dataString.orEmpty())
    }

    fun processNfcTag(tag: Tag) {
        val result = NfcParser.parseTag(tag)
        handleExtractionResult(result, rawInput = tag.id.joinToString("") { "%02X".format(it) })
    }

    fun processRawInput(input: String) {
        val extractedId = NfcParser.extractCustomerIdFromString(input)
        if (extractedId != null) {
            lookupTagOrNavigate(tagValue = input, extractedId = extractedId)
        } else {
            _uiState.value = ScanUiState.Error("Invalid input format. Unable to extract valid Customer ID.")
        }
    }

    private fun handleExtractionResult(result: Result<String>, rawInput: String) {
        result.fold(
            onSuccess = { customerId ->
                lookupTagOrNavigate(tagValue = if (rawInput.isNotEmpty()) rawInput else customerId, extractedId = customerId)
            },
            onFailure = { throwable ->
                val errorMessage = throwable.localizedMessage ?: "Invalid or empty NFC tag scanned."
                _uiState.value = ScanUiState.Error(errorMessage)
            }
        )
    }

    private fun lookupTagOrNavigate(tagValue: String, extractedId: String) {
        _uiState.value = ScanUiState.Reading
        viewModelScope.launch {
            repository.getCustomerByTagValue(tagValue).fold(
                onSuccess = { customer ->
                    _uiState.value = ScanUiState.Success(customer)
                },
                onFailure = {
                    // Try by Customer ID
                    repository.getCustomerById(extractedId).fold(
                        onSuccess = { customer ->
                            _uiState.value = ScanUiState.Success(customer)
                        },
                        onFailure = {
                            // Tag & ID are not registered -> Proceed to New Customer Registration form
                            _uiState.value = ScanUiState.NewTag(tagValue = tagValue, extractedId = extractedId)
                            _currentScreen.value = AppScreen.RegisterCustomer(
                                scannedTagValue = tagValue,
                                suggestedCustomerId = extractedId
                            )
                        }
                    )
                }
            )
        }
    }

    fun searchCustomerById(customerId: String) {
        val sanitizedId = customerId.trim()
        if (sanitizedId.isEmpty()) {
            _searchError.value = "Please enter a Customer ID"
            return
        }

        viewModelScope.launch {
            repository.getCustomerById(sanitizedId).fold(
                onSuccess = { customer ->
                    _searchError.value = null
                    _currentScreen.value = AppScreen.CustomerDetails(customer)
                },
                onFailure = {
                    _searchError.value = "Customer not found"
                }
            )
        }
    }

    fun registerCustomer(
        customerId: String,
        fullName: String,
        photoUriStr: String,
        tagValue: String
    ) {
        viewModelScope.launch {
            repository.registerCustomer(customerId, fullName, photoUriStr, tagValue).fold(
                onSuccess = { newCustomer ->
                    _currentScreen.value = AppScreen.CustomerDetails(newCustomer)
                },
                onFailure = { throwable ->
                    _uiState.value = ScanUiState.Error(throwable.localizedMessage ?: "Registration failed")
                    _currentScreen.value = AppScreen.ScanNfc
                }
            )
        }
    }

    fun resetScan() {
        _uiState.value = ScanUiState.Idle(nfcStatus = currentNfcStatus)
        _currentScreen.value = AppScreen.ScanNfc
    }
}
