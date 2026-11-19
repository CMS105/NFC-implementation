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
 * Stores customers with 10-character unique NFC codes and NDEF tag bindings.
 */
class AppDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "gasseva_nfc.db"
        private const val DATABASE_VERSION = 2

        private const val TABLE_CUSTOMERS = "customers"
        private const val COL_CUSTOMER_ID = "customerId"
        private const val COL_CUSTOMER_NAME = "customerName"
        private const val COL_PHOTO_PATH = "photoPath"
        private const val COL_UNIQUE_NFC_CODE = "uniqueNfcCode"

        private const val TABLE_TAGS = "nfc_tags"
        private const val COL_TAG_VALUE = "tagValue"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_CUSTOMERS (
                $COL_CUSTOMER_ID TEXT PRIMARY KEY NOT NULL,
                $COL_CUSTOMER_NAME TEXT NOT NULL,
                $COL_PHOTO_PATH TEXT NOT NULL,
                $COL_UNIQUE_NFC_CODE TEXT UNIQUE
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

        // Prepopulate supplied CSV customer records
        val initialCustomers = listOf(
            Tuple4("CUS001", "M S CHIRAG", "YHWP84PENQ", R.drawable.photo_cus001),
            Tuple4("CUS002", "DIGANTH", "M4UP8H5PE8", R.drawable.ic_avatar_priya),
            Tuple4("CUS003", "ISIRI", "XGE5SWAEM4", R.drawable.ic_avatar_amitabh),
            Tuple4("CUS004", "SHARMA SHREENIDHI", "MAUCXTJHHH", R.drawable.ic_avatar_sunita)
        )

        for ((id, name, code, drawableId) in initialCustomers) {
            val cvCust = ContentValues().apply {
                put(COL_CUSTOMER_ID, id)
                put(COL_CUSTOMER_NAME, name)
                put(COL_PHOTO_PATH, "res://$drawableId")
                put(COL_UNIQUE_NFC_CODE, code)
            }
            db.insertWithOnConflict(TABLE_CUSTOMERS, null, cvCust, SQLiteDatabase.CONFLICT_IGNORE)

            val cvTag = ContentValues().apply {
                put(COL_TAG_VALUE, "TAG_$id")
                put(COL_CUSTOMER_ID, id)
            }
            db.insertWithOnConflict(TABLE_TAGS, null, cvTag, SQLiteDatabase.CONFLICT_IGNORE)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            try {
                db.execSQL("ALTER TABLE $TABLE_CUSTOMERS ADD COLUMN $COL_UNIQUE_NFC_CODE TEXT UNIQUE")
            } catch (_: Exception) {
                // If column exists or error, fall through safely
            }
        }
    }

    fun insertCustomerWithTag(customer: CustomerEntity, tag: NfcTagEntity): Boolean {
        val db = writableDatabase
        db.beginTransaction()
        try {
            val cvCust = ContentValues().apply {
                put(COL_CUSTOMER_ID, customer.customerId)
                put(COL_CUSTOMER_NAME, customer.customerName)
                put(COL_PHOTO_PATH, customer.photoPath)
                put(COL_UNIQUE_NFC_CODE, customer.uniqueNfcCode)
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
            val tagResult = db.insertWithOnConflict(TABLE_TAGS, null, cvTag, SQLiteDatabase.CONFLICT_REPLACE)
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
            arrayOf(COL_CUSTOMER_ID, COL_CUSTOMER_NAME, COL_PHOTO_PATH, COL_UNIQUE_NFC_CODE),
            "$COL_CUSTOMER_ID = ?",
            arrayOf(customerId.trim().uppercase()),
            null, null, null, "1"
        )
        return cursor.use {
            if (it.moveToFirst()) {
                CustomerEntity(
                    customerId = it.getString(0),
                    customerName = it.getString(1),
                    photoPath = it.getString(2),
                    uniqueNfcCode = it.getString(3) ?: ""
                )
            } else {
                null
            }
        }
    }

    fun getCustomerByTagValue(tagValue: String): CustomerEntity? {
        val db = readableDatabase
        val query = """
            SELECT c.$COL_CUSTOMER_ID, c.$COL_CUSTOMER_NAME, c.$COL_PHOTO_PATH, c.$COL_UNIQUE_NFC_CODE 
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
                    photoPath = it.getString(2),
                    uniqueNfcCode = it.getString(3) ?: ""
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

    fun bindTagToCustomer(tagValue: String, customerId: String): Boolean {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_TAG_VALUE, tagValue.trim())
            put(COL_CUSTOMER_ID, customerId.trim().uppercase())
        }
        val result = db.insertWithOnConflict(TABLE_TAGS, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
        return result != -1L
    }
}

private data class Tuple4<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
