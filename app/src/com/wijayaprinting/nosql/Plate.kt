package com.wijayaprinting.nosql

import kotlinx.nosql.Id
import kotlinx.nosql.double
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.string

object Plates : DocumentSchema<Plate>("plate", Plate::class) {
    val name = string("name")
    val price = double("price")
}

data class Plate(
        var name: String,
        var price: Double
) {
    lateinit var id: Id<String, Plates>
}