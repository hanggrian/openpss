package com.hendraanggrian.openpss.db

import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.EmployeeAccesses
import com.hendraanggrian.openpss.db.schemas.GlobalSetting
import com.hendraanggrian.openpss.db.schemas.GlobalSettings
import com.hendraanggrian.openpss.db.schemas.Invoice
import com.hendraanggrian.openpss.db.schemas.Payments
import com.hendraanggrian.openpss.util.isNotEmpty
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

/** Extended version of [MongoDBSession]. */
@Suppress("NOTHING_TO_INLINE")
class SessionWrapper(val session: MongoDBSession) :
    Session by session,
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
    inline fun <S : DocumentSchema<D>, D : Document<S>> S.find(
        noinline query: (S) -> Query
    ): DocumentSchemaQueryWrapper<S, String, D> = session.run { return find(query) }

    inline fun <S : DocumentSchema<D>, D : Document<S>> S.findById(
        id: Id<String, S>
    ): DocumentSchemaQueryWrapper<S, String, D> = find { it.id.equal(id) }

    inline fun <S : DocumentSchema<D>, D : Document<S>> S.findByDoc(
        document: D
    ): DocumentSchemaQueryWrapper<S, String, D> = findById(document.id)

    inline fun <S : DocumentSchema<D>, D : Document<S>> S.findWithBuilder(
        noinline builder: QueryBuilder.(S) -> Unit
    ): DocumentSchemaQueryWrapper<S, String, D> = find {
        _QueryBuilder().apply { builder(this@findWithBuilder) }.build()
    }

    inline fun Employee.isFullAccess(): Boolean =
        EmployeeAccesses.find { it.employeeId.equal(id) }.isNotEmpty()

    inline fun findGlobalSettings(
        key: String
    ): DocumentSchemaQueryWrapper<GlobalSettings, String, GlobalSetting> = GlobalSettings.find { it.key.equal(key) }

    inline fun Invoice.calculateDue(): Double =
        total - Payments.find { it.invoiceId.equal(id) }.sumByDouble { it.value }
}
