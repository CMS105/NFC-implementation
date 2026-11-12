package com.example.nfclpgreader.data.repository

import com.example.nfclpgreader.data.SampleCustomerData
import com.example.nfclpgreader.data.model.Customer
import kotlinx.coroutines.delay

/**
 * Local mock implementation of CustomerRepository.
 * Keeps sample customer data isolated so a FastAPI / REST API implementation
 * can be plugged in easily without altering the UI or ViewModel.
 */
class LocalCustomerRepositoryImpl : CustomerRepository {

    override suspend fun getCustomerById(customerId: String): Result<Customer> {
        // Simulate short asynchronous network or database lookup delay
        delay(200)

        val sanitizedId = customerId.trim().uppercase()
        val customer = SampleCustomerData.customers.find { 
            it.customerId.uppercase() == sanitizedId 
        }

        return if (customer != null) {
            Result.success(customer)
        } else {
            Result.failure(NoSuchElementException("Customer ID '$sanitizedId' not found in database."))
        }
    }
}
