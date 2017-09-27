package com.wijayaprinting.javafx.io

import java.io.File
import java.util.*

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
abstract class PropertiesFile(child: String) : File(WPFolder(), child) {

    abstract fun save(comments: String?)

    fun save() = save(null)

    /** Properties reference to get, set, and finally save into this mysqlFile. */
    private val properties: Properties = Properties()

    init {
        createNewFileIfNotExists()
        inputStream().let {
            properties.load(it)
            it.close()
        }
    }

    @JvmOverloads
    protected fun getString(key: String, defaultValue: String? = null): String = when (defaultValue) {
        null -> properties.getProperty(key)
        else -> properties.getProperty(key, defaultValue)
    }

    protected fun setString(key: String, value: String): Any = properties.setProperty(key, value)

    protected fun store(comments: String? = null): Unit = outputStream().let {
        properties.store(it, comments)
        it.close()
    }

    private fun createNewFileIfNotExists() {
        if (!exists()) createNewFile()
    }
}