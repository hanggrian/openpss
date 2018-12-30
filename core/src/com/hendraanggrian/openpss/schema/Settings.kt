package com.hendraanggrian.openpss.schema

import com.hendraanggrian.openpss.data.Setting
import com.hendraanggrian.openpss.nosql.Schema
import kotlinx.nosql.string

object Settings : Schema<Setting>("settings", Setting::class) {
    val key = string("key")
    val value = string("value")

    val LANGUAGE = "language" to "en-US" // or equivalent to Language.EN_US.fullCode
    val INVOICE_HEADERS = "invoice_headers" to ""
}