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
import com.example.nfclpgreader.nfc.ParsedNfcData
import com.example.nfclpgreader.nfc.UniqueCodeGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AppScreen {
    object Home : AppScreen()
    object ScanNfc : AppScreen()
    data class CustomerNotFound(val customerId: String, val customerName: String? = null, val tagValue: String = "") : AppScreen()
    data class RegisterCustomer(val scannedTagValue: String, val suggestedCustomerId: String, val suggestedCustomerName: String? = null) : AppScreen()
    object SearchCustomer : AppScreen()
    data class CustomerDetails(val customer: Customer) : AppScreen()
}

sealed class ScanUiState {
    data class Idle(val nfcStatus: NfcStatus = NfcStatus.Ready) : ScanUiState()
    object Reading : ScanUiState()
    data class Success(val customer: Customer) : ScanUiState()
    data class NewTag(val tagValue: String, val extractedId: String, val customerName: String? = null) : ScanUiState()
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
        val result = NfcParser.parseIntentDetailed(intent)
        result.fold(
            onSuccess = { parsedData ->
                verifyTagData(parsedData)
            },
            onFailure = { throwable ->
                val errorMessage = throwable.localizedMessage ?: "Invalid or unreadable NFC tag scanned."
                _uiState.value = ScanUiState.Error(errorMessage)
            }
        )
    }

    fun processNfcTag(tag: Tag) {
        val result = NfcParser.parseTagDetailed(tag)
        val rawTagId = tag.id.joinToString("") { "%02X".format(it) }
        result.fold(
            onSuccess = { parsedData ->
                val tagVal = if (parsedData.rawTagValue.isNotEmpty()) parsedData.rawTagValue else rawTagId
                verifyTagData(parsedData.copy(rawTagValue = tagVal))
            },
            onFailure = { throwable ->
                val errorMessage = throwable.localizedMessage ?: "Invalid or unreadable NFC tag scanned."
                _uiState.value = ScanUiState.Error(errorMessage)
            }
        )
    }

    fun processRawInput(input: String) {
        val parsed = NfcParser.parseLpgPassbookTextRecord(input)
        if (parsed != null) {
            verifyTagData(parsed)
            return
        }

        val extractedId = NfcParser.extractCustomerIdFromString(input)
        if (extractedId != null) {
            verifyTagData(ParsedNfcData(customerId = extractedId, rawTagValue = input))
        } else {
            _uiState.value = ScanUiState.Error("Invalid NFC Passbook: Unable to read valid customer identity data.")
        }
    }

    fun simulateError() {
        _uiState.value = ScanUiState.Error("NFC Verification Failed: Security code mismatch between passbook tag and database record.")
    }

    private fun verifyTagData(parsedData: ParsedNfcData) {
        _uiState.value = ScanUiState.Reading
        viewModelScope.launch {
            val customerId = parsedData.customerId
            val tagCode = parsedData.uniqueNfcCode.orEmpty()

            repository.getCustomerById(customerId).fold(
                onSuccess = { customer ->
                    // Strict Verification Checks:
                    if (tagCode.isEmpty()) {
                        // Missing unique code on tag -> Reject verification
                        _uiState.value = ScanUiState.Error(
                            "NFC Verification Failed: Security Code Missing on tag. Unable to verify customer authenticity."
                        )
                    } else if (customer.uniqueNfcCode.isEmpty()) {
                        // Missing unique code in DB -> Reject verification
                        _uiState.value = ScanUiState.Error(
                            "NFC Verification Failed: Security Code Missing in database record for ${customer.fullName}."
                        )
                    } else if (tagCode.equals(customer.uniqueNfcCode, ignoreCase = true)) {
                        // Valid Customer & Matching Code
                        _uiState.value = ScanUiState.Success(customer)
                    } else {
                        // Customer ID exists but unique code does NOT match
                        _uiState.value = ScanUiState.Error(
                            "NFC Verification Failed: Tag unique code ($tagCode) does not match database record for ${customer.fullName}."
                        )
                    }
                },
                onFailure = {
                    // Customer ID does not exist in database
                    _uiState.value = ScanUiState.NewTag(
                        tagValue = parsedData.rawTagValue,
                        extractedId = customerId,
                        customerName = parsedData.customerName
                    )
                    _currentScreen.value = AppScreen.CustomerNotFound(
                        customerId = customerId,
                        customerName = parsedData.customerName,
                        tagValue = parsedData.rawTagValue
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
                    _currentScreen.value = AppScreen.CustomerNotFound(
                        customerId = sanitizedId,
                        customerName = null,
                        tagValue = ""
                    )
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
            val uniqueCode = UniqueCodeGenerator.generateCode()
            repository.registerCustomer(customerId, fullName, photoUriStr, tagValue, uniqueCode).fold(
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
