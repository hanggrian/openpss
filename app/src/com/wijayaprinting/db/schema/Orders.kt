package com.wijayaprinting.db.schema

import kotlinx.nosql.Discriminator
import kotlinx.nosql.double
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.string
import kotlin.reflect.KClass

open class Orders<D : Any, S : DocumentSchema<D>>(klass: KClass<D>, discriminator: String) : DocumentSchema<D>("order", klass, Discriminator(string("type"), discriminator)) {
    val total = double<S>("total")
}