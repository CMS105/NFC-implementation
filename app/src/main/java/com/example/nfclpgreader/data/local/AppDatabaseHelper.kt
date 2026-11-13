package com.example.nfclpgreader.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.nfclpgreader.R
import com.example.nfclpgreader.data.local.entity.CustomerEntity
import com.example.nfclpgreader.data.local.entity.NfcTagEntity

/**
 * Lightweight, robust SQLite database helper for Gas Seva NFC Reader.
 * Completely independent of KSP or KAPT compiler plugins, ensuring 100% reliable builds.
 */
class AppDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "gasseva_nfc.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_CUSTOMERS = "customers"
        private const val COL_CUSTOMER_ID = "customerId"
        private const val COL_CUSTOMER_NAME = "customerName"
        private const val COL_PHOTO_PATH = "photoPath"

        private const val TABLE_TAGS = "nfc_tags"
        private const val COL_TAG_VALUE = "tagValue"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_CUSTOMERS (
                $COL_CUSTOMER_ID TEXT PRIMARY KEY NOT NULL,
                $COL_CUSTOMER_NAME TEXT NOT NULL,
                $COL_PHOTO_PATH TEXT NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_TAGS (
                $COL_TAG_VALUE TEXT PRIMARY KEY NOT NULL,
                $COL_CUSTOMER_ID TEXT NOT NULL,
                FOREIGN KEY($COL_CUSTOMER_ID) REFERENCES $TABLE_CUSTOMERS($COL_CUSTOMER_ID) ON DELETE CASCADE
            )
            """.trimIndent()
        )

        // Prepopulate initial sample data
        val samples = listOf(
            Triple("CUS001", "Ravi Kumar", R.drawable.ic_avatar_ravi),
            Triple("CUS002", "Priya Sharma", R.drawable.ic_avatar_priya),
            Triple("CUS003", "Amitabh Verma", R.drawable.ic_avatar_amitabh),
            Triple("CUS004", "Sunita Patel", R.drawable.ic_avatar_sunita),
            Triple("CUS005", "Rajesh Nair", R.drawable.ic_avatar_rajesh)
        )

        for ((id, name, drawableId) in samples) {
            val cvCust = ContentValues().apply {
                put(COL_CUSTOMER_ID, id)
                put(COL_CUSTOMER_NAME, name)
                put(COL_PHOTO_PATH, "res://" + drawableId)
            }
            db.insert(TABLE_CUSTOMERS, null, cvCust)

            val cvTag = ContentValues().apply {
                put(COL_TAG_VALUE, "CUS001_TAG_VAL_$id")
                put(COL_CUSTOMER_ID, id)
            }
            db.insert(TABLE_TAGS, null, cvTag)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TAGS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CUSTOMERS")
        onCreate(db)
    }

    fun insertCustomerWithTag(customer: CustomerEntity, tag: NfcTagEntity): Boolean {
        val db = writableDatabase
        db.beginTransaction()
        try {
            val cvCust = ContentValues().apply {
                put(COL_CUSTOMER_ID, customer.customerId)
                put(COL_CUSTOMER_NAME, customer.customerName)
                put(COL_PHOTO_PATH, customer.photoPath)
            }
            val custResult = db.insertWithOnConflict(TABLE_CUSTOMERS, null, cvCust, SQLiteDatabase.CONFLICT_ABORT)
            if (custResult == -1L) {
                db.endTransaction()
                return false
            }

            val cvTag = ContentValues().apply {
                put(COL_TAG_VALUE, tag.tagValue)
                put(COL_CUSTOMER_ID, tag.customerId)
            }
            val tagResult = db.insertWithOnConflict(TABLE_TAGS, null, cvTag, SQLiteDatabase.CONFLICT_ABORT)
            if (tagResult == -1L) {
                db.endTransaction()
                return false
            }

            db.setTransactionSuccessful()
            return true
        } catch (_: Exception) {
            return false
        } finally {
            db.endTransaction()
        }
    }

    fun getCustomerById(customerId: String): CustomerEntity? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_CUSTOMERS,
            arrayOf(COL_CUSTOMER_ID, COL_CUSTOMER_NAME, COL_PHOTO_PATH),
            "$COL_CUSTOMER_ID = ?",
            arrayOf(customerId.trim().uppercase()),
            null, null, null, "1"
        )
        return cursor.use {
            if (it.moveToFirst()) {
                CustomerEntity(
                    customerId = it.getString(0),
                    customerName = it.getString(1),
                    photoPath = it.getString(2)
                )
            } else {
                null
            }
        }
    }

    fun getCustomerByTagValue(tagValue: String): CustomerEntity? {
        val db = readableDatabase
        val query = """
            SELECT c.$COL_CUSTOMER_ID, c.$COL_CUSTOMER_NAME, c.$COL_PHOTO_PATH 
            FROM $TABLE_CUSTOMERS c 
            INNER JOIN $TABLE_TAGS t ON c.$COL_CUSTOMER_ID = t.$COL_CUSTOMER_ID 
            WHERE t.$COL_TAG_VALUE = ? 
            LIMIT 1
        """
        val cursor = db.rawQuery(query, arrayOf(tagValue.trim()))
        return cursor.use {
            if (it.moveToFirst()) {
                CustomerEntity(
                    customerId = it.getString(0),
                    customerName = it.getString(1),
                    photoPath = it.getString(2)
                )
            } else {
                null
            }
        }
    }

    fun getNfcTagByValue(tagValue: String): NfcTagEntity? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_TAGS,
            arrayOf(COL_TAG_VALUE, COL_CUSTOMER_ID),
            "$COL_TAG_VALUE = ?",
            arrayOf(tagValue.trim()),
            null, null, null, "1"
        )
        return cursor.use {
            if (it.moveToFirst()) {
                NfcTagEntity(
                    tagValue = it.getString(0),
                    customerId = it.getString(1)
                )
            } else {
                null
            }
        }
    }
}
