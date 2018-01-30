@file:Suppress("NOTHING_TO_INLINE")

package com.wijayaprinting.util

import com.wijayaprinting.App
import javafx.scene.text.Font
import javafx.scene.text.Font.loadFont
import java.io.InputStream
import java.net.URL

private var mainClass: Class<*>? = null
    get() {
        if (field == null) field = App::class.java
        return field
    }

fun getResource(name: String): URL = mainClass!!.getResource(name)
fun getResourceAsStream(name: String): InputStream = mainClass!!.getResourceAsStream(name)

inline fun getExternalForm(id: String): String = getResource(id).toExternalForm()

inline fun getFont(font: String, size: Number): Font = loadFont(getExternalForm(font), size.toDouble())