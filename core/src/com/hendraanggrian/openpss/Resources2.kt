package com.hendraanggrian.openpss

import javafx.scene.paint.Color
import java.util.Properties
import java.util.ResourceBundle

/** Easier access to [ResourceBundle] across components. */
interface Resources2 {

    val resourceBundle: ResourceBundle

    val valueProperties: Properties

    val language: Language
        get() = Language.ofCode(
            resourceBundle.locale.language
        )

    fun getString(id: String): String = resourceBundle.getString(id)

    fun getString(id: String, vararg args: Any): String = getString(id).format(*args)

    fun getLong(id: String): Long = valueProperties.getProperty(id).toLong()

    fun getDouble(id: String): Double = valueProperties.getProperty(id).toDouble()

    fun getColor(id: String): Color = Color.web(valueProperties.getProperty(id))

    /** Mark enum value to be translatable. */
    interface Enum {

        val resourceId: String

        fun toString(resources: Resources2): String = resources.getString(resourceId)
    }
}