package com.example.nfclpgreader

import com.example.nfclpgreader.nfc.NfcParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class NfcParserTest {

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
        val raw = "cus005"
        val customerId = NfcParser.extractCustomerIdFromString(raw)
        assertEquals("CUS005", customerId)
    }

    @Test
    fun testExtractCustomerIdInvalidString() {
        val raw = "InvalidDataFormatWithNoId"
        val customerId = NfcParser.extractCustomerIdFromString(raw)
        assertNull(customerId)
    }
}
