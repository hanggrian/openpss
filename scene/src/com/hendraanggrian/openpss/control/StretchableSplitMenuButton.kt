@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.control

import javafx.beans.DefaultProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.scene.Node
import javafx.scene.control.SplitMenuButton

@DefaultProperty("graphic")
class StretchableSplitMenuButton @JvmOverloads constructor(
    stretchPoint: Double = -1.0,
    stretchableText: String? = null,
    graphic: Node? = null
) : SplitMenuButton(), StretchableLabeled {

    private val stretchPointProperty = SimpleDoubleProperty(stretchPoint)
    override fun stretchPointProperty(): DoubleProperty = stretchPointProperty

    private val stretchableTextProperty = SimpleStringProperty(stretchableText)
    override fun stretchableTextProperty(): StringProperty = stretchableTextProperty

    init {
        initialize()
        setGraphic(graphic)
    }
}