@file:JvmName("DimensionsKt")
@file:Suppress("NOTHING_TO_INLINE")

package com.wijayaprinting.javafx.scene.utils

import javafx.scene.layout.GridPane
import javafx.scene.layout.Region

inline fun Region.setSize(size: Double) {
    setMinSize(size)
    setPrefSize(size)
    setMaxSize(size)
}

inline fun Region.setSize(width: Double, height: Double) {
    setMinSize(width, height)
    setPrefSize(width, height)
    setMaxSize(width, height)
}

inline fun Region.setMinSize(size: Double) {
    minWidth = size
    minHeight = size
}

inline fun Region.setPrefSize(size: Double) {
    prefWidth = size
    prefHeight = size
}

inline fun Region.setMaxSize(size: Double) {
    maxWidth = size
    maxHeight = size
}

inline fun GridPane.setGaps(gap: Double) {
    hgap = gap
    vgap = gap
}