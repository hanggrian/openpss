package com.hendraanggrian.openpss.schema

import com.hendraanggrian.openpss.data.Recess
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.time

object Recesses : DocumentSchema<Recess>("$Recesses", Recess::class),
    Schemed {
    val start = time("start")
    val end = time("end")

    override fun toString(): String = "recesses"
}