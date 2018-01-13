package com.wijayaprinting.io

import javafx.beans.property.StringProperty
import kotfx.asMutableProperty
import java.io.File
import java.util.*
import kotlin.collections.HashMap

/** Represents a properties file used that acts as local settings. */
open class PropertiesFile(child: String, vararg pairs: Pair<String, String>) : File(PropertiesFolder(), child) {

    /** Properties reference to get, set, and finally save into this file. */
    private val properties = Properties()

    /** Actual map that stores properties for bindings. */
    protected val map = HashMap<String, StringProperty>()

    init {
        @Suppress("LeakingThis") if (!exists()) createNewFile()
        inputStream().use { properties.load(it) }
        pairs.forEach {
            val key = it.first
            val value = properties.getProperty(it.first, it.second).asMutableProperty()
            value.addListener { _, _, newValue -> properties.setProperty(key, newValue) }
            map.put(key, value)
        }
    }

    @JvmOverloads
    fun save(comments: String? = null): Unit = outputStream().use { properties.store(it, comments) }
}