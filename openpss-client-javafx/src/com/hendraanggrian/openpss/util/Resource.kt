package com.hendraanggrian.openpss.util

import com.hendraanggrian.openpss.OpenPssApp
import java.io.InputStream
import java.net.URL

fun getResource(name: String): URL = OpenPssApp::class.java.getResource(name)

fun getResourceAsStream(name: String): InputStream = OpenPssApp::class.java.getResourceAsStream(name)

fun getStyle(name: String): String = getResource(name).toExternalForm()
