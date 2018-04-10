package com.hendraanggrian.openpss.io.properties

import com.hendraanggrian.openpss.io.MainFolder
import javafx.beans.property.StringProperty
import kotlinx.coroutines.experimental.async
import ktfx.beans.property.toProperty
import ktfx.coroutines.listener
import java.io.File
import java.util.Properties
import java.util.WeakHashMap
import kotlin.reflect.KProperty

/**
 * Represents a file of [Properties] that acts as local settings.
 * It is saved as hidden file located in [MainFolder].
 *
 * Since it is hidden, some systems will misrepresent file name as extension.
 * To avoid this issue, use an unusual name that is not a known file extension.
 */
abstract class PropertiesFile(name: String) : File(MainFolder, ".$name") {

    /** Properties reference to get, set, and finally save into this file. */
    private val properties = Properties()
    private val cache = WeakHashMap<String, StringProperty>()

    init {
        @Suppress("LeakingThis") if (!exists()) createNewFile()
        inputStream().use { properties.load(it) }
    }

    suspend fun save(comments: String? = null) = async {
        outputStream().use { properties.store(it, comments) }
    }.await()

    operator fun Any?.getValue(thisRef: Any?, property: KProperty<*>): StringProperty {
        val key = property.name.toLowerCase()
        var value = cache[key]
        if (value == null) {
            value = properties.getProperty(key, this as? String ?: "").toProperty()
            cache[key] = value
        }
        value.listener { _, _, newValue -> properties.setProperty(key, newValue) }
        return value
    }
}