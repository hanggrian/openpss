package com.hendraanggrian.openpss.nosql

import com.hendraanggrian.openpss.schema.GlobalSetting
import com.hendraanggrian.openpss.schema.GlobalSettings
import kotlinx.nosql.AbstractColumn
import kotlinx.nosql.AbstractSchema
import kotlinx.nosql.DocumentSchemaOperations
import kotlinx.nosql.Id
import kotlinx.nosql.IndexOperations
import kotlinx.nosql.Query
import kotlinx.nosql.Session
import kotlinx.nosql.TableSchemaOperations
import kotlinx.nosql.equal
import kotlinx.nosql.id
import kotlinx.nosql.mongodb.MongoDBSession
import kotlinx.nosql.query.NoQuery

/** Extended version of [MongoDBSession]. */
class SessionWrapper(private val session: MongoDBSession) : Session by session,
    DocumentSchemaOperations by session,
    TableSchemaOperations by session,
    IndexOperations by session {

    /** Same with `MongoDBSession`'s `find` but moved schema instance from receiver to parameter. */
    operator fun <S : Schema<D>, D : Document<S>> S.invoke(
        query: S.() -> Query = { NoQuery }
    ): DocumentQuery<S, String, D> = session.run { return find(query) }

    /** Realm-style find by id. */
    operator fun <S : Schema<D>, D : Document<S>> S.get(id: StringId<S>): DocumentQuery<S, String, D> = invoke {
        this.id.equal(id)
    }

    /** Find by id associated with document. */
    operator fun <S : Schema<D>, D : Document<S>> S.get(document: D): DocumentQuery<S, String, D> = get(document.id)

    operator fun <S : Schema<D>, D : Document<S>> S.get(idValue: String): DocumentQuery<S, String, D> = get(Id(idValue))

    /** Use shorter `plusAssign` operator when inserted document's id is unused. */
    operator fun <S : Schema<D>, D : Document<S>> S.plusAssign(document: D) {
        insert(document)
    }

    operator fun <S : Schema<D>, D : Document<S>> S.minusAssign(id: StringId<S>) {
        get(id).remove()
    }

    operator fun <S : Schema<D>, D : Document<S>> S.minusAssign(document: D) {
        get(document).remove()
    }

    /** Build query for optional and/or query operation. */
    fun <S : Schema<D>, D : Document<S>> S.buildQuery(
        builder: QueryBuilder.() -> Unit
    ): DocumentQuery<S, String, D> = invoke { QueryBuilderImpl().apply { builder() }.build() }

    fun findGlobalSetting(key: String): GlobalSetting = GlobalSettings { this.key.equal(key) }.single()

    /** Matches with [regex] automatically transformed to pattern with certain [flags]. */
    fun <T : AbstractSchema, C> AbstractColumn<out C?, T, *>.matches(
        regex: Any,
        flags: Int = 0
    ): Query = matches("$regex".toPattern(flags))
}
