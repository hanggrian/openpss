package com.hendraanggrian.openpss.util

import javafx.scene.control.Labeled
import javafx.scene.control.Tooltip
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import ktfx.beans.binding.`when`
import ktfx.beans.binding.otherwise
import ktfx.beans.binding.then
import ktfx.beans.value.greaterEq
import ktfx.coroutines.FX

fun Labeled.stretchableText(adaptableText: String? = text) {
    when (scene != null && scene.widthProperty() != null) {
        true -> initStretchable(adaptableText)
        else -> launch(FX) {
            while (scene == null || scene.widthProperty() == null) delay(250)
            initStretchable(adaptableText)
        }
    }
}

private fun Labeled.initStretchable(adaptableText: String?) = `when`(scene.widthProperty() greaterEq 1200).let {
    textProperty().bind(it then adaptableText otherwise "")
    tooltipProperty().bind(it then null as Tooltip? otherwise Tooltip(adaptableText))
}