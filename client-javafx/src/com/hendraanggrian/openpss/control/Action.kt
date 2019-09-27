package com.hendraanggrian.openpss.control

import com.hendraanggrian.openpss.R
import com.jfoenix.controls.JFXButton
import javafx.beans.DefaultProperty
import javafx.beans.property.StringProperty
import javafx.scene.Node
import javafx.scene.control.Tooltip
import javafx.scene.image.ImageView
import ktfx.asProperty
import ktfx.bindings.buildBinding
import ktfx.getValue
import ktfx.setValue

@DefaultProperty("graphic")
class Action @JvmOverloads constructor(text: String? = null, graphic: Node? = null) :
    JFXButton(null, graphic) {

    constructor(text: String? = null, graphicUrl: String? = null) :
        this(text, graphicUrl?.let { ImageView(it) })

    private val tooltipTextProperty = text.asProperty()

    fun tooltipTextProperty(): StringProperty = tooltipTextProperty

    var tooltipText: String? by tooltipTextProperty

    init {
        buttonType = ButtonType.FLAT
        styleClass += R.style.flat
        tooltipProperty().bind(buildBinding(tooltipTextProperty) {
            tooltipText?.let { Tooltip(it) }
        })
    }
}
