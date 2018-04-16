package com.hendraanggrian.openpss.db

interface SimpleOrder : Order {

    val price: Double

    override val total: Double get() = qty * price
}