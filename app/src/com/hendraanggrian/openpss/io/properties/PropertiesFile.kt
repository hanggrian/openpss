package com.hendraanggrian.openpss.io.properties

import com.hendraanggrian.openpss.io.MainFolder
import javafx.beans.property.StringProperty
import kotfx.beans.property.toProperty
import kotfx.coroutines.listener
import java.io.File
import java.util.Properties

/**
 * Represents a file of [Properties] that acts as local settings.
 * It is saved as hidden file located in [MainFolder].
 *
 * Since it is hidden, some systems will misrepresent file name as extension.
 * To avoid this issue, use an unusual name that is not a known file extension.
 */
@Suppress("LeakingThis")
abstract class PropertiesFile(
    name: String,
    private val map: MutableMap<String, StringProperty> = mutableMapOf()
) : File(MainFolder, ".$name"), MutableMap<String, StringProperty> by map {

    abstract val pairs: Array<Pair<String, String>>

    /** Properties reference to get, set, and finally save into this file. */
    private val properties = Properties()

    init {
        if (!exists()) createNewFile()
        inputStream().use { properties.load(it) }
        pairs.forEach { (key, value) ->
            val valueProperty = properties.getProperty(key, value).toProperty()
            valueProperty.listener { _, _, newValue -> properties.setProperty(key, newValue) }
            map[key] = valueProperty
        }
    }

    fun save(comments: String? = null): Unit = outputStream().use { properties.store(it, comments) }
}