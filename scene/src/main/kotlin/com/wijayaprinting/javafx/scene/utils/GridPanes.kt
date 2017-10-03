@file:JvmName("GridPanesKt")
@file:Suppress("NOTHING_TO_INLINE")

package com.wijayaprinting.javafx.scene.utils

import javafx.scene.layout.GridPane

inline var GridPane.gaps: Double
    get() {
        check(hgap == vgap)
        return hgap
    }
    set(value) {
        hgap = value
        vgap = value
    }