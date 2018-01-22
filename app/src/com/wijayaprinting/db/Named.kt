package com.wijayaprinting.db

import kotlinx.nosql.mongodb.DocumentSchema

/** Some DAOs have name. */
interface Named<S : DocumentSchema<*>> : Ided<S> {

    val name: String

    override fun toString(): String
}