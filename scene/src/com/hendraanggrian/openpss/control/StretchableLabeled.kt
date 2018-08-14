package com.hendraanggrian.openpss.control

import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.ReadOnlyDoubleProperty
import javafx.beans.property.StringProperty
import javafx.scene.Scene
import javafx.scene.control.Tooltip
import javafxx.beans.binding.bindingOf
import javafxx.beans.binding.stringBindingOf
import javafxx.coroutines.FX
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch

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
            else -> launch(FX) {
                while (getScene()?.widthProperty() == null) delay(250)
                getScene()!!.widthProperty()()
            }
        }
    }

    private operator fun ReadOnlyDoubleProperty.invoke() {
        textProperty().bind(stringBindingOf(this, stretchPointProperty(), stretchableTextProperty()) {
            when (get() >= stretchPoint) {
                true -> stretchableText
                else -> null
            }
        })
        tooltipProperty().bind(bindingOf(this, stretchPointProperty(), stretchableTextProperty()) {
            when (get() >= stretchPoint) {
                true -> null
                else -> Tooltip(stretchableText)
            }
        })
    }
}