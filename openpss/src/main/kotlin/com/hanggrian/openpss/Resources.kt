package com.hanggrian.openpss

import com.hanggrian.openpss.util.getResourceAsStream
import javafx.scene.paint.Color
import java.util.Properties
import java.util.ResourceBundle

/** Easier access to [ResourceBundle] across components. */
interface Resources {
    val resourceBundle: ResourceBundle

    val dimenResources: Properties

    val colorResources: Properties

    val language: Language
        get() = Language.ofCode(resourceBundle.baseBundleName.substringAfter('_'))

    fun getString(id: String): String = resourceBundle.getString(id)

    fun getString(id: String, vararg args: Any): String = getString(id).format(*args)

    fun getDouble(id: String): Double = dimenResources.getProperty(id).toDouble()

    fun getColor(id: String): Color = Color.web(colorResources.getProperty(id))

    fun getProperties(propertiesId: String): Properties =
        getResourceAsStream(propertiesId)
            .use { stream -> Properties().apply { load(stream) } }

    /** Mark enum value to be translatable. */
    interface Enum {
        val resourceId: String

        fun toString(resources: Resources): String = resources.getString(resourceId)
    }
}
