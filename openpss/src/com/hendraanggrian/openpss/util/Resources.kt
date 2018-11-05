@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.util

import com.hendraanggrian.openpss.App
import javafx.scene.paint.Color
import javafx.scene.paint.Color.web
import java.io.InputStream
import java.net.URL

inline fun getResource(name: String): URL = App::class.java.getResource(name)

inline fun getResourceAsStream(name: String): InputStream = App::class.java.getResourceAsStream(name)

inline fun getStyle(name: String): String = getResource(name).toExternalForm()

// should probably not be here
inline fun getColor(name: String): Color = web(name)