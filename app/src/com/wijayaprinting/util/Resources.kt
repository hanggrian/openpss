@file:Suppress("NOTHING_TO_INLINE")

package com.wijayaprinting.util

import com.wijayaprinting.App
import javafx.scene.text.Font
import java.io.InputStream
import java.net.URL

inline fun getResource(name: String): URL = App::class.java.getResource(name)
inline fun getResourceAsStream(name: String): InputStream = App::class.java.getResourceAsStream(name)

inline fun getExternalForm(id: String): String = getResource(id).toExternalForm()

inline fun getFont(font: String, size: Number): Font = Font.loadFont(getExternalForm(font), size.toDouble())