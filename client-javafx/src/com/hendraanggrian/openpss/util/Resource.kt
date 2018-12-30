@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.util

import com.hendraanggrian.openpss.OpenPssApplication
import java.io.InputStream
import java.net.URL

inline fun getResource(name: String): URL = OpenPssApplication::class.java.getResource(name)

inline fun getResourceAsStream(name: String): InputStream = OpenPssApplication::class.java.getResourceAsStream(name)

inline fun getStyle(name: String): String = getResource(name).toExternalForm()