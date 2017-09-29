@file:JvmName("IOsKt")
@file:Suppress("NOTHING_TO_INLINE")

package com.wijayaprinting.javafx.utils

import java.io.Closeable

inline fun <T : Closeable> T.use(block: (T) -> Unit) {
    block(this)
    close()
}