package com.hendraanggrian.openpss.db

interface SimpleTotaled : Totaled {

    val qty: Int

    val price: Double

    override val total: Double get() = qty * price
}