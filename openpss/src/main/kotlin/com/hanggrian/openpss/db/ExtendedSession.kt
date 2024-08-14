package com.hanggrian.openpss.db

import com.hanggrian.openpss.Context
import com.hanggrian.openpss.OpenPssApp
import com.hanggrian.openpss.R
import com.hanggrian.openpss.db.schemas.GlobalSetting
import com.hanggrian.openpss.db.schemas.GlobalSettings
import com.hanggrian.openpss.db.schemas.Invoice
import com.hanggrian.openpss.db.schemas.Invoices
import com.hanggrian.openpss.db.schemas.Payments
import kotlinx.nosql.DocumentSchemaOperations
import kotlinx.nosql.DocumentSchemaQueryWrapper
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
import kotlinx.nosql.update
import ktfx.jfoenix.controls.jfxSnackbar

/** Extended version of [MongoDBSession]. */
class ExtendedSession(val session: MongoDBSession) :
    Session by session,
    DocumentSchemaOperations by session,
    TableSchemaOperations by session,
    IndexOperations by session {
    /** Use shorter `plusAssign` operator when inserted document's id is unused. */
    inline operator fun <S : DocumentSchema<D>, D : Document<S>> S.plusAssign(document: D) {
        insert(document)
    }

    /** Same with `MongoDBSession`'s `find` but moved schema instance from receiver to parameter. */
    inline operator fun <S : DocumentSchema<D>, D : Document<S>> S.invoke(
        noinline query: (S) -> Query = { NoQuery },
    ): DocumentSchemaQueryWrapper<S, String, D> = session.run { return find(query) }

    /** Realm-style find by id. */
    inline operator fun <S : DocumentSchema<D>, D : Document<S>> S.get(
        id: Id<String, S>,
    ): DocumentSchemaQueryWrapper<S, String, D> = invoke { it.id.equal(id) }

    /** Find by id associated with document. */
    inline operator fun <S : DocumentSchema<D>, D : Document<S>> S.get(
        document: D,
    ): DocumentSchemaQueryWrapper<S, String, D> = get(document.id)

    inline operator fun <S : DocumentSchema<D>, D : Document<S>> S.minusAssign(id: Id<String, S>) {
        get(id).remove()
    }

    inline operator fun <S : DocumentSchema<D>, D : Document<S>> S.minusAssign(document: D) {
        get(document).remove()
    }

    /** Build query for optional and/or query operation. */
    fun <S : DocumentSchema<D>, D : Document<S>> S.buildQuery(
        builder: QueryBuilder.(S) -> Unit,
    ): DocumentSchemaQueryWrapper<S, String, D> =
        invoke { QueryBuilderImpl().apply { builder(this@buildQuery) }.build() }

    fun findGlobalSettings(
        key: String,
    ): DocumentSchemaQueryWrapper<GlobalSettings, String, GlobalSetting> =
        GlobalSettings { it.key.equal(key) }

    fun Invoice.done(context: Context): Boolean {
        val query = Invoices[this]
        if (query.single().isDone) {
            context.stack.jfxSnackbar(
                context.getString(R.string_already_done),
                OpenPssApp.DURATION_LONG,
            )
            return false
        }
        query.projection { isDone }.update(true)
        return true
    }

    fun Invoice.calculateDue(): Double =
        total - Payments { it.invoiceId.equal(id) }.sumByDouble { it.value }
}
