package com.example.nfclpgreader.data.repository

import com.example.nfclpgreader.data.SampleCustomerData
import com.example.nfclpgreader.data.model.Customer
import kotlinx.coroutines.delay

/**
 * Local mock implementation of CustomerRepository.
 */
class LocalCustomerRepositoryImpl : CustomerRepository {

    override suspend fun getCustomerById(customerId: String): Result<Customer> {
        delay(100)
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

    override suspend fun getCustomerByTagValue(tagValue: String): Result<Customer> {
        return getCustomerById(tagValue)
    }

    override suspend fun isTagRegistered(tagValue: String): Boolean {
        return getCustomerByTagValue(tagValue).isSuccess
    }

    override suspend fun registerCustomer(
        customerId: String,
        fullName: String,
        photoSourceUri: String,
        tagValue: String
    ): Result<Customer> {
        val newCustomer = Customer(
            customerId = customerId.trim().uppercase(),
            fullName = fullName.trim(),
            photoPath = photoSourceUri
        )
        return Result.success(newCustomer)
    }
}
