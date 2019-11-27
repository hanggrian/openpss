package com.hendraanggrian.openpss.util

import com.hendraanggrian.openpss.App
import java.io.InputStream
import java.net.URL

fun getResource(name: String): URL = App::class.java.getResource(name)

fun getResourceAsStream(name: String): InputStream = App::class.java.getResourceAsStream(name)

fun getStyle(name: String): String = getResource(name).toExternalForm()
