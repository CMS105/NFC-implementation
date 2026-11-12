package com.example.nfclpgreader.data.repository

import com.example.nfclpgreader.data.model.Customer

/**
 * Repository interface for fetching customer identity details.
 * Abstracts the data source to allow seamless transition to a remote FastAPI backend.
 */
interface CustomerRepository {
    /**
     * Look up customer identity by Customer ID.
     * @param customerId The sanitized customer ID extracted from the NFC tag.
     * @return Result containing Customer object if found, or Exception if invalid or not found.
     */
    suspend fun getCustomerById(customerId: String): Result<Customer>
}
