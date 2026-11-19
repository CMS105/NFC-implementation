package com.example.nfclpgreader.data.model

import androidx.annotation.DrawableRes

/**
 * Customer entity representing an LPG passbook holder.
 * Holds customer ID, full name, profile photograph resource ID or local file path, and unique 10-character NFC code.
 */
data class Customer(
    val customerId: String,
    val fullName: String,
    @field:DrawableRes val photoResId: Int = 0,
    val photoPath: String = "",
    val uniqueNfcCode: String = ""
)
