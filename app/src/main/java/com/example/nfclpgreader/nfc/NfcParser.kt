package com.example.nfclpgreader.nfc

import android.content.Intent
import android.net.Uri
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Build
import android.os.Parcelable
import java.nio.charset.Charset

/**
 * Utility for parsing NDEF formatted 13.56 MHz NFC tags (e.g. NTAG213, NTAG215).
 * Extracts customer IDs from URI records, text records, or tag IDs safely.
 */
object NfcParser {

    /**
     * Extracts a customer ID from an incoming Intent (e.g. NDEF_DISCOVERED or TAG_DISCOVERED).
     */
    @Suppress("DEPRECATION")
    fun extractCustomerIdFromIntent(intent: Intent): Result<String> {
        val action = intent.action ?: return Result.failure(IllegalArgumentException("No NFC action found in intent"))

        if (action == NfcAdapter.ACTION_NDEF_DISCOVERED ||
            action == NfcAdapter.ACTION_TECH_DISCOVERED ||
            action == NfcAdapter.ACTION_TAG_DISCOVERED
        ) {
            val rawMessages: Array<Parcelable>? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES, Parcelable::class.java)
            } else {
                intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            }

            if (rawMessages != null && rawMessages.isNotEmpty()) {
                val ndefMessage = rawMessages[0] as NdefMessage
                return parseNdefMessage(ndefMessage)
            }

            // If no NDEF message in intent extras, attempt reading directly from Tag tech
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            if (tag != null) {
                return parseTag(tag)
            }

            return Result.failure(IllegalStateException("NFC tag contains no readable NDEF data"))
        }

        return Result.failure(IllegalArgumentException("Intent action is not an NFC discovery action"))
    }

    /**
     * Parses a raw NFC Tag object.
     */
    fun parseTag(tag: Tag): Result<String> {
        val ndef = Ndef.get(tag) ?: return Result.failure(IllegalStateException("Tag is not NDEF formatted or supported"))

        return try {
            ndef.connect()
            val ndefMessage = ndef.ndefMessage
            ndef.close()

            if (ndefMessage == null || ndefMessage.records.isEmpty()) {
                Result.failure(IllegalStateException("NFC tag is empty or unformatted"))
            } else {
                parseNdefMessage(ndefMessage)
            }
        } catch (e: Exception) {
            Result.failure(IllegalStateException("Failed to read NFC tag: ${e.localizedMessage ?: "Unknown error"}"))
        }
    }

    /**
     * Parses an NdefMessage to find a Customer ID.
     */
    fun parseNdefMessage(message: NdefMessage): Result<String> {
        val records = message.records
        if (records.isEmpty()) {
            return Result.failure(IllegalStateException("Tag contains no NDEF records"))
        }

        for (record in records) {
            val uri = record.toUri()
            if (uri != null) {
                val extracted = extractCustomerIdFromUri(uri)
                if (extracted != null) {
                    return Result.success(extracted)
                }
            }

            // Fallback: Try reading text payload if record is text
            val textPayload = parseTextRecord(record)
            if (textPayload != null) {
                val extracted = extractCustomerIdFromString(textPayload)
                if (extracted != null) {
                    return Result.success(extracted)
                }
            }
        }

        return Result.failure(IllegalArgumentException("Tag data is invalid or unrecognized customer URL format"))
    }

    /**
     * Extracts customer ID from Uri (e.g. https://example.com/customer/CUS001 -> CUS001).
     */
    fun extractCustomerIdFromUri(uri: Uri): String? {
        if (uri.path == null) {
            return extractCustomerIdFromString(uri.toString())
        }

        // 1. Check path segments for /customer/CUS001 or /passbook/CUS001
        val segments = uri.pathSegments
        if (segments.isNotEmpty()) {
            val lastSegment = segments.last()
            val extracted = extractCustomerIdFromString(lastSegment)
            if (extracted != null) return extracted
        }

        // 2. Check query parameter e.g., ?id=CUS001
        val idParam = uri.getQueryParameter("id") ?: uri.getQueryParameter("customerId")
        if (idParam != null) {
            val extracted = extractCustomerIdFromString(idParam)
            if (extracted != null) return extracted
        }

        // 3. Fallback to full string matching
        return extractCustomerIdFromString(uri.toString())
    }

    /**
     * Extract Customer ID using regex pattern or direct string parsing.
     * Matches patterns like CUS001, CUS12345, or alphanumeric IDs.
     */
    fun extractCustomerIdFromString(raw: String): String? {
        val cleaned = raw.trim()
        if (cleaned.isEmpty()) return null

        // Pattern 1: Matches explicit customer ID format e.g. CUS001, CUS123
        val cusPattern = Regex("(?i)\\bCUS\\d+\\b")
        val match = cusPattern.find(cleaned)
        if (match != null) {
            return match.value.uppercase()
        }

        // Pattern 2: Customer ID in URI path like /customer/(CUS001 or alphanumeric)
        val pathPattern = Regex("(?i)/(?:customer|passbook|user)/([A-Za-z0-9_-]+)")
        val pathMatch = pathPattern.find(cleaned)
        if (pathMatch != null && pathMatch.groupValues.size > 1) {
            return pathMatch.groupValues[1].uppercase()
        }

        // Pattern 3: Direct alphanumeric ID if short string (e.g. CUS001 or 3-15 chars)
        if (cleaned.length in 3..15 && cleaned.matches(Regex("^[A-Za-z0-9_-]+$"))) {
            return cleaned.uppercase()
        }

        return null
    }

    private fun parseTextRecord(record: NdefRecord): String? {
        if (record.tnf != NdefRecord.TNF_WELL_KNOWN) return null
        if (!record.type.contentEquals(NdefRecord.RTD_TEXT)) return null

        return try {
            val payload = record.payload
            if (payload.isEmpty()) return null

            val textEncoding = if ((payload[0].toInt() and 128) == 0) Charset.forName("UTF-8") else Charset.forName("UTF-16")
            val languageCodeLength = payload[0].toInt() and 63
            String(payload, languageCodeLength + 1, payload.size - languageCodeLength - 1, textEncoding)
        } catch (_: Exception) {
            null
        }
    }
}
