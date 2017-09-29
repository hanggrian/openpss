@file:JvmName("FileStreamsKt")
@file:Suppress("NOTHING_TO_INLINE")

package com.wijayaprinting.javafx.utils

import java.io.Closeable
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

inline fun File.useInputStream(block: (FileInputStream) -> Unit) {
    val stream = inputStream()
    block(stream)
    stream.close()
}

inline fun File.useOutputStream(block: (FileOutputStream) -> Unit) {
    val stream = outputStream()
    block(stream)
    stream.close()
}

inline fun <T : Closeable> T.use(block: (T) -> Unit) {
    block(this)
    close()
}