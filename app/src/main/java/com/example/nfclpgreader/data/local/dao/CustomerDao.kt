package com.example.nfclpgreader.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.nfclpgreader.data.local.entity.CustomerEntity
import com.example.nfclpgreader.data.local.entity.NfcTagEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Customer and NFC Tag operations in Room DB.
 */
@Dao
interface CustomerDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertCustomer(customer: CustomerEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertNfcTag(tag: NfcTagEntity)

    /**
     * Atomically registers a new customer and binds their NFC tag.
     */
    @Transaction
    suspend fun registerCustomerWithTag(customer: CustomerEntity, tag: NfcTagEntity) {
        insertCustomer(customer)
        insertNfcTag(tag)
    }

    @Query("SELECT * FROM customers WHERE customerId = :customerId LIMIT 1")
    suspend fun getCustomerById(customerId: String): CustomerEntity?

    @Query("SELECT c.* FROM customers c INNER JOIN nfc_tags t ON c.customerId = t.customerId WHERE t.tagValue = :tagValue LIMIT 1")
    suspend fun getCustomerByTagValue(tagValue: String): CustomerEntity?

    @Query("SELECT * FROM nfc_tags WHERE tagValue = :tagValue LIMIT 1")
    suspend fun getNfcTagByValue(tagValue: String): NfcTagEntity?

    @Query("SELECT * FROM customers")
    fun getAllCustomers(): Flow<List<CustomerEntity>>
}
