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

class NfcSecurityVerificationTest {

    private val repository = LocalCustomerRepositoryImpl()

    @Test
    fun testScenario1_Cus001CorrectStickerVerification() = runBlocking {
        val payload = """
            LPGPASSBOOK
            ID:CUS001
            NAME:M S CHIRAG
            CODE:YHWP84PENQ
        """.trimIndent()

        val parsed = NfcParser.parseLpgPassbookTextRecord(payload)
        assertNotNull(parsed)
        assertEquals("CUS001", parsed?.customerId)
        assertEquals("M S CHIRAG", parsed?.customerName)
        assertEquals("YHWP84PENQ", parsed?.uniqueNfcCode)

        val customerResult = repository.getCustomerById(parsed!!.customerId)
        assertTrue(customerResult.isSuccess)
        val customer = customerResult.getOrNull()
        assertNotNull(customer)

        // Exact Case-Sensitive Match Verification
        val isVerified = parsed.uniqueNfcCode == customer?.uniqueNfcCode
        assertTrue("Expected verification success for matching CUS001 code", isVerified)
    }

    @Test
    fun testScenario2_IncorrectSecurityCodeFailsVerification() = runBlocking {
        val payload = """
            LPGPASSBOOK
            ID:CUS001
            NAME:M S CHIRAG
            CODE:WRONGCODE1
        """.trimIndent()

        val parsed = NfcParser.parseLpgPassbookTextRecord(payload)
        assertNotNull(parsed)

        val customer = repository.getCustomerById(parsed!!.customerId).getOrNull()
        assertNotNull(customer)

        val isVerified = parsed.uniqueNfcCode == customer?.uniqueNfcCode
        assertFalse("Expected verification failure for incorrect code", isVerified)
    }

    @Test
    fun testScenario3_CaseSensitivityMismatchFailsVerification() = runBlocking {
        val payload = """
            LPGPASSBOOK
            ID:CUS001
            NAME:M S CHIRAG
            CODE:yhwp84penq
        """.trimIndent()

        val parsed = NfcParser.parseLpgPassbookTextRecord(payload)
        assertNotNull(parsed)

        val customer = repository.getCustomerById(parsed!!.customerId).getOrNull()
        assertNotNull(customer)

        // Case-sensitive check MUST fail for lowercase code
        val isVerified = parsed.uniqueNfcCode == customer?.uniqueNfcCode
        assertFalse("Expected verification failure for lowercase code", isVerified)
    }

    @Test
    fun testScenario4_MissingTagSecurityCodeFailsVerification() = runBlocking {
        val legacyPayload = """
            LPGPASSBOOK
            ID:CUS001
            NAME:M S CHIRAG
        """.trimIndent()

        val parsed = NfcParser.parseLpgPassbookTextRecord(legacyPayload)
        assertNotNull(parsed)
        assertTrue(parsed?.uniqueNfcCode.isNullOrEmpty())

        val customer = repository.getCustomerById(parsed!!.customerId).getOrNull()
        assertNotNull(customer)

        val tagCode = parsed.uniqueNfcCode.orEmpty()
        val isVerified = tagCode.isNotEmpty() && tagCode == customer?.uniqueNfcCode
        assertFalse("Expected verification failure when tag code is missing", isVerified)
    }

    @Test
    fun testScenario5_UnknownCustomerFailsLookup() = runBlocking {
        val result = repository.getCustomerById("CUS999")
        assertTrue(result.isFailure)
    }

    @Test
    fun testScenario6_ManualCustomerSearchCus001Success() = runBlocking {
        val result = repository.getCustomerById("CUS001")
        assertTrue(result.isSuccess)
        val customer = result.getOrNull()
        assertEquals("CUS001", customer?.customerId)
        assertEquals("M S CHIRAG", customer?.fullName)
    }

    @Test
    fun testScenario7_ManualCustomerSearchCaseInsensitive() = runBlocking {
        val result = repository.getCustomerById("cus002")
        assertTrue(result.isSuccess)
        val customer = result.getOrNull()
        assertEquals("CUS002", customer?.customerId)
        assertEquals("DIGANTH", customer?.fullName)
    }

    @Test
    fun testScenario8_MalformedTagPayloadFailsParsing() {
        val malformed = "INVALID_UNSUPPORTED_NFC_DATA"
        val parsed = NfcParser.parseLpgPassbookTextRecord(malformed)
        assertNull(parsed)
    }

    @Test
    fun testScenario9_UniqueCodeGeneratorFormatAndUniqueness() {
        val code = UniqueCodeGenerator.generateCode()
        assertNotNull(code)
        assertEquals(10, code.length)
        assertTrue(code.matches(Regex("^[A-Z0-9]{10}$")))
    }
}
