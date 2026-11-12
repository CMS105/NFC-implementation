package com.example.nfclpgreader.data.model

import androidx.annotation.DrawableRes

/**
 * Customer entity representing an LPG passbook holder.
 * Strictly holds only customer ID, full name, and profile photograph resource ID.
 */
data class Customer(
    val customerId: String,
    val fullName: String,
    @field:DrawableRes val photoResId: Int
)
