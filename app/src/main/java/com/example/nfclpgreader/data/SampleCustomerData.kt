package com.example.nfclpgreader.data

import com.example.nfclpgreader.R
import com.example.nfclpgreader.data.model.Customer

/**
 * Initial sample customer database containing verified LPG customers matching supplied CSV records.
 */
object SampleCustomerData {
    val customers = listOf(
        Customer(
            customerId = "CUS001",
            fullName = "M S CHIRAG",
            photoResId = R.drawable.photo_cus001,
            uniqueNfcCode = "YHWP84PENQ"
        ),
        Customer(
            customerId = "CUS002",
            fullName = "DIGANTH",
            photoResId = R.drawable.photo_cus002,
            uniqueNfcCode = "M4UP8H5PE8"
        ),
        Customer(
            customerId = "CUS003",
            fullName = "ISIRI",
            photoResId = 0,
            uniqueNfcCode = "XGE5SWAEM4"
        ),
        Customer(
            customerId = "CUS004",
            fullName = "SHARMA",
            photoResId = R.drawable.photo_cus004,
            uniqueNfcCode = "MAUCXTJHHH"
        )
    )
}
