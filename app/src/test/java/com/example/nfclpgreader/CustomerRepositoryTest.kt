package com.example.nfclpgreader

import com.example.nfclpgreader.data.repository.LocalCustomerRepositoryImpl
import com.example.nfclpgreader.nfc.NfcParser
import com.example.nfclpgreader.nfc.UniqueCodeGenerator
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CustomerRepositoryTest {

    private val repository = LocalCustomerRepositoryImpl()

    @Test
    fun testGetExistingCustomerCorrectIdAndCode() = runBlocking {
        val result = repository.getCustomerById("CUS001")
        assertTrue(result.isSuccess)

        val customer = result.getOrNull()
        assertEquals("CUS001", customer?.customerId)
        assertEquals("M S CHIRAG", customer?.fullName)
        assertEquals("YHWP84PENQ", customer?.uniqueNfcCode)
    }

    @Test
    fun testGetExistingCustomerCaseInsensitiveLookup() = runBlocking {
        val result = repository.getCustomerById("cus002")
        assertTrue(result.isSuccess)

        val customer = result.getOrNull()
        assertEquals("CUS002", customer?.customerId)
        assertEquals("DIGANTH", customer?.fullName)
        assertEquals("M4UP8H5PE8", customer?.uniqueNfcCode)
    }

    @Test
    fun testGetNonExistentCustomerUnknownId() = runBlocking {
        val result = repository.getCustomerById("CUS999")
        assertTrue(result.isFailure)
    }

    @Test
    fun testCodeMatchingAndMismatchRejection() = runBlocking {
        val customer = repository.getCustomerById("CUS001").getOrNull()
        assertNotNull(customer)

        val validCode = "YHWP84PENQ"
        val invalidCode = "WRONGCODE1"

        assertTrue(validCode.equals(customer?.uniqueNfcCode, ignoreCase = true))
        assertTrue(!invalidCode.equals(customer?.uniqueNfcCode, ignoreCase = true))
    }

    @Test
    fun testLegacyTagMissingCodeExtraction() {
        val legacyText = """
            LPGPASSBOOK
            ID:CUS002
            NAME:DIGANTH
        """.trimIndent()

        val parsed = NfcParser.parseLpgPassbookTextRecord(legacyText)
        assertNotNull(parsed)
        assertEquals("CUS002", parsed?.customerId)
        assertNull(parsed?.uniqueNfcCode)
    }

    @Test
    fun testMalformedPayloadRejection() {
        val malformed = "CORRUPTED_PAYLOAD_WITHOUT_PASSBOOK_OR_ID"
        val parsed = NfcParser.parseLpgPassbookTextRecord(malformed)
        assertNull(parsed)
    }

    @Test
    fun testUniqueCodeGeneratorLengthAndUniqueness() {
        val code = UniqueCodeGenerator.generateCode()
        assertNotNull(code)
        assertEquals(10, code.length)

        val set = mutableSetOf<String>()
        for (i in 0 until 100) {
            val generated = UniqueCodeGenerator.generateCode()
            assertEquals(10, generated.length)
            set.add(generated)
        }
        assertTrue(set.size > 90)
    }
}
