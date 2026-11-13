package com.example.nfclpgreader.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.example.nfclpgreader.R
import com.example.nfclpgreader.data.SampleCustomerData
import com.example.nfclpgreader.data.local.AppDatabaseHelper
import com.example.nfclpgreader.data.local.entity.CustomerEntity
import com.example.nfclpgreader.data.local.entity.NfcTagEntity
import com.example.nfclpgreader.data.model.Customer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * SQLite database implementation of CustomerRepository with persistent local photo file storage.
 */
class RoomCustomerRepositoryImpl(
    private val context: Context,
    private val dbHelper: AppDatabaseHelper = AppDatabaseHelper(context)
) : CustomerRepository {

    override suspend fun getCustomerById(customerId: String): Result<Customer> = withContext(Dispatchers.IO) {
        val sanitizedId = customerId.trim().uppercase()
        val entity = dbHelper.getCustomerById(sanitizedId)

        if (entity != null) {
            Result.success(mapEntityToCustomer(entity))
        } else {
            val sample = SampleCustomerData.customers.find { it.customerId.uppercase() == sanitizedId }
            if (sample != null) {
                Result.success(sample)
            } else {
                Result.failure(NoSuchElementException("Customer ID '$sanitizedId' not found in database."))
            }
        }
    }

    override suspend fun getCustomerByTagValue(tagValue: String): Result<Customer> = withContext(Dispatchers.IO) {
        val sanitizedTag = tagValue.trim()
        val entity = dbHelper.getCustomerByTagValue(sanitizedTag)

        if (entity != null) {
            return@withContext Result.success(mapEntityToCustomer(entity))
        }

        val match = Regex("(?i)\\bCUS\\d+\\b").find(sanitizedTag)?.value?.uppercase()
        if (match != null) {
            val byId = dbHelper.getCustomerById(match)
            if (byId != null) {
                return@withContext Result.success(mapEntityToCustomer(byId))
            }
            val sample = SampleCustomerData.customers.find { it.customerId.uppercase() == match }
            if (sample != null) {
                return@withContext Result.success(sample)
            }
        }

        Result.failure(NoSuchElementException("No customer registered for NFC tag value '$sanitizedTag'."))
    }

    override suspend fun isTagRegistered(tagValue: String): Boolean = withContext(Dispatchers.IO) {
        val sanitizedTag = tagValue.trim()
        val tagEntity = dbHelper.getNfcTagByValue(sanitizedTag)
        if (tagEntity != null) return@withContext true

        val match = Regex("(?i)\\bCUS\\d+\\b").find(sanitizedTag)?.value?.uppercase()
        if (match != null) {
            val existingCustomer = dbHelper.getCustomerById(match)
            if (existingCustomer != null) return@withContext true
            val sample = SampleCustomerData.customers.find { it.customerId.uppercase() == match }
            if (sample != null) return@withContext true
        }

        false
    }

    override suspend fun registerCustomer(
        customerId: String,
        fullName: String,
        photoSourceUri: String,
        tagValue: String
    ): Result<Customer> = withContext(Dispatchers.IO) {
        val sanitizedId = customerId.trim().uppercase()
        val sanitizedName = fullName.trim()
        val sanitizedTag = tagValue.trim()

        if (sanitizedId.isEmpty()) {
            return@withContext Result.failure(IllegalArgumentException("Customer ID cannot be empty."))
        }
        if (sanitizedName.isEmpty()) {
            return@withContext Result.failure(IllegalArgumentException("Customer full name cannot be empty."))
        }

        val existingCustomer = dbHelper.getCustomerById(sanitizedId)
        if (existingCustomer != null) {
            return@withContext Result.failure(IllegalStateException("Customer ID '$sanitizedId' is already registered."))
        }

        val savedPhotoPath = savePhotoToPersistentStorage(context, photoSourceUri, sanitizedId)

        val customerEntity = CustomerEntity(
            customerId = sanitizedId,
            customerName = sanitizedName,
            photoPath = savedPhotoPath
        )

        val nfcTagEntity = NfcTagEntity(
            tagValue = if (sanitizedTag.isNotEmpty()) sanitizedTag else "TAG_$sanitizedId",
            customerId = sanitizedId
        )

        val success = dbHelper.insertCustomerWithTag(customerEntity, nfcTagEntity)
        if (success) {
            Result.success(mapEntityToCustomer(customerEntity))
        } else {
            Result.failure(IllegalStateException("Failed to save customer to local database."))
        }
    }

    private fun mapEntityToCustomer(entity: CustomerEntity): Customer {
        val resDrawableId = getDrawableIdFromPath(entity.photoPath)
        return Customer(
            customerId = entity.customerId,
            fullName = entity.customerName,
            photoResId = resDrawableId,
            photoPath = if (resDrawableId == 0) entity.photoPath else ""
        )
    }

    private fun getDrawableIdFromPath(path: String): Int {
        return when {
            path.contains("avatar_ravi") || path.contains("CUS001") -> R.drawable.ic_avatar_ravi
            path.contains("avatar_priya") || path.contains("CUS002") -> R.drawable.ic_avatar_priya
            path.contains("avatar_amitabh") || path.contains("CUS003") -> R.drawable.ic_avatar_amitabh
            path.contains("avatar_sunita") || path.contains("CUS004") -> R.drawable.ic_avatar_sunita
            path.contains("avatar_rajesh") || path.contains("CUS005") -> R.drawable.ic_avatar_rajesh
            else -> 0
        }
    }

    private fun savePhotoToPersistentStorage(context: Context, sourceUriStr: String, customerId: String): String {
        if (sourceUriStr.isEmpty()) return ""

        return try {
            val photosDir = File(context.filesDir, "customer_photos")
            if (!photosDir.exists()) photosDir.mkdirs()

            val photoFile = File(photosDir, "photo_${customerId}_${System.currentTimeMillis()}.jpg")
            val uri = Uri.parse(sourceUriStr)

            val inputStream = context.contentResolver.openInputStream(uri)
            if (inputStream != null) {
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream.close()

                val outputStream = FileOutputStream(photoFile)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                outputStream.flush()
                outputStream.close()
                photoFile.absolutePath
            } else {
                sourceUriStr
            }
        } catch (_: Exception) {
            sourceUriStr
        }
    }
}
