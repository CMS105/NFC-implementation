package com.example.nfclpgreader.nfc

import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable

/**
 * Utility for formatting and writing NDEF records to NTAG213 / NTAG215 passbook tags.
 */
object NfcWriter {

    /**
     * Formats NDEF Text Record for GasSeva Passbook:
     * LPGPASSBOOK
     * ID:CUS001
     * NAME:M S CHIRAG
     * CODE:YHWP84PENQ
     */
    fun createLpgPassbookNdefMessage(
        customerId: String,
        customerName: String,
        uniqueCode: String
    ): NdefMessage {
        val payloadText = """
            LPGPASSBOOK
            ID:${customerId.trim().uppercase()}
            NAME:${customerName.trim()}
            CODE:${uniqueCode.trim().uppercase()}
        """.trimIndent()

        val textRecord = NdefRecord.createTextRecord("en", payloadText)
        return NdefMessage(arrayOf(textRecord))
    }

    /**
     * Writes NDEF message to physical tag and reads it back to verify.
     */
    fun writeTag(tag: Tag, message: NdefMessage): Result<Unit> {
        val ndef = Ndef.get(tag)

        if (ndef != null) {
            return try {
                ndef.connect()
                if (!ndef.isWritable) {
                    ndef.close()
                    return Result.failure(IllegalStateException("NFC tag is read-only or locked."))
                }

                val size = message.toByteArray().size
                if (ndef.maxSize < size) {
                    ndef.close()
                    return Result.failure(IllegalStateException("NFC tag capacity (${ndef.maxSize} bytes) is smaller than payload ($size bytes)."))
                }

                ndef.writeNdefMessage(message)

                // Read back and verify written content
                val readBackMessage = ndef.ndefMessage
                ndef.close()

                if (readBackMessage != null && readBackMessage.records.isNotEmpty()) {
                    Result.success(Unit)
                } else {
                    Result.failure(IllegalStateException("Verification failed: Tag read-back returned empty NDEF message."))
                }
            } catch (e: Exception) {
                Result.failure(IllegalStateException("Failed to write NFC tag: ${e.localizedMessage ?: "Unknown write error"}"))
            }
        } else {
            // Unformatted tag -> Try formatting with NdefFormatable
            val formatable = NdefFormatable.get(tag)
                ?: return Result.failure(IllegalStateException("Tag does not support NDEF formatting."))

            return try {
                formatable.connect()
                formatable.format(message)
                formatable.close()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(IllegalStateException("Failed to format NFC tag: ${e.localizedMessage ?: "Unknown format error"}"))
            }
        }
    }
}
