package com.hendraanggrian.openpss.schema

import com.hendraanggrian.openpss.data.Recess
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.time

object Recesses : DocumentSchema<Recess>("recesses", Recess::class) {
    val start = time("start")
    val end = time("end")
}