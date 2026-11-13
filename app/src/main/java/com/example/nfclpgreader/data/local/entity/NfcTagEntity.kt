package com.example.nfclpgreader.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room database entity binding an NFC tag NDEF value to a Customer ID.
 */
@Entity(
    tableName = "nfc_tags",
    foreignKeys = [
        ForeignKey(
            entity = CustomerEntity::class,
            parentColumns = ["customerId"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["customerId"])]
)
data class NfcTagEntity(
    @PrimaryKey val tagValue: String,
    val customerId: String
)
