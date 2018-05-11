package com.hendraanggrian.openpss.controls

import javafx.beans.property.ObjectProperty
import javafx.beans.property.StringProperty
import javafx.scene.Scene
import javafx.scene.control.Tooltip
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import ktfx.beans.binding.`when`
import ktfx.beans.binding.otherwise
import ktfx.beans.binding.then
import ktfx.beans.value.greaterEq
import ktfx.coroutines.FX
import ktfx.coroutines.listener

interface StretchableLabeled {

    companion object {
        private const val STRETCH_POINT = 1200
    }

    fun stretchableTextProperty(): StringProperty
    var stretchableText: String?
        get() = stretchableTextProperty().get()
        set(value) = stretchableTextProperty().set(value)

    fun getScene(): Scene?

    fun textProperty(): StringProperty

    fun tooltipProperty(): ObjectProperty<Tooltip>

    fun initialize(stretchableText: String?) {
        stretchableTextProperty().listener { _, _, value ->
            when (getScene()?.widthProperty() != null) {
                true -> initStretchable(value)
                else -> launch(FX) {
                    while (getScene()?.widthProperty() == null) delay(250)
                    initStretchable(value)
                }
            }
        }
        stretchableTextProperty().set(stretchableText)
    }

    private fun initStretchable(adaptableText: String?) {
        val condition = `when`(getScene()!!.widthProperty() greaterEq STRETCH_POINT)
        textProperty().bind(condition then adaptableText otherwise null as String?)
        tooltipProperty().bind(condition then null as Tooltip? otherwise Tooltip(adaptableText))
    }
}