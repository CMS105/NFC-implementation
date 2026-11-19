package com.example.nfclpgreader

import com.example.nfclpgreader.data.repository.LocalCustomerRepositoryImpl
import com.example.nfclpgreader.nfc.NfcParser
import com.example.nfclpgreader.nfc.UniqueCodeGenerator
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CustomerRepositoryTest {

    private val repository = LocalCustomerRepositoryImpl()

    @Test
    fun testCorrectIdAndExactMatchingCode() = runBlocking {
        val result = repository.getCustomerById("CUS001")
        assertTrue(result.isSuccess)

        val customer = result.getOrNull()
        assertNotNull(customer)
        assertEquals("CUS001", customer?.customerId)
        assertEquals("M S CHIRAG", customer?.fullName)
        assertEquals("YHWP84PENQ", customer?.uniqueNfcCode)

        val tagCode = "YHWP84PENQ"
        assertTrue(tagCode == customer?.uniqueNfcCode)
    }

    @Test
    fun testCorrectIdButIncorrectCodeRejection() = runBlocking {
        val customer = repository.getCustomerById("CUS001").getOrNull()
        assertNotNull(customer)

        val incorrectCode = "WRONGCODE1"
        assertFalse(incorrectCode == customer?.uniqueNfcCode)
    }

    @Test
    fun testCorrectIdButDifferentCasingRejection() = runBlocking {
        val customer = repository.getCustomerById("CUS001").getOrNull()
        assertNotNull(customer)

        val lowerCaseCode = "yhwp84penq"
        // Case-sensitive check must fail
        assertFalse(lowerCaseCode == customer?.uniqueNfcCode)
    }

    @Test
    fun testCorrectIdButMissingTagCodeRejection() {
        val legacyText = """
            LPGPASSBOOK
            ID:CUS001
            NAME:M S CHIRAG
        """.trimIndent()

        val parsed = NfcParser.parseLpgPassbookTextRecord(legacyText)
        assertNotNull(parsed)
        assertTrue(parsed?.uniqueNfcCode.isNullOrEmpty())
    }

    @Test
    fun testUnknownCustomerIdRejection() = runBlocking {
        val result = repository.getCustomerById("CUS999")
        assertTrue(result.isFailure)
    }

    @Test
    fun testCaseInsensitiveCustomerIdLookup() = runBlocking {
        val result = repository.getCustomerById("cus002")
        assertTrue(result.isSuccess)

        val customer = result.getOrNull()
        assertEquals("CUS002", customer?.customerId)
        assertEquals("DIGANTH", customer?.fullName)
    }

    @Test
    fun testMalformedTagPayloadRejection() {
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
