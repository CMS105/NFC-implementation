package com.example.nfclpgreader.data.repository

import com.example.nfclpgreader.data.model.Customer

/**
 * Repository interface for customer data operations.
 */
interface CustomerRepository {
    /**
     * Search customer by Customer ID.
     */
    suspend fun getCustomerById(customerId: String): Result<Customer>

    /**
     * Look up customer by scanned NFC tag NDEF value.
     */
    suspend fun getCustomerByTagValue(tagValue: String): Result<Customer>

    /**
     * Check whether an NFC tag value is already registered.
     */
    suspend fun isTagRegistered(tagValue: String): Boolean

    /**
     * Register a new customer and bind their NFC tag value.
     * Copies photo to app persistent storage.
     */
    suspend fun registerCustomer(
        customerId: String,
        fullName: String,
        photoSourceUri: String,
        tagValue: String
    ): Result<Customer>
}
