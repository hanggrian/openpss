package com.hendraanggrian.openpss

import javafx.scene.paint.Color
import java.util.Properties

interface ValueResources {

    val valueProperties: Properties

    fun getLong(id: String): Long = valueProperties.getProperty(id).toLong()

    fun getDouble(id: String): Double = valueProperties.getProperty(id).toDouble()

    fun getColor(id: String): Color = Color.web(valueProperties.getProperty(id))
}