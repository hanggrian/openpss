@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.utils

import com.hendraanggrian.openpss.App
import javafx.scene.paint.Color
import javafx.scene.paint.Color.web
import javafx.scene.text.Font
import javafx.scene.text.Font.loadFont
import java.io.InputStream
import java.net.URL

inline fun getResource(name: String): URL = App::class.java.getResource(name)

inline fun getResourceAsStream(name: String): InputStream = App::class.java.getResourceAsStream(name)

inline fun getFont(name: String, size: Int = 13): Font = loadFont(getResource(name).toExternalForm(), size.toDouble())

inline fun getColor(name: String): Color = web(name)