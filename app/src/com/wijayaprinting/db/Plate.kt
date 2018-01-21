package com.wijayaprinting.db

import kotlinx.nosql.Id
import kotlinx.nosql.double
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.string

object Plates : DocumentSchema<Plate>("plate", Plate::class) {
    val name = string("name")
    val price = double("price")
}

data class Plate @JvmOverloads constructor(
        var name: String,
        var price: Double = 0.0
) {
    lateinit var id: Id<String, Plates>
}