package com.hendraanggrian.openpss

import java.util.Properties

interface ValueResources {

    val valueProperties: Properties

    fun getLong(id: String): Long = valueProperties.getProperty(id).toLong()

    fun getDouble(id: String): Double = valueProperties.getProperty(id).toDouble()
}