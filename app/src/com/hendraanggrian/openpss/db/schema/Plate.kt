package com.hendraanggrian.openpss.db.schema

import com.hendraanggrian.openpss.db.NamedDocument
import com.hendraanggrian.openpss.db.NamedDocumentSchema
import kotlinx.nosql.Id
import kotlinx.nosql.double

object Plates : NamedDocumentSchema<Plate>("plate", Plate::class) {
    val price = double("price")
}

data class Plate @JvmOverloads constructor(
    override var name: String,
    var price: Double = 0.0
) : NamedDocument<Plates> {
    override lateinit var id: Id<String, Plates>
}