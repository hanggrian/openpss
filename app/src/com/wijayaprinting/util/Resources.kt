@file:Suppress("NOTHING_TO_INLINE")

package com.wijayaprinting.util

import com.wijayaprinting.App
import java.io.InputStream
import java.lang.ref.WeakReference
import java.net.URL

private var ref: WeakReference<Class<*>> = WeakReference(App::class.java)
private val mainClass: Class<*>
    get() {
        var cls = ref.get()
        if (cls == null) {
            cls = App::class.java
            ref = WeakReference(cls)
        }
        return cls
    }

fun getResource(name: String): URL = mainClass.getResource(name)
fun getResourceAsStream(name: String): InputStream = mainClass.getResourceAsStream(name)