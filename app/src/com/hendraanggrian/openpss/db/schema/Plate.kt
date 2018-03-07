package com.hendraanggrian.openpss.db.schema

import com.hendraanggrian.openpss.db.Named
import com.hendraanggrian.openpss.db.NamedDocumentSchema
import kotlinx.nosql.Id
import kotlinx.nosql.double

object Plates : NamedDocumentSchema<Plate>("plate", Plate::class) {
    val price = double("price")
}

data class Plate(
    override var name: String,
    var price: Double
) : Named<Plates> {
    override lateinit var id: Id<String, Plates>
}