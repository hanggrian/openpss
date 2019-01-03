@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.util

import com.hendraanggrian.openpss.OpenPssApp
import java.io.InputStream
import java.net.URL

inline fun getResource(name: String): URL = OpenPssApp::class.java.getResource(name)

inline fun getResourceAsStream(name: String): InputStream = OpenPssApp::class.java.getResourceAsStream(name)

inline fun getStyle(name: String): String = getResource(name).toExternalForm()