package com.hendraanggrian.openpss.server.db

import com.hendraanggrian.openpss.data.Document
import com.hendraanggrian.openpss.data.GlobalSetting
import com.hendraanggrian.openpss.schema.GlobalSettings
import kotlinx.nosql.DocumentSchemaOperations
import kotlinx.nosql.Id
import kotlinx.nosql.IndexOperations
import kotlinx.nosql.Query
import kotlinx.nosql.Session
import kotlinx.nosql.TableSchemaOperations
import kotlinx.nosql.equal
import kotlinx.nosql.id
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.mongodb.MongoDBSession
import kotlinx.nosql.query.NoQuery

/** Extended version of [MongoDBSession]. */
@Suppress("NOTHING_TO_INLINE")
class SessionWrapper(val session: MongoDBSession) : Session by session,
    DocumentSchemaOperations by session,
    TableSchemaOperations by session,
    IndexOperations by session {

    /** Use shorter `plusAssign` operator when inserted document's id is unused. */
    inline operator fun <S : DocumentSchema<D>, D : Document<S>> S.plusAssign(
        document: D
    ) {
        insert(document)
    }

    /** Same with `MongoDBSession`'s `find` but moved schema instance from receiver to parameter. */
    inline operator fun <S : DocumentSchema<D>, D : Document<S>> S.invoke(
        noinline query: (S) -> Query = { NoQuery }
    ): DocumentQuery<S, String, D> = session.run { return find(query) }

    /** Realm-style find by id. */
    inline operator fun <S : DocumentSchema<D>, D : Document<S>> S.get(
        id: Id<String, S>
    ): DocumentQuery<S, String, D> = invoke { it.id.equal(id) }

    /** Find by id associated with document. */
    inline operator fun <S : DocumentSchema<D>, D : Document<S>> S.get(
        document: D
    ): DocumentQuery<S, String, D> = get(document.id)

    inline operator fun <S : DocumentSchema<D>, D : Document<S>> S.get(
        idValue: String
    ): DocumentQuery<S, String, D> = get(Id(idValue))

    inline operator fun <S : DocumentSchema<D>, D : Document<S>> S.minusAssign(
        id: Id<String, S>
    ) {
        get(id).remove()
    }

    inline operator fun <S : DocumentSchema<D>, D : Document<S>> S.minusAssign(
        document: D
    ) {
        get(document).remove()
    }

    /** Build query for optional and/or query operation. */
    fun <S : DocumentSchema<D>, D : Document<S>> S.buildQuery(
        builder: QueryBuilder.(S) -> Unit
    ): DocumentQuery<S, String, D> = invoke {
        _QueryBuilder().apply { builder(this@buildQuery) }.build()
    }

    fun findGlobalSettings(
        key: String
    ): DocumentQuery<GlobalSettings, String, GlobalSetting> = GlobalSettings { it.key.equal(key) }
}