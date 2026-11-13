package com.example.nfclpgreader.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.nfclpgreader.R
import com.example.nfclpgreader.data.local.dao.CustomerDao
import com.example.nfclpgreader.data.local.entity.CustomerEntity
import com.example.nfclpgreader.data.local.entity.NfcTagEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Main Room Database class for Gas Seva NFC Reader.
 */
@Database(
    entities = [CustomerEntity::class, NfcTagEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun customerDao(): CustomerDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gasseva_nfc_database"
                )
                    .addCallback(DatabaseCallback(context.applicationContext))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val context: Context
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialSampleData(database.customerDao())
                    }
                }
            }

            private suspend fun populateInitialSampleData(dao: CustomerDao) {
                val samples = listOf(
                    Pair("CUS001", "Ravi Kumar" to R.drawable.ic_avatar_ravi),
                    Pair("CUS002", "Priya Sharma" to R.drawable.ic_avatar_priya),
                    Pair("CUS003", "Amitabh Verma" to R.drawable.ic_avatar_amitabh),
                    Pair("CUS004", "Sunita Patel" to R.drawable.ic_avatar_sunita),
                    Pair("CUS005", "Rajesh Nair" to R.drawable.ic_avatar_rajesh)
                )

                for ((id, pair) in samples) {
                    val (name, drawableId) = pair
                    val entity = CustomerEntity(
                        customerId = id,
                        customerName = name,
                        photoPath = "res://$drawableId"
                    )
                    val tagEntity = NfcTagEntity(
                        tagValue = "CUS001_TAG_VAL_$id",
                        customerId = id
                    )
                    try {
                        dao.registerCustomerWithTag(entity, tagEntity)
                    } catch (_: Exception) {}
                }
            }
        }
    }
}
