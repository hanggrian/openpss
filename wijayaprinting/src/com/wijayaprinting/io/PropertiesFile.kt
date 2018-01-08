package com.wijayaprinting.io

import com.wijayaprinting.utils.createIfNotExists
import com.wijayaprinting.utils.hideOnWindows
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
        createIfNotExists()
        hideOnWindows()
        inputStream().use { properties.load(it) }
        pairs.forEach { map.put(it.first, properties.getProperty(it.first, it.second).asMutableProperty()) }
    }

    @JvmOverloads
    fun save(comments: String? = null) {
        map.keys.forEach { properties.setProperty(it, map[it]!!.value) }
        outputStream().use { properties.store(it, comments) }
    }
}