package com.hendraanggrian.openpss.nosql

import kotlin.reflect.KClass
import kotlinx.nosql.mongodb.DocumentSchema

abstract class Schema<D : Any>(schemaName: String, klass: KClass<D>) : DocumentSchema<D>(schemaName, klass), Schemed
