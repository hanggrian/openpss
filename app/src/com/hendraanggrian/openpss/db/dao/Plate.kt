package com.hendraanggrian.openpss.db.dao

import com.hendraanggrian.openpss.db.Named
import com.hendraanggrian.openpss.db.schema.Plates
import kotlinx.nosql.Id

data class Plate(
    override var name: String,
    var price: Double
) : Named<Plates> {
    override lateinit var id: Id<String, Plates>
}