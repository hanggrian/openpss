package com.hanggrian.openpss.io.properties

import com.hanggrian.openpss.io.MainDirectory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Properties
import kotlin.reflect.KProperty

/**
 * Represents a file of [Properties] that acts as local settings.
 * It is saved as hidden file located in [MainDirectory].
 *
 * Since it is hidden, some systems will misrepresent file name as extension.
 * To avoid this issue, use an unusual name that is not a known file extension.
 */
abstract class PropertiesFile(name: String) : File(MainDirectory, ".$name") {
    /** Properties reference to get, set, and finally save into this file. */
    private val properties = Properties()

    init {
        if (!exists()) {
            createNewFile()
        }
        inputStream().use { properties.load(it) }
    }

    suspend fun save(comments: String? = null) =
        withContext(Dispatchers.Default) { outputStream().use { properties.store(it, comments) } }

    protected operator fun <T> T.getValue(thisRef: Any?, property: KProperty<*>): T {
        val value = properties.getProperty(property.key, toString())!!
        return when (this) {
            is Boolean -> value.toBoolean() as T
            is Double -> value.toDouble() as T
            is Int -> value.toInt() as T
            else -> value as T
        }
    }

    protected operator fun <T> T.setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        properties.setProperty(property.key, value.toString())
    }

    private inline val KProperty<*>.key: String get() = name.lowercase()
}
