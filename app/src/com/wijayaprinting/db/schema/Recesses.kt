package com.wijayaprinting.db.schema

import com.wijayaprinting.db.dao.Recess
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.time

object Recesses : DocumentSchema<Recess>("recess", Recess::class) {
    val start = time("start")
    val end = time("end")
}