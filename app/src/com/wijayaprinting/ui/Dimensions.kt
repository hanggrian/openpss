@file:Suppress("NOTHING_TO_INLINE")

package com.wijayaprinting.ui

import javafx.scene.layout.GridPane
import javafx.scene.layout.Region

inline fun Region.size(size: Number) {
    minSize(size)
    prefSize(size)
    maxSize(size)
}

inline fun Region.size(width: Number, height: Number) {
    setMinSize(width.toDouble(), height.toDouble())
    setPrefSize(width.toDouble(), height.toDouble())
    setMaxSize(width.toDouble(), height.toDouble())
}

inline fun Region.minSize(size: Number) {
    minWidth = size.toDouble()
    minHeight = size.toDouble()
}

inline fun Region.prefSize(size: Number) {
    prefWidth = size.toDouble()
    prefHeight = size.toDouble()
}

inline fun Region.maxSize(size: Number) {
    maxWidth = size.toDouble()
    maxHeight = size.toDouble()
}

inline fun GridPane.gap(gap: Number) {
    hgap = gap.toDouble()
    vgap = gap.toDouble()
}