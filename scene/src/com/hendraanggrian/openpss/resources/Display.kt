package com.hendraanggrian.openpss.resources

import javafx.beans.property.DoubleProperty
import javafx.beans.property.ReadOnlyDoubleWrapper
import javafx.stage.Stage
import ktfx.beans.value.getValue
import ktfx.stage.setMinSize

enum class Display(val widthProperty: DoubleProperty, val heightProperty: DoubleProperty) {
    HQVGA(240.0, 160.0);

    constructor(width: Double, height: Double) : this(ReadOnlyDoubleWrapper(width), ReadOnlyDoubleWrapper(height))

    val width: Double by widthProperty

    val height: Double by heightProperty
}

inline var Stage.minDisplay: Display
    get() = throw UnsupportedOperationException()
    set(value) = setMinSize(value.width, value.height)