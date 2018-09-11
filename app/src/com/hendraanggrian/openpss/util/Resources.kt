@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.util

import com.hendraanggrian.openpss.App
import javafx.scene.paint.Color
import javafx.scene.paint.Color.web
import javafx.scene.text.Font
import javafx.scene.text.Font.loadFont
import java.io.InputStream
import java.net.URL

inline fun getResource(name: String): URL = App::class.java.getResource(name)

inline fun getResourceAsStream(name: String): InputStream = App::class.java.getResourceAsStream(name)

inline fun getStyle(name: String): String = getResource(name).toExternalForm()

inline fun getFont(name: String, size: Number = -1): Font {
    var f = loadFont(getResource(name).toExternalForm(), size.toDouble())
    if (f == null) {
        // fallback method for when `loadFont(String, Double)` doesn't work, known to happen in Windows.
        f = getResourceAsStream(name).use { loadFont(it, size.toDouble()) }
    }
    return f
}

// should probably not be here
inline fun getColor(name: String): Color = web(name)