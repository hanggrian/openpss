package com.wijayaprinting.db

import kotlinx.nosql.Id
import kotlinx.nosql.double
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.string

object Plates : DocumentSchema<Plate>("plate", Plate::class), NamedColumn<Plates> {
    override val name = string("name")
    val price = double("price")
}

data class Plate(
        override var name: String,
        var price: Double
) : Named, Ided<Plates> {
    override lateinit var id: Id<String, Plates>
}