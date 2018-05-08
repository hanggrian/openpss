package com.hendraanggrian.openpss.util

import com.hendraanggrian.openpss.resources.Display
import javafx.scene.control.Labeled
import javafx.scene.control.Tooltip
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import ktfx.beans.binding.`when`
import ktfx.beans.binding.otherwise
import ktfx.beans.binding.then
import ktfx.beans.value.greaterEq
import ktfx.coroutines.FX

fun Labeled.adaptableText(adaptableText: String = text) {
    when (scene != null && scene.widthProperty() != null) {
        true -> init(adaptableText)
        else -> launch(FX) {
            while (scene == null || scene.widthProperty() == null) delay(250)
            init(adaptableText)
        }
    }
}

private fun Labeled.init(adaptableText: String) {
    val condition = `when`(scene.widthProperty() greaterEq Display.XGA_.width)
    textProperty().bind(condition then adaptableText otherwise "")
    tooltipProperty().bind(condition then null as Tooltip? otherwise Tooltip(adaptableText))
}