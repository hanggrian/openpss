package com.hanggrian.openpss.db

import javafx.util.Builder
import kotlinx.nosql.AndQuery
import kotlinx.nosql.Query
import kotlinx.nosql.query.NoQuery
import kotlinx.nosql.query.OrQuery

interface QueryBuilder {
    fun and(target: Query)

    fun or(target: Query)
}

class QueryBuilderImpl :
    QueryBuilder,
    Builder<Query> {
    private var source: Query? = null

    override fun and(target: Query) {
        source = source?.let { AndQuery(it, target) } ?: target
    }

    override fun or(target: Query) {
        source = source?.let { OrQuery(it, target) } ?: target
    }

    override fun build(): Query = source ?: NoQuery
}
