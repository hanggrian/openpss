package com.hanggrian.openpss.util

import com.hanggrian.openpss.OpenPssApp
import java.io.InputStream
import java.net.URL

inline fun getResource(name: String): URL = OpenPssApp::class.java.getResource(name)!!

inline fun getResourceAsStream(name: String): InputStream =
    OpenPssApp::class.java.getResourceAsStream(name)!!

inline fun getStyle(name: String): String = getResource(name).toExternalForm()
