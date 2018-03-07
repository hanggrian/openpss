package com.hendraanggrian.openpss.db

import kotlinx.nosql.Discriminator
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.string
import kotlin.reflect.KClass

abstract class NamedDocumentSchema<D : Any>(
    name: String,
    valueClass: KClass<D>,
    discriminator: Discriminator<out Any, out kotlinx.nosql.DocumentSchema<String, D>>? = null
) : DocumentSchema<D>(name, valueClass, discriminator) {

    val name = string("name")
}