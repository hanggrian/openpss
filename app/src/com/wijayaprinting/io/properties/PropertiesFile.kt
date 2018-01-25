package com.wijayaprinting.io.properties

import com.wijayaprinting.io.HiddenFolder
import javafx.beans.property.StringProperty
import kotfx.asMutableProperty
import java.io.File
import java.util.*

/**
 * Represents a file of [Properties] that acts as local settings.
 * It is saved as hidden file located in [HiddenFolder].
 */
@Suppress("LeakingThis")
abstract class PropertiesFile(
        name: String,
        private val map: MutableMap<String, StringProperty> = mutableMapOf()
) : File(HiddenFolder, ".$name"), MutableMap<String, StringProperty> by map {

    abstract val pairs: Array<Pair<String, String>>

    /** Properties reference to get, set, and finally save into this file. */
    private val properties = Properties()

    init {
        if (!exists()) createNewFile()
        inputStream().use { properties.load(it) }
        pairs.forEach { (key, value) ->
            val valueProperty = properties.getProperty(key, value).asMutableProperty()
            valueProperty.addListener { _, _, newValue -> properties.setProperty(key, newValue) }
            map[key] = valueProperty
        }
    }

    @JvmOverloads fun save(comments: String? = null): Unit = outputStream().use { properties.store(it, comments) }
}