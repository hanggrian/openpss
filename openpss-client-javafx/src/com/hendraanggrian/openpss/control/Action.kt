package com.hendraanggrian.openpss.control

import com.hendraanggrian.openpss.R
import com.jfoenix.controls.JFXButton
import javafx.beans.DefaultProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.scene.Node
import javafx.scene.control.Tooltip
import javafx.scene.image.ImageView
import ktfx.getValue
import ktfx.setValue
import ktfx.toBinding

@DefaultProperty("graphic")
class Action @JvmOverloads constructor(text: String? = null, graphic: Node? = null) : JFXButton(null, graphic) {

    constructor(text: String? = null, graphicUrl: String? = null) :
        this(text, graphicUrl?.let { ImageView(it) })

    private val tooltipTextProperty = SimpleStringProperty(text)
    fun tooltipTextProperty(): StringProperty = tooltipTextProperty
    var tooltipText: String? by tooltipTextProperty

    init {
        buttonType = ButtonType.FLAT
        styleClass += R.style.flat
        tooltipProperty().bind(tooltipTextProperty.toBinding { it?.let { Tooltip(it) } })
    }
}
