package com.example.nfclpgreader

import com.example.nfclpgreader.nfc.NfcParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class NfcParserTest {

    @Test
    fun testExtractLpgPassbookTextRecordWithCode() {
        val textBlock = """
            LPGPASSBOOK
            ID:CUS001
            NAME:M S CHIRAG
            CODE:YHWP84PENQ
        """.trimIndent()

        val parsed = NfcParser.parseLpgPassbookTextRecord(textBlock)
        assertNotNull(parsed)
        assertEquals("CUS001", parsed?.customerId)
        assertEquals("M S CHIRAG", parsed?.customerName)
        assertEquals("YHWP84PENQ", parsed?.uniqueNfcCode)
    }

    @Test
    fun testExtractLpgPassbookTextRecordWithoutCode() {
        val textBlock = """
            LPGPASSBOOK
            ID:CUS002
            NAME:DIGANTH
        """.trimIndent()

        val parsed = NfcParser.parseLpgPassbookTextRecord(textBlock)
        assertNotNull(parsed)
        assertEquals("CUS002", parsed?.customerId)
        assertEquals("DIGANTH", parsed?.customerName)
        assertNull(parsed?.uniqueNfcCode)
    }

    @Test
    fun testExtractCustomerIdFromStandardUrl() {
        val url = "https://example.com/customer/CUS001"
        val customerId = NfcParser.extractCustomerIdFromString(url)
        assertEquals("CUS001", customerId)
    }

    @Test
    fun testExtractCustomerIdFromGasSevaUrl() {
        val url = "https://gasseva.in/passbook/CUS002"
        val customerId = NfcParser.extractCustomerIdFromString(url)
        assertEquals("CUS002", customerId)
    }

    @Test
    fun testExtractCustomerIdFromQueryParam() {
        val url = "https://gasseva.in/passbook?id=CUS003"
        val customerId = NfcParser.extractCustomerIdFromString(url)
        assertEquals("CUS003", customerId)
    }

    @Test
    fun testExtractCustomerIdFromRawString() {
        val raw = "CUS004"
        val customerId = NfcParser.extractCustomerIdFromString(raw)
        assertEquals("CUS004", customerId)
    }

    @Test
    fun testExtractCustomerIdCaseInsensitive() {
        val raw = "cus004"
        val customerId = NfcParser.extractCustomerIdFromString(raw)
        assertEquals("CUS004", customerId)
    }

    @Test
    fun testExtractCustomerIdInvalidString() {
        val raw = "InvalidDataFormatWithNoId"
        val customerId = NfcParser.extractCustomerIdFromString(raw)
        assertNull(customerId)
    }
}
