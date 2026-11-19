package com.example.nfclpgreader

import com.example.nfclpgreader.identity.BuildMode
import com.example.nfclpgreader.identity.CreatorIdentity
import com.example.nfclpgreader.identity.CreatorManifest
import com.example.nfclpgreader.identity.CreatorManifestVerifier
import com.example.nfclpgreader.identity.LicenseManager
import com.example.nfclpgreader.nfc.NfcParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CreatorIdentityTest {

    @Test
    fun testCreatorIdentityConstants() {
        assertEquals("GasSeva", CreatorIdentity.PROJECT_NAME)
        assertEquals("M S Chirag", CreatorIdentity.CREATOR_NAME)
        assertEquals("CMS105", CreatorIdentity.CREATOR_ID)
        assertEquals("© 2026 M S Chirag", CreatorIdentity.COPYRIGHT)
    }

    @Test
    fun testValidCreatorManifestVerifiesSuccessfully() {
        val validManifest = CreatorManifest()
        val isVerified = CreatorManifestVerifier.verifyManifest(validManifest)
        assertTrue("Expected valid manifest to verify successfully", isVerified)
    }

    @Test
    fun testTamperedCreatorNameFailsVerification() {
        val tampered = CreatorManifest(creatorName = "Fake Creator")
        val isVerified = CreatorManifestVerifier.verifyManifest(tampered)
        assertFalse("Expected tampered creator name to fail verification", isVerified)
    }

    @Test
    fun testTamperedCreatorIdFailsVerification() {
        val tampered = CreatorManifest(creatorId = "HACKER101")
        val isVerified = CreatorManifestVerifier.verifyManifest(tampered)
        assertFalse("Expected tampered creator ID to fail verification", isVerified)
    }

    @Test
    fun testTamperedProjectNameFailsVerification() {
        val tampered = CreatorManifest(projectName = "ClonedApp")
        val isVerified = CreatorManifestVerifier.verifyManifest(tampered)
        assertFalse("Expected tampered project name to fail verification", isVerified)
    }

    @Test
    fun testLicenseManagerMode() {
        assertEquals(BuildMode.DEVELOPMENT, LicenseManager.currentBuildMode)
        assertTrue(LicenseManager.isLicensed())
    }

    @Test
    fun testParseGassevaOriginRecordWatermark() {
        val watermarkRecord = """
            GASSEVA_ORIGIN
            CREATOR:M S CHIRAG
            CREATOR_ID:CMS105
            PROJECT:GasSeva
            COPYRIGHT:2026
        """.trimIndent()

        val parsed = NfcParser.parseGassevaOriginRecord(watermarkRecord)
        assertNotNull(parsed)
        assertEquals("M S CHIRAG", parsed?.creatorName)
        assertEquals("CMS105", parsed?.creatorId)
        assertEquals("GasSeva", parsed?.projectName)
        assertEquals("2026", parsed?.copyrightYear)
    }
}
