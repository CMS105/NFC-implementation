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
 * Structured container for extracted NDEF data from NTAG213 / NTAG215 passbook tags.
 */
data class ParsedNfcData(
    val customerId: String,
    val customerName: String? = null,
    val uniqueNfcCode: String? = null,
    val rawTagValue: String
)

/**
 * Watermark structure for optional GASSEVA_ORIGIN NDEF records.
 */
data class GassevaWatermarkData(
    val creatorName: String,
    val creatorId: String,
    val projectName: String,
    val copyrightYear: String
)

/**
 * Utility for parsing NDEF formatted 13.56 MHz NFC tags (e.g. NTAG213, NTAG215).
 * Parses NDEF text records (LPGPASSBOOK, ID:CUS..., NAME:..., CODE:...) and NDEF URI records.
 */
object NfcParser {

    /**
     * Extracts structured NDEF data from an incoming Intent.
     */
    @Suppress("DEPRECATION")
    fun parseIntentDetailed(intent: Intent): Result<ParsedNfcData> {
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
                return parseNdefMessageDetailed(ndefMessage)
            }

            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            if (tag != null) {
                return parseTagDetailed(tag)
            }

            return Result.failure(IllegalStateException("NFC tag contains no readable NDEF data"))
        }

        return Result.failure(IllegalArgumentException("Intent action is not an NFC discovery action"))
    }

    /**
     * Extracts a customer ID string from an incoming Intent.
     */
    fun extractCustomerIdFromIntent(intent: Intent): Result<String> {
        return parseIntentDetailed(intent).map { it.customerId }
    }

    /**
     * Parses a raw NFC Tag object into structured NDEF data.
     */
    fun parseTagDetailed(tag: Tag): Result<ParsedNfcData> {
        val ndef = Ndef.get(tag) ?: return Result.failure(IllegalStateException("Tag is not NDEF formatted or supported"))

        return try {
            ndef.connect()
            val ndefMessage = ndef.ndefMessage
            ndef.close()

            if (ndefMessage == null || ndefMessage.records.isEmpty()) {
                Result.failure(IllegalStateException("NFC tag is empty or unformatted"))
            } else {
                parseNdefMessageDetailed(ndefMessage)
            }
        } catch (e: Exception) {
            Result.failure(IllegalStateException("Failed to read NFC tag: ${e.localizedMessage ?: "Unknown error"}"))
        }
    }

    /**
     * Parses a raw NFC Tag object into Customer ID string.
     */
    fun parseTag(tag: Tag): Result<String> {
        return parseTagDetailed(tag).map { it.customerId }
    }

    /**
     * Parses an NdefMessage into structured ParsedNfcData.
     */
    fun parseNdefMessageDetailed(message: NdefMessage): Result<ParsedNfcData> {
        val records = message.records
        if (records.isEmpty()) {
            return Result.failure(IllegalStateException("Tag contains no NDEF records"))
        }

        for (record in records) {
            // Check NDEF Text Record format: LPGPASSBOOK \n ID:CUS001 \n NAME:M S CHIRAG \n CODE:YHWP84PENQ
            val textPayload = parseTextRecord(record)
            if (textPayload != null) {
                val parsed = parseLpgPassbookTextRecord(textPayload)
                if (parsed != null) {
                    return Result.success(parsed)
                }

                val extractedId = extractCustomerIdFromString(textPayload)
                if (extractedId != null) {
                    return Result.success(ParsedNfcData(customerId = extractedId, rawTagValue = textPayload))
                }
            }

            // Check NDEF URI Record format: https://example.com/customer/CUS001
            val uri = record.toUri()
            if (uri != null) {
                val extractedId = extractCustomerIdFromUri(uri)
                if (extractedId != null) {
                    return Result.success(ParsedNfcData(customerId = extractedId, rawTagValue = uri.toString()))
                }
            }
        }

        return Result.failure(IllegalArgumentException("Tag data is invalid or unrecognized passbook format"))
    }

    /**
     * Parses an NdefMessage into Customer ID string.
     */
    fun parseNdefMessage(message: NdefMessage): Result<String> {
        return parseNdefMessageDetailed(message).map { it.customerId }
    }

    /**
     * Parses NDEF text block with LPGPASSBOOK format:
     * LPGPASSBOOK
     * ID:CUS001
     * NAME:M S CHIRAG
     * CODE:YHWP84PENQ
     */
    fun parseLpgPassbookTextRecord(textBlock: String): ParsedNfcData? {
        val cleaned = textBlock.trim()
        if (cleaned.isEmpty()) return null

        val idRegex = Regex("(?i)\\bID:\\s*([A-Za-z0-9_-]+)")
        val nameRegex = Regex("(?i)\\bNAME:\\s*(.+)")
        val codeRegex = Regex("(?i)\\bCODE:\\s*([A-Za-z0-9_-]+)")

        val idMatch = idRegex.find(cleaned)
        val nameMatch = nameRegex.find(cleaned)
        val codeMatch = codeRegex.find(cleaned)

        if (idMatch != null) {
            val extractedId = idMatch.groupValues[1].uppercase()
            val extractedName = nameMatch?.groupValues?.get(1)?.trim()
            val extractedCode = codeMatch?.groupValues?.get(1)?.trim() // Preserves exact casing from tag for strict verification

            return ParsedNfcData(
                customerId = extractedId,
                customerName = if (!extractedName.isNullOrEmpty()) extractedName else null,
                uniqueNfcCode = if (!extractedCode.isNullOrEmpty()) extractedCode else null,
                rawTagValue = cleaned
            )
        }

        return null
    }

    /**
     * Parses optional GASSEVA_ORIGIN NDEF record watermark:
     * GASSEVA_ORIGIN
     * CREATOR:M S CHIRAG
     * CREATOR_ID:CMS105
     * PROJECT:GasSeva
     * COPYRIGHT:2026
     */
    fun parseGassevaOriginRecord(textBlock: String): GassevaWatermarkData? {
        val cleaned = textBlock.trim()
        if (!cleaned.contains("GASSEVA_ORIGIN", ignoreCase = true)) return null

        val creatorMatch = Regex("(?i)\\bCREATOR:\\s*(.+)").find(cleaned)
        val creatorIdMatch = Regex("(?i)\\bCREATOR_ID:\\s*([A-Za-z0-9_-]+)").find(cleaned)
        val projectMatch = Regex("(?i)\\bPROJECT:\\s*(.+)").find(cleaned)
        val copyrightMatch = Regex("(?i)\\bCOPYRIGHT:\\s*(.+)").find(cleaned)

        return if (creatorMatch != null && creatorIdMatch != null) {
            GassevaWatermarkData(
                creatorName = creatorMatch.groupValues[1].trim(),
                creatorId = creatorIdMatch.groupValues[1].trim().uppercase(),
                projectName = projectMatch?.groupValues?.get(1)?.trim() ?: "GasSeva",
                copyrightYear = copyrightMatch?.groupValues?.get(1)?.trim() ?: "2026"
            )
        } else {
            null
        }
    }

    /**
     * Extracts customer ID from Uri (e.g. https://example.com/customer/CUS001 -> CUS001).
     */
    fun extractCustomerIdFromUri(uri: Uri): String? {
        if (uri.path == null) {
            return extractCustomerIdFromString(uri.toString())
        }

        val segments = uri.pathSegments
        if (segments.isNotEmpty()) {
            val lastSegment = segments.last()
            val extracted = extractCustomerIdFromString(lastSegment)
            if (extracted != null) return extracted
        }

        val idParam = uri.getQueryParameter("id") ?: uri.getQueryParameter("customerId")
        if (idParam != null) {
            val extracted = extractCustomerIdFromString(idParam)
            if (extracted != null) return extracted
        }

        return extractCustomerIdFromString(uri.toString())
    }

    /**
     * Extract Customer ID using regex pattern or direct string parsing.
     * Matches patterns like CUS001, CUS12345, or alphanumeric IDs.
     */
    fun extractCustomerIdFromString(raw: String): String? {
        val cleaned = raw.trim()
        if (cleaned.isEmpty()) return null

        // Check ID:CUS... format first
        val idFormatMatch = Regex("(?i)\\bID:\\s*([A-Za-z0-9_-]+)").find(cleaned)
        if (idFormatMatch != null) {
            return idFormatMatch.groupValues[1].uppercase()
        }

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
