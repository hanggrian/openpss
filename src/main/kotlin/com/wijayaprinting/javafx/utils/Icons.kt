@file:JvmName("IconsKt")
@file:Suppress("NOTHING_TO_INLINE")

package com.wijayaprinting.javafx.utils

import javafx.application.Application
import javafx.scene.control.Dialog
import javafx.scene.image.Image
import javafx.stage.Stage
import org.apache.commons.lang3.SystemUtils

inline var Stage.icon: Image
    get() = icons[0]
    set(value) {
        if (icons.isEmpty()) icons.clear()
        icons.add(value)
    }

inline var Dialog<*>.icon: Image
    get() = (dialogPane.scene.window as Stage).icon
    set(value) {
        (dialogPane.scene.window as Stage).icon = value
    }

inline fun Application.setIconOnOSX(image: java.awt.Image) {
    if (SystemUtils.IS_OS_MAC_OSX) {
        Class.forName("com.apple.eawt.Application")
                .newInstance()
                .javaClass
                .getMethod("getApplication")
                .invoke(null).let { application ->
            application.javaClass
                    .getMethod("setDockIconImage", java.awt.Image::class.java)
                    .invoke(application, image)
        }
    }
}