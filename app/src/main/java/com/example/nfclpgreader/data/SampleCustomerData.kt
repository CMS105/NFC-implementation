package com.example.nfclpgreader.data

import com.example.nfclpgreader.R
import com.example.nfclpgreader.data.model.Customer

/**
 * Initial sample customer database containing verified LPG customers.
 */
object SampleCustomerData {
    val customers = listOf(
        Customer(
            customerId = "CUS001",
            fullName = "Ravi Kumar",
            photoResId = R.drawable.ic_avatar_ravi
        ),
        Customer(
            customerId = "CUS002",
            fullName = "Priya Sharma",
            photoResId = R.drawable.ic_avatar_priya
        ),
        Customer(
            customerId = "CUS003",
            fullName = "Amitabh Verma",
            photoResId = R.drawable.ic_avatar_amitabh
        ),
        Customer(
            customerId = "CUS004",
            fullName = "Sunita Patel",
            photoResId = R.drawable.ic_avatar_sunita
        ),
        Customer(
            customerId = "CUS005",
            fullName = "Rajesh Nair",
            photoResId = R.drawable.ic_avatar_rajesh
        )
    )
}
