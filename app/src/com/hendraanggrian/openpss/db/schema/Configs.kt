package com.hendraanggrian.openpss.db.schema

import com.hendraanggrian.openpss.db.dao.Config
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.string

object Configs : DocumentSchema<Config>("config", Config::class) {
    val key = string("key")
    val value = string("value")
}