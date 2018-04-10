package com.hendraanggrian.openpss.io.properties

import com.hendraanggrian.openpss.io.MainFolder
import javafx.beans.property.Property
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
    protected val properties = Properties()
    protected val cache = WeakHashMap<String, Property<*>>()

    init {
        @Suppress("LeakingThis") if (!exists()) createNewFile()
        inputStream().use { properties.load(it) }
    }

    suspend fun save(comments: String? = null) = async {
        outputStream().use { properties.store(it, comments) }
    }.await()

    @Suppress("UNCHECKED_CAST")
    protected inline operator fun <reified T, R : Property<T>> T.getValue(thisRef: Any?, property: KProperty<*>): R {
        val key = property.name.toLowerCase()
        var value = cache[key]
        if (value == null) {
            val propertyString = properties.getProperty(key, toString())!!
            value = when (T::class) {
                String::class -> propertyString.toProperty()
                Boolean::class -> propertyString.toBoolean().toProperty()
                else -> propertyString.toProperty()
            }
            cache[key] = value
        }
        value.listener { _, _, newValue -> properties.setProperty(key, newValue.toString()) }
        return value as R
    }
}