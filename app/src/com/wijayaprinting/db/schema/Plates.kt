package com.wijayaprinting.db.schema

import com.wijayaprinting.db.NamedDocumentSchema
import com.wijayaprinting.db.dao.Plate
import kotlinx.nosql.double

object Plates : NamedDocumentSchema<Plate>("plate", Plate::class) {
    val price = double("price")
}