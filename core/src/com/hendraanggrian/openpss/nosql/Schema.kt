package com.hendraanggrian.openpss.nosql

import kotlinx.nosql.mongodb.DocumentSchema
import kotlin.reflect.KClass

abstract class Schema<D : Any>(schemed: NamedSchemed, valueClass: KClass<D>) :
    DocumentSchema<D>("$schemed", valueClass), NamedSchemed {

    abstract override fun toString(): String
}