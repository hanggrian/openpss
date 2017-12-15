@file:JvmName("DimensionsKt")
@file:Suppress("NOTHING_TO_INLINE")

package com.wijayaprinting.manager.scene.utils

import javafx.scene.layout.GridPane
import javafx.scene.layout.Region

inline fun Region.setSize(size: Number) {
    setMinSize(size)
    setPrefSize(size)
    setMaxSize(size)
}

inline fun Region.setSize(width: Number, height: Number) {
    setMinSize(width.toDouble(), height.toDouble())
    setPrefSize(width.toDouble(), height.toDouble())
    setMaxSize(width.toDouble(), height.toDouble())
}

inline fun Region.setMinSize(size: Number) {
    minWidth = size.toDouble()
    minHeight = size.toDouble()
}

inline fun Region.setPrefSize(size: Number) {
    prefWidth = size.toDouble()
    prefHeight = size.toDouble()
}

inline fun Region.setMaxSize(size: Number) {
    maxWidth = size.toDouble()
    maxHeight = size.toDouble()
}

inline fun GridPane.setGaps(gap: Number) {
    hgap = gap.toDouble()
    vgap = gap.toDouble()
}