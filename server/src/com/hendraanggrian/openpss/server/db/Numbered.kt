package com.hendraanggrian.openpss.server.db

import com.hendraanggrian.openpss.db.Document
import com.hendraanggrian.openpss.db.Numbered
import kotlinx.nosql.mongodb.DocumentSchema

/** Determine next integer based on the last one. */
fun <S : DocumentSchema<D>, D> S.nextNo(): Int where D : Document<S>, D : Numbered =
    transaction { this@nextNo().lastOrNull()?.no ?: 0 } + 1