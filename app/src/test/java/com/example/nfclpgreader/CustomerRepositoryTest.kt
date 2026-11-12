package com.example.nfclpgreader

import com.example.nfclpgreader.data.repository.LocalCustomerRepositoryImpl
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CustomerRepositoryTest {

    private val repository = LocalCustomerRepositoryImpl()

    @Test
    fun testGetExistingCustomer() = runBlocking {
        val result = repository.getCustomerById("CUS001")
        assertTrue(result.isSuccess)

        val customer = result.getOrNull()
        assertEquals("CUS001", customer?.customerId)
        assertEquals("Ravi Kumar", customer?.fullName)
    }

    @Test
    fun testGetExistingCustomerCaseInsensitive() = runBlocking {
        val result = repository.getCustomerById("cus002")
        assertTrue(result.isSuccess)

        val customer = result.getOrNull()
        assertEquals("CUS002", customer?.customerId)
        assertEquals("Priya Sharma", customer?.fullName)
    }

    @Test
    fun testGetNonExistentCustomer() = runBlocking {
        val result = repository.getCustomerById("CUS999")
        assertTrue(result.isFailure)
    }
}
