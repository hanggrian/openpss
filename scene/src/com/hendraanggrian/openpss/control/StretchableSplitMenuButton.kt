@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.control

import javafx.beans.DefaultProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.scene.Node
import javafx.scene.control.SplitMenuButton

@DefaultProperty("graphic")
class StretchableSplitMenuButton @JvmOverloads constructor(
    stretchableText: String? = null,
    graphic: Node? = null
) : SplitMenuButton(), StretchableLabeled {

    private val stretchableTextProperty = SimpleStringProperty()
    override fun stretchableTextProperty(): StringProperty = stretchableTextProperty

    init {
        initialize(stretchableText)
        setGraphic(graphic)
    }
}