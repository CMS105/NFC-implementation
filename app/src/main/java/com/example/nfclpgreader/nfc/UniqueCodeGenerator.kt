package com.example.nfclpgreader.nfc

import java.security.SecureRandom

/**
 * Utility for generating secure 10-character alphanumeric codes (e.g. YHWP84PENQ).
 */
object UniqueCodeGenerator {

    private const val CODE_LENGTH = 10
    private const val CHAR_POOL = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789" // Excludes visually ambiguous chars O, 0, I, 1
    private val random = SecureRandom()

    /**
     * Generates a unique 10-character uppercase alphanumeric code.
     */
    fun generateCode(): String {
        val sb = StringBuilder(CODE_LENGTH)
        for (i in 0 until CODE_LENGTH) {
            val randomIndex = random.nextInt(CHAR_POOL.length)
            sb.append(CHAR_POOL[randomIndex])
        }
        return sb.toString()
    }
}
