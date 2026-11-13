package com.example.nfclpgreader.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room database entity for customer record.
 */
@Entity(tableName = "customers")
data class CustomerEntity(
    @PrimaryKey val customerId: String,
    val customerName: String,
    val photoPath: String
)
