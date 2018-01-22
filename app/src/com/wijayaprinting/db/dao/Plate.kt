package com.wijayaprinting.db.dao

import com.wijayaprinting.db.Named
import com.wijayaprinting.db.schema.Plates
import kotlinx.nosql.Id

data class Plate(
        override var name: String,
        var price: Double
) : Named<Plates> {
    override lateinit var id: Id<String, Plates>
}