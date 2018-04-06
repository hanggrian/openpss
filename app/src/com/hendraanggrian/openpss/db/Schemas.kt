@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.db

import kotlinx.nosql.DocumentSchemaQueryWrapper
import kotlinx.nosql.Id
import kotlinx.nosql.equal
import kotlinx.nosql.id
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.mongodb.MongoDBSession

inline fun <S : DocumentSchema<D>, D : Document<S>> MongoDBSession.findByDoc(
    schema: S,
    document: Document<S>
): DocumentSchemaQueryWrapper<S, String, D> = schema.find { this.id.equal(document.id) }

inline fun <S : DocumentSchema<D>, D : Document<S>> MongoDBSession.findById(
    schema: S,
    id: Id<String, S>
): DocumentSchemaQueryWrapper<S, String, D> = schema.find { this.id.equal(id) }