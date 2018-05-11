@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.controls

import javafx.beans.DefaultProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.scene.Node
import javafx.scene.control.ToggleButton

@DefaultProperty("graphic")
class StretchableToggleButton @JvmOverloads constructor(
    stretchableText: String? = null,
    graphic: Node? = null
) : ToggleButton(null, graphic), StretchableLabeled {

    private val stretchableTextProperty = SimpleStringProperty()
    override fun stretchableTextProperty(): StringProperty = stretchableTextProperty

    init {
        initialize(stretchableText)
    }
}