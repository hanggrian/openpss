package com.hendraanggrian.openpss.control

import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.ReadOnlyDoubleProperty
import javafx.beans.property.StringProperty
import javafx.scene.Scene
import javafx.scene.control.Tooltip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import ktfx.beans.binding.buildBinding
import ktfx.beans.binding.buildStringBinding

interface StretchableLabeled {

    fun stretchPointProperty(): DoubleProperty
    var stretchPoint: Double
        get() = stretchPointProperty().get()
        set(value) = stretchPointProperty().set(value)

    fun stretchableTextProperty(): StringProperty
    var stretchableText: String?
        get() = stretchableTextProperty().get()
        set(value) = stretchableTextProperty().set(value)

    fun getScene(): Scene?

    fun textProperty(): StringProperty

    fun tooltipProperty(): ObjectProperty<Tooltip>

    fun initialize() {
        when {
            getScene()?.widthProperty() != null -> getScene()!!.widthProperty()()
            else -> GlobalScope.launch(Dispatchers.JavaFx) {
                while (getScene()?.widthProperty() == null) delay(250)
                getScene()!!.widthProperty()()
            }
        }
    }

    private operator fun ReadOnlyDoubleProperty.invoke() {
        textProperty().bind(
            buildStringBinding(this, stretchPointProperty(), stretchableTextProperty()) {
                when (get() >= stretchPoint) {
                    true -> stretchableText
                    else -> null
                }
            }
        )
        tooltipProperty().bind(
            buildBinding(this, stretchPointProperty(), stretchableTextProperty()) {
                when (get() >= stretchPoint) {
                    true -> null
                    else -> Tooltip(stretchableText)
                }
            }
        )
    }
}
