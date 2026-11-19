package com.example.nfclpgreader.identity

/**
 * License activation status and mode configuration for GasSeva.
 */
enum class BuildMode {
    DEVELOPMENT, // Prototype mode - unlocked for offline testing
    PRODUCTION   // Production mode - requires Admin Panel activation
}

/**
 * Architecture preparing GasSeva for future Admin Panel backend license activation.
 */
object LicenseManager {

    val currentBuildMode: BuildMode = BuildMode.DEVELOPMENT

    fun isLicensed(): Boolean {
        return when (currentBuildMode) {
            BuildMode.DEVELOPMENT -> true
            BuildMode.PRODUCTION -> false // Requires activation code from Admin Panel
        }
    }

    fun validateActivationCode(code: String): Boolean {
        if (code.trim().isEmpty()) return false
        // Backend activation endpoint hook
        return code.trim().uppercase().startsWith("GASSEVA-")
    }
}
