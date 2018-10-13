package com.hendraanggrian.openpss.db

import javafx.util.Builder
import kotlinx.nosql.AndQuery
import kotlinx.nosql.Query
import kotlinx.nosql.query.NoQuery
import kotlinx.nosql.query.OrQuery

interface QueryBuilder {

    fun and(target: Query)

    fun or(target: Query)
}

@Suppress("ClassName")
class _QueryBuilder : QueryBuilder, Builder<Query> {
    private var source: Query? = null

    override fun and(target: Query) = setOrCreate(target) { AndQuery(it, target) }

    override fun or(target: Query) = setOrCreate(target) { OrQuery(it, target) }

    override fun build(): Query = source ?: NoQuery

    private fun setOrCreate(target: Query, creator: (Query) -> Query) {
        source = source?.let { creator(it) } ?: target
    }
}