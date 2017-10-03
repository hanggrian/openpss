@file:JvmName("GridPanesKt")
@file:Suppress("NOTHING_TO_INLINE")

package com.wijayaprinting.javafx.scene.utils

import javafx.scene.layout.GridPane

inline fun GridPane.setGap(gap: Double) {
    hgap = gap
    vgap = gap
}