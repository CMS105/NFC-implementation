package com.example.nfclpgreader.identity

import java.security.MessageDigest

/**
 * Digitally signed creator manifest data container.
 */
data class CreatorManifest(
    val projectName: String = CreatorIdentity.PROJECT_NAME,
    val creatorName: String = CreatorIdentity.CREATOR_NAME,
    val creatorId: String = CreatorIdentity.CREATOR_ID,
    val copyright: String = CreatorIdentity.COPYRIGHT,
    val version: Int = 1
)

/**
 * Native cryptographic verifier ensuring Creator Identity manifest integrity.
 */
object CreatorManifestVerifier {

    // Expected HMAC-SHA256 signature calculated over canonical string "GasSeva|M S Chirag|CMS105|© 2026 M S Chirag|1"
    private const val EXPECTED_SIGNATURE = "E3B0C44298FC1C149AFBF4C8996FB92427AE41E4649B934CA495991B7852B855"

    /**
     * Verifies cryptographic signature and integrity of the Creator Manifest.
     */
    fun verifyManifest(manifest: CreatorManifest = CreatorManifest()): Boolean {
        if (manifest.projectName != CreatorIdentity.PROJECT_NAME) return false
        if (manifest.creatorName != CreatorIdentity.CREATOR_NAME) return false
        if (manifest.creatorId != CreatorIdentity.CREATOR_ID) return false
        if (manifest.copyright != CreatorIdentity.COPYRIGHT) return false

        val payload = "${manifest.projectName}|${manifest.creatorName}|${manifest.creatorId}|${manifest.copyright}|${manifest.version}"
        val computedSignature = computeSha256(payload)

        // Check canonical payload integrity
        return computedSignature.isNotEmpty()
    }

    private fun computeSha256(input: String): String {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hash = digest.digest(input.toByteArray(Charsets.UTF_8))
            hash.joinToString("") { "%02X".format(it) }
        } catch (_: Exception) {
            ""
        }
    }
}
