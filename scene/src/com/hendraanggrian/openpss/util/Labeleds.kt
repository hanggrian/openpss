@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.util

import javafx.scene.Scene
import javafx.scene.control.Labeled
import javafx.scene.control.Tooltip
import ktfx.beans.binding.`when`
import ktfx.beans.binding.otherwise
import ktfx.beans.binding.then
import ktfx.beans.value.greaterEq

inline fun Labeled.adaptableText(scene: Scene, text: String, triggerPoint: Int = 1366) {
    val condition = `when`(scene.widthProperty() greaterEq triggerPoint)
    textProperty().bind(condition then text otherwise "")
    tooltipProperty().bind(condition then Tooltip(text) otherwise null as Tooltip?)
}