package com.hendraanggrian.openpss.db.schema

import com.hendraanggrian.openpss.db.NamedDocumentSchema
import com.hendraanggrian.openpss.db.dao.Plate
import kotlinx.nosql.double

object Plates : NamedDocumentSchema<Plate>("plate", Plate::class) {
    val price = double("price")
}