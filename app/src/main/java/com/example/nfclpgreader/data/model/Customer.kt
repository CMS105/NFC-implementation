package com.example.nfclpgreader.data.model

import androidx.annotation.DrawableRes

/**
 * Customer entity representing an LPG passbook holder.
 * Holds only customer ID, full name, and profile photograph resource ID or local file path.
 */
data class Customer(
    val customerId: String,
    val fullName: String,
    @field:DrawableRes val photoResId: Int = 0,
    val photoPath: String = ""
)
