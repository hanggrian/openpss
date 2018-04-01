package com.hendraanggrian.openpss.db

import javafx.util.Builder
import kotlinx.nosql.AndQuery
import kotlinx.nosql.Query
import kotlinx.nosql.query.NoQuery
import kotlinx.nosql.query.OrQuery

interface QueryBuilder : Builder<Query> {

    fun append(target: Query)
}

private abstract class _QueryBuilder : QueryBuilder {
    private var source: Query? = null

    abstract fun newInstance(source: Query, target: Query): Query

    override fun append(target: Query) {
        source = source?.let { newInstance(it, target) } ?: target
    }

    override fun build(): Query = source ?: NoQuery
}

fun andQueryBuilder(builder: QueryBuilder.() -> Unit): Query = object : _QueryBuilder() {
    override fun newInstance(source: Query, target: Query): Query = AndQuery(source, target)
}.apply(builder).build()

fun orQueryBuilder(builder: QueryBuilder.() -> Unit): Query = object : _QueryBuilder() {
    override fun newInstance(source: Query, target: Query): Query = OrQuery(source, target)
}.apply(builder).build()