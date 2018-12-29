package com.hendraanggrian.openpss.nosql

import kotlinx.nosql.mongodb.DocumentSchema
import kotlin.reflect.KClass

abstract class Schema<D : Any>(schemaName: String, klass: KClass<D>) : DocumentSchema<D>(schemaName, klass), Schemed