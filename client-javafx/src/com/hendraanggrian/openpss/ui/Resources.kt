package com.hendraanggrian.openpss.ui

import com.hendraanggrian.openpss.content.Language
import com.hendraanggrian.openpss.util.getResourceAsStream
import javafx.scene.paint.Color
import java.util.Properties
import java.util.ResourceBundle

/** Easier access to [ResourceBundle] across components. */
interface Resources {

    val resourceBundle: ResourceBundle

    val valueProperties: Properties

    val language: Language
        get() = Language.ofCode(
            resourceBundle.baseBundleName.substringAfter('_')
        )

    fun getString(id: String): String = resourceBundle.getString(id)

    fun getString(id: String, vararg args: Any): String = getString(id).format(*args)

    fun getLong(id: String): Long = valueProperties.getProperty(id).toLong()

    fun getDouble(id: String): Double = valueProperties.getProperty(id).toDouble()

    fun getColor(id: String): Color = Color.web(valueProperties.getProperty(id))

    fun getProperties(propertiesId: String): Properties = getResourceAsStream(propertiesId).use { stream ->
        Properties().apply { load(stream) }
    }

    /** Mark enum value to be translatable. */
    interface Enum {

        val resourceId: String

        fun toString(resources: Resources): String = resources.getString(resourceId)
    }
}